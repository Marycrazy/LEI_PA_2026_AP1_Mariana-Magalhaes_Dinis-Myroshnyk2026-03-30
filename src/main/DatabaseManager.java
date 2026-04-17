package main;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Transaction;
import com.surrealdb.Value;
import com.surrealdb.signin.RootCredential;

import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.RegistrableUser;
import main.models.User;
import main.models.Notification;

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
            else if (value instanceof ZonedDateTime) sb.append("d'").append(value.toString()).append("', ");
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
        String query = "SELECT *, (->is_a.out.*)[0] AS reg_data, (->is_a.out->of_type.out.*)[0] AS user_data FROM user WHERE username = $username";

        try {
            Response response = driver.queryBind(query, Map.of("username", username));
            var objData = response.take(0).getArray().get(0).getObject();
            String userType = objData.get("type").getString();

            if (userType.equals("ADMIN")) {
                return new Admin.Builder()
                    .setId(objData.get("id").getRecordId())
                    .setName(objData.get("name").getString())
                    .setUsername(objData.get("username").getString())
                    .setPassword(objData.get("password").getString())
                    .setEmail(objData.get("email").getString())
                    .build();
            }

            var regData = objData.get("reg_data").getObject();
            var userData = objData.get("user_data").getArray().get(0).getObject();

            if (userType.equals("EMPLOYEE")) {
                return new Employee.Builder()
                    .setSpecialization(userData.get("specialization").getString())
                    .setNif(regData.get("nif").getString())
                    .setPhone(regData.get("phone").getString())
                    .setAddress(regData.get("address").getString())
                    .setId(objData.get("id").getRecordId())
                    .setName(objData.get("name").getString())
                    .setUsername(objData.get("username").getString())
                    .setPassword(objData.get("password").getString())
                    .setEmail(objData.get("email").getString())
                    .build();
            }
            else if (userType.equals("CLIENT")) {
                return new Client.Builder()
                    .setScale(userData.get("scale").getString())
                    .setSector(userData.get("sector").getString())
                    .setNif(regData.get("nif").getString())
                    .setPhone(regData.get("phone").getString())
                    .setAddress(regData.get("address").getString())
                    .setId(objData.get("id").getRecordId())
                    .setName(objData.get("name").getString())
                    .setUsername(objData.get("username").getString())
                    .setPassword(objData.get("password").getString())
                    .setEmail(objData.get("email").getString())
                    .build();
            }
        } catch (Exception e) { System.err.println("Error fetching user: " + e.getMessage()); }
        return null;
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
}