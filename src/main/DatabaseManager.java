package main;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Transaction;
import com.surrealdb.UpType;
import com.surrealdb.Value;
import com.surrealdb.signin.RootCredential;

import main.enums.UserStatus;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.Equipment;
import main.models.RegistrableUser;
import main.models.Repair;
import main.models.User;
import main.models.Notification;
import main.models.Part;

public class DatabaseManager {
    public record UserCredentials(String username, String password) {}
    public record DbFunctionCall(String dbFunc, String value) {}
    public record NotificationRequest(String content, Object target) {}

    private static DatabaseManager instance;
    private Surreal driver;
    private PropertiesManager props;

    private DatabaseManager() {
        this.driver = new Surreal();
        this.props = new PropertiesManager();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void connect() {
        String connect = props.getProperty("connect");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        String namespace = props.getProperty("namespace");
        String database = props.getProperty("database");

        driver.connect(connect);
        driver.signin(new RootCredential(username, password));
        driver.useNs(namespace).useDb(database);
    }

    public void close() { driver.close(); }

    public boolean hasAdmin() {
        String query = "SELECT count() FROM user WHERE type = 'ADMIN' AND status = 'ACTIVE' GROUP ALL";
        Response response = driver.query(query);
        return response.take(0).getArray().get(0).getObject().get("count").getLong() > 0;
    }

    public boolean isConfigured() { return props.hasProperties(); }

    public void saveUser(User user) {
        Transaction transaction = driver.beginTransaction();

        try {
            String query = "CREATE user CONTENT " + toSQL(User.toMap(user));
            Response response = transaction.query(query);
            user.setUserId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());

            if (user instanceof RegistrableUser) {
                RegistrableUser regUser = (RegistrableUser) user;

                query = "CREATE registrable_user CONTENT " + toSQL(RegistrableUser.toMap(regUser));
                response = transaction.query(query);
                regUser.setRegistrableUserId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());
                transaction.query("RELATE " + user.getUserId() + " -> is_a -> " + regUser.getRegistrableUserId());

                if (regUser instanceof Employee employee) {
                    query = "CREATE employee CONTENT " + toSQL(Employee.toMap(employee));
                    response = transaction.query(query);
                    employee.setEmployeeId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());
                    transaction.query("RELATE " + regUser.getRegistrableUserId() + " -> of_type -> " + employee.getEmployeeId());
                } else if (regUser instanceof Client client) {
                    query = "CREATE client CONTENT " + toSQL(Client.toMap(client));
                    response = transaction.query(query);
                    client.setClientId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());
                    transaction.query("RELATE " + regUser.getRegistrableUserId() + " -> of_type -> " + client.getClientId());
                }
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.cancel();
            System.err.println("Error saving user: " + e.getMessage());
            System.err.println("Transaction rolled back.");
            throw e; // TODO: treat this exception properly somewhere
        }
    }

    private String toSQL(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        map.forEach((key, value) -> {
            sb.append(key).append(": ");
            if (value instanceof String) sb.append("'").append(value).append("', ");
            else if (value instanceof ZonedDateTime) {
                ZonedDateTime utc = ((ZonedDateTime) value).withZoneSameInstant(ZoneOffset.UTC);
                sb.append("d'").append(utc.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).append("', ");
            }
            else sb.append(value).append(", ");
        });
        sb.append("}");
        return sb.toString();
    }

    public boolean validateField(DbFunctionCall call) {
        try {
            driver.run(call.dbFunc, call.value);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean userExists(UserCredentials credentials) {
        Value result = driver.run("fn::user_exists", credentials.username, credentials.password);
        return result.getBoolean();
    }

    public String getUserStatus(String username) {
        String query = "SELECT status FROM user WHERE username = $username";

        Response response = driver.queryBind(query, Map.of("username", username));
        Value result = response.take(0);

        return result.getArray().get(0).getObject().get("status").getString();
    }

    public String getType(String username) {
        Value result = driver.run("fn::get_user_type", username);
        return result.getString();
    }

    public User fetchUser(String username) {
        String query =  "SELECT *, (->is_a.out.*)[0] AS reg_data, (->is_a.out->of_type.out.*)[0] AS user_data " +
                        "FROM user WHERE username = $username";
        try {
            Response response = driver.queryBind(query, Map.of("username", username));
            return userFromValue(response.take(0).getArray().get(0));
        } catch (Exception e) {
            System.err.println("Error fetching user: " + e.getMessage());
            return null;
        }
    }

    public void sendNotification(NotificationRequest request) {
        Notification note = new Notification(request.content, request.target);
        driver.create("notification", note);
    }

    public long getUnreadNotifications(User user) {
        String query =  "SELECT count() FROM notification " +
                        "WHERE (target = $id OR target = $type) " +
                            "AND id NOTINSIDE (SELECT VALUE out FROM viewed_notification WHERE in = $id) " +
                        "GROUP ALL";

        Response response = driver.queryBind(query, Map.of("id", user.getUserId(), "type", user.getType()));
        Value result = response.take(0);

        return result.getArray().get(0).getObject().get("count").getLong();
    }

    public List<Notification> getNotifications(User user, boolean listAll) {
        String query = "SELECT * FROM notification WHERE (target = $id OR target = $type) ";

        if (!listAll) query += "AND id NOTINSIDE (SELECT VALUE out FROM viewed_notification WHERE in = $id) ";
        query += "ORDER BY created_at DESC";

        Response response = driver.queryBind(query, Map.of("id", user.getUserId(), "type", user.getType()));
        Value result = response.take(0);

        List<Notification> notificationList = new ArrayList<Notification>();
        for (Value element : result.getArray()) {
            Notification notification = element.get(Notification.class);
            notificationList.add(notification);
        }

        return notificationList;
    }

    public void markAsRead(User user, Notification notification) {
        driver.relate(user.getUserId(), "viewed_notification", notification.getId());
    }

    public List<User> getUsers(String search, String sortBy, boolean asc, User currUser) {
        String query =  "SELECT *, (->is_a.out.*)[0] AS reg_data, (->is_a.out->of_type.out.*)[0] AS user_data " +
                        "FROM user WHERE id != $currentId ";

        if (!search.isEmpty()) query += "AND (string::contains(string::lowercase(name), string::lowercase($search)) " +
                                        "OR string::contains(string::lowercase(username), string::lowercase($search))) ";

        query += "ORDER BY " + sortBy + " " + (asc ? "ASC" : "DESC");

        Response response = driver.queryBind(query, Map.of(
            "currentId", currUser.getUserId(),
            "search", search
        ));

        List<User> users = new ArrayList<>();
        for (Value element : response.take(0).getArray()) {
            User u = userFromValue(element);
            if (u != null) users.add(u);
        }
        return users;
    }

    private User userFromValue(Value element) {
        var obj = element.getObject();
        String type = obj.get("type").getString();

        if (type.equals("ADMIN")) {
            return new Admin.Builder()
                .setId(obj.get("id").getRecordId())
                .setName(obj.get("name").getString())
                .setUsername(obj.get("username").getString())
                .setPassword(obj.get("password").getString())
                .setEmail(obj.get("email").getString())
                .setStatus(UserStatus.valueOf(obj.get("status").getString()).toString())
                .build();
        }

        var regData  = obj.get("reg_data").getObject();
        var userData = obj.get("user_data").getArray().get(0).getObject();

        if (type.equals("EMPLOYEE")) {
            return new Employee.Builder()
                .setSpecialization(userData.get("specialization").getString())
                .setStartDate(userData.get("start_date").getDateTime())
                .setNif(regData.get("nif").getString())
                .setPhone(regData.get("phone").getString())
                .setAddress(regData.get("address").getString())
                .setId(obj.get("id").getRecordId())
                .setName(obj.get("name").getString())
                .setUsername(obj.get("username").getString())
                .setPassword(obj.get("password").getString())
                .setEmail(obj.get("email").getString())
                .setStatus(UserStatus.valueOf(obj.get("status").getString()).toString())
                .build();
        }

        return new Client.Builder()
            .setScale(userData.get("scale").getString())
            .setSector(userData.get("sector").getString())
            .setNif(regData.get("nif").getString())
            .setPhone(regData.get("phone").getString())
            .setAddress(regData.get("address").getString())
            .setId(obj.get("id").getRecordId())
            .setName(obj.get("name").getString())
            .setUsername(obj.get("username").getString())
            .setPassword(obj.get("password").getString())
            .setEmail(obj.get("email").getString())
            .setStatus(UserStatus.valueOf(obj.get("status").getString()).toString())
            .build();
    }

    public void setUserStatus(User user, String status) {
        String query = "UPDATE user SET status = $status WHERE id = $id";
        driver.queryBind(query, Map.of("id", user.getUserId(), "status", status));
    }

    public void updateUser(User user) {
        Transaction transaction = driver.beginTransaction();

        try {
            transaction.query("UPDATE " + user.getUserId() + " MERGE   " + toSQL(User.toMap(user)));

            if (user instanceof RegistrableUser reg) {
                transaction.query(
                    "UPDATE (SELECT VALUE ->is_a.out FROM " + user.getUserId() + ")[0] MERGE " + toSQL(RegistrableUser.toMap(reg))
                );

                if (reg instanceof Employee employee) {
                    transaction.query(
                        "UPDATE (SELECT VALUE ->is_a.out->of_type.out FROM " + user.getUserId() + ")[0][0] MERGE " + toSQL(Employee.toMap(employee))
                    );
                } else if (reg instanceof Client client) {
                    transaction.query(
                        "UPDATE (SELECT VALUE ->is_a.out->of_type.out FROM " + user.getUserId() + ")[0][0] MERGE " + toSQL(Client.toMap(client))
                    );
                }
            }

            transaction.commit();
            System.out.println("Updating user...");
        } catch (Exception e) {
            transaction.cancel();
            System.err.println("Error updating user: " + e.getMessage());
            System.err.println("Transaction rolled back.");
            throw e;
        }
    }

    public void savePart(Part part) {
        driver.create("part", part);
    }

    public void updatePart(Part part) {
        driver.update(part.getId(), UpType.MERGE, part);
    }

    public List<Part> getParts(String search) {
        String query = "SELECT * FROM part";
        if (!search.isEmpty())
            query += " WHERE string::contains(string::lowercase(designation), string::lowercase($search))" +
                    " OR string::contains(string::lowercase(manufacturer), string::lowercase($search))";

        Response response = search.isEmpty()
            ? driver.query(query)
            : driver.queryBind(query, Map.of("search", search));

        List<Part> parts = new ArrayList<>();
        for (Value element : response.take(0).getArray()) {
            Part part = element.get(Part.class);
            parts.add(part);
        }
        return parts;
    }

    public Part fetchPart(RecordId id) {
        Optional<Part> part = driver.select(Part.class, id);
        return part.get();
    }

    public void saveEquipment(Equipment equipment, User user) {
        while (!skuExists(equipment.getSku())) {
            equipment.regenerateSku();
        }

        Transaction transaction = driver.beginTransaction();
        try {
            String query = "CREATE equipment CONTENT " + toSQL(Equipment.toMap(equipment));
            Response response = transaction.query(query);
            RecordId equipmentId = response.take(0).getArray().get(0).getObject().get("id").getRecordId(); equipment.setId(equipmentId);

            RecordId clientId = transaction.query(
                "(SELECT VALUE ->is_a.out->of_type.out FROM " + user.getUserId() + ")[0][0]"
            ).take(0).getArray().get(0).getRecordId();

            transaction.query("RELATE " + clientId + " -> inserts -> " + equipmentId);

            transaction.commit();
        }
        catch (Exception e) {
            transaction.cancel();
            System.err.println("Error saving equipment: " + e.getMessage());
            System.err.println("Transaction rolled back.");
        }
    }

    private boolean skuExists(int sku) {
        Value result = driver.run("fn::check_sku", sku);
        return result.getBoolean();
    }

    public List<Equipment> getEquipment(RecordId userId, String search) {
        String query = "SELECT * FROM (SELECT VALUE ->is_a.out->of_type.out->inserts.out FROM " + userId + ")[0][0][0]";

        if (!search.isEmpty())
            query += " WHERE string::contains(string::lowercase(brand), string::lowercase($search))" +
                    " OR string::contains(string::lowercase(model), string::lowercase($search))";

        query += " ORDER BY brand ASC";

        Response response = search.isEmpty()
            ? driver.query(query)
            : driver.queryBind(query, Map.of("search", search));

        List<Equipment> list = new ArrayList<>();
        for (Value element : response.take(0).getArray())
            list.add(element.get(Equipment.class));
        return list;
    }

    //------------------- TODO: --------------------

    public void saveRepair(Equipment equipment, User user) {
        Transaction transaction = driver.beginTransaction();

        try {
            String query = "CREATE repair";
            Response response = transaction.query(query);
            RecordId repairId = response.take(0).getArray().get(0).getObject().get("id").getRecordId();

            transaction.query("RELATE " + equipment.getId() + " -> contains -> " + repairId);
            transaction.query("RELATE " + user.getUserId() + " -> user_repair -> " + repairId);

            transaction.commit();

            sendNotification(new NotificationRequest(
                "New repair request submitted by " + user.getName(), "ADMIN"));

        } catch (Exception e) {
            transaction.cancel();
            System.err.println("Error saving repair: " + e.getMessage());
            System.err.println("Transaction rolled back.");
            throw e;
        }
    }

    public List<Repair> getRepairs(String search, String filterState, boolean asc) {
        String query = "SELECT * FROM repair WHERE 1 = 1 ";
        if (!filterState.isEmpty()) query += "AND state = '" + filterState + "' ";
        if (!search.isEmpty())
            query += "AND (repair_code ~ $search OR (<-requested.in.name)[0] ~ $search) ";
        query += "ORDER BY start_date " + (asc ? "ASC" : "DESC");

        Response response = search.isEmpty()
            ? driver.query(query)
            : driver.queryBind(query, Map.of("search", search));

        List<Repair> list = new ArrayList<>();
        for (Value element : response.take(0).getArray())
            list.add(repairFromValue(element));
        return list;
    }

    public Repair fetchRepair(RecordId id) {
        Response response = driver.queryBind("SELECT * FROM $id", Map.of("id", id));
        return repairFromValue(response.take(0).getArray().get(0));
    }

    public void rejectRepair(Repair repair, String reason) {
        Transaction transaction = driver.beginTransaction();
        try {
            transaction.query("UPDATE " + repair.getId() + " SET state = 'REJECTED'" +
                (reason.isEmpty() ? "" : ", observations = '" + reason + "'"));
            transaction.commit();
        } catch (Exception e) {
            transaction.cancel();
            System.err.println("Error rejecting repair: " + e.getMessage());
            throw e;
        }

        try {
            RecordId clientId = driver.queryBind(
                "(SELECT VALUE <-requested.in FROM repair WHERE id = $id)[0][0]",
                Map.of("id", repair.getId())
            ).take(0).getArray().get(0).getRecordId();

            sendNotification(new NotificationRequest(
                "Your repair request " + repair.getRepairCode() + " was rejected." +
                (reason.isEmpty() ? "" : " Reason: " + reason),
                clientId));
        } catch (Exception e) {
            System.err.println("Failed to notify client: " + e.getMessage());
        }
    }

    public void updateRepairState(RecordId repairId, String state) {
        driver.query("UPDATE " + repairId + " SET state = '" + state + "'");
    }

    public List<Employee> getAvailableEmployees(RecordId repairId) {
        String query = "SELECT * FROM user WHERE type = 'EMPLOYEE' AND status = 'ACTIVE' " +
                    "AND id NOTINSIDE (SELECT VALUE in FROM rejected_repair WHERE out = $repairId)";
        Response response = driver.queryBind(query, Map.of("repairId", repairId));
        List<Employee> list = new ArrayList<>();
        for (Value element : response.take(0).getArray())
            list.add((Employee) userFromValue(element));
        return list;
    }

    public void assignEmployee(Repair repair, Employee employee) {
        Transaction transaction = driver.beginTransaction();
        try {
            transaction.query("UPDATE " + repair.getId() + " SET state = 'ACCEPTED'");
            transaction.query("RELATE " + employee.getUserId() + " -> user_repair -> " + repair.getId());
            transaction.commit();

            sendNotification(new NotificationRequest(
                "You have been assigned repair " + repair.getRepairCode(),
                employee.getUserId()));
        } catch (Exception e) {
            transaction.cancel();
            System.err.println("Error assigning employee: " + e.getMessage());
            throw e;
        }
    }

    private Repair repairFromValue(Value element) {
        var obj = element.getObject();
        Repair repair = new Repair();
        repair.setId(obj.get("id").getRecordId());

        Value repairCode = obj.get("repair_code");
        Value state = obj.get("state");
        Value observations = obj.get("observations");
        Value startDate = obj.get("start_date");
        Value endDate = obj.get("end_date");
        Value cost = obj.get("cost");

        if (repairCode != null && !repairCode.isNone()) repair.setRepairCode(repairCode.getString());
        repair.setState(state.getString());
        if (observations != null && !observations.isNone()) repair.setObservations(observations.getString());
        repair.setStartDate(startDate.getDateTime());
        if (endDate != null && !endDate.isNone()) repair.setEndDate(endDate.getDateTime());
        if (cost != null && !cost.isNull()) repair.setCost(cost.getDouble());

        return repair;
    }
}