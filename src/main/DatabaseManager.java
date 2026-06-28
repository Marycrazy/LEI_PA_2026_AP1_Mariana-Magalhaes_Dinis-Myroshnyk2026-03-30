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
import main.enums.UserType;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.Equipment;
import main.models.RegistrableUser;
import main.models.Repair;
import main.models.User;
import main.models.Notification;
import main.models.Part;
import main.models.Log;

/**
 * Central access point for all interactions with the SurrealDB database.
 * <p>
 * Implemented as a singleton, accessible via {@link #getInstance()}. Wraps
 * connection management, authentication, and every read/write operation
 * used by the application, including user accounts, repairs, equipment,
 * parts, notifications, and action logs.
 * <p>
 * Multi-statement writes that touch related tables (e.g. creating or
 * updating a {@link User} and its associated {@link RegistrableUser} /
 * {@link Employee} / {@link Client} records) are wrapped in explicit
 * {@link Transaction}s to keep them atomic.
 */
public class DatabaseManager {

    /**
     * Login credentials supplied by a user attempting to sign in.
     *
     * @param username the account username
     * @param password the account password, in plain text as entered by the user
     */
    public record UserCredentials(String username, String password) {
    }

    /**
     * A single argument call to a server-side SurrealDB validation function,
     * used to check whether a given field value satisfies a DB-defined rule
     * (e.g. {@code fn::check_email}, {@code fn::check_phone}).
     *
     * @param dbFunc the fully-qualified name of the SurrealDB function to invoke
     * @param value  the value to validate
     */
    public record DbFunctionCall(String dbFunc, String value) {
    }

    /**
     * Describes a notification to be created and delivered to a target,
     * which may be a specific user's {@link RecordId} or a broad role such
     * as {@code "ADMIN"}.
     *
     * @param content the notification's message text
     * @param target  the recipient: either a {@link RecordId} of a specific
     *                user, or a {@link String} naming a user type/role
     */
    public record NotificationRequest(String content, Object target) {
    }

    private static DatabaseManager instance;
    private Surreal driver;
    private PropertiesManager props;

    private DatabaseManager() {
        this.driver = new Surreal();
        this.props = new PropertiesManager();
    }

    /**
     * Returns the shared singleton instance of the database manager,
     * creating it on first access.
     *
     * @return the singleton {@code DatabaseManager} instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Connects to the SurrealDB server using the connection details stored
     * in the application's properties file, signs in with the configured
     * root credentials, and selects the configured namespace and database.
     */
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

    /**
     * Closes the underlying database connection. Should be called once on
     * application shutdown.
     */
    public void close() {
        driver.close();
    }

    /**
     * Checks whether at least one active administrator account already
     * exists in the database.
     *
     * @return {@code true} if an active admin user exists, {@code false} otherwise
     */
    public boolean hasAdmin() {
        String query = "SELECT count() FROM user WHERE type = 'ADMIN' AND status = 'ACTIVE' GROUP ALL";
        Response response = driver.query(query);
        return response.take(0).getArray().get(0).getObject().get("count").getLong() > 0;
    }

    /**
     * Checks whether the application's connection properties have already
     * been configured (i.e. a previous successful setup has taken place).
     *
     * @return {@code true} if connection properties are present, {@code false} otherwise
     */
    public boolean isConfigured() {
        return props.hasProperties();
    }

    /**
     * Persists a new user to the database. Depending on the runtime type of
     * {@code user}, also creates and links the associated
     * {@code registrable_user} record and, for employees or clients, the
     * corresponding {@code employee}/{@code client} record, via graph
     * relations ({@code is_a}, {@code of_type}). The entire operation runs
     * inside a single transaction and is rolled back on failure.
     *
     * @param user the user to create; may be an {@link Admin}, {@link Employee},
     *             or {@link Client}
     * @throws RuntimeException if any part of the creation fails; the
     *                          transaction is cancelled before the exception propagates
     */
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
                    transaction.query(
                            "RELATE " + regUser.getRegistrableUserId() + " -> of_type -> " + employee.getEmployeeId());
                } else if (regUser instanceof Client client) {
                    query = "CREATE client CONTENT " + toSQL(Client.toMap(client));
                    response = transaction.query(query);
                    client.setClientId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());
                    transaction.query(
                            "RELATE " + regUser.getRegistrableUserId() + " -> of_type -> " + client.getClientId());
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
            if (value instanceof String)
                sb.append("'").append(value).append("', ");
            else if (value instanceof ZonedDateTime) {
                ZonedDateTime utc = ((ZonedDateTime) value).withZoneSameInstant(ZoneOffset.UTC);
                sb.append("d'").append(utc.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).append("', ");
            } else
                sb.append(value).append(", ");
        });
        sb.append("}");
        return sb.toString();
    }

    /**
     * Validates a single field's value against a server-side SurrealDB
     * validation function.
     *
     * @param call the function name and value to validate
     * @return {@code null} if the value is valid, or the database's error
     *         message describing why validation failed
     */
    public String validateField(DbFunctionCall call) {
        try {
            driver.run(call.dbFunc, call.value);
            return null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return e.getMessage();
        }
    }

    /**
     * Checks whether a username/password combination matches an existing
     * user account.
     *
     * @param credentials the username and password to check
     * @return {@code true} if the credentials match a stored account, {@code false} otherwise
     */
    public boolean userExists(UserCredentials credentials) {
        Value result = driver.run("fn::user_exists", credentials.username, credentials.password);
        return result.getBoolean();
    }

    /**
     * Retrieves the account status (e.g. {@code PENDING}, {@code ACTIVE},
     * {@code REJECTED}, {@code INACTIVE}) of the user with the given username.
     *
     * @param username the username to look up
     * @return the user's current status
     */
    public String getUserStatus(String username) {
        String query = "SELECT status FROM user WHERE username = $username";

        Response response = driver.queryBind(query, Map.of("username", username));
        Value result = response.take(0);

        return result.getArray().get(0).getObject().get("status").getString();
    }

    /**
     * Retrieves the account type (e.g. {@code ADMIN}, {@code EMPLOYEE},
     * {@code CLIENT}) of the user with the given username.
     *
     * @param username the username to look up
     * @return the user's account type
     */
    public String getType(String username) {
        Value result = driver.run("fn::get_user_type", username);
        return result.getString();
    }

    /**
     * Fetches a fully-populated user (including registrable-user and
     * type-specific fields, where applicable) by username.
     *
     * @param username the username to look up
     * @return the matching {@link User} (as an {@link Admin}, {@link Employee},
     *         or {@link Client}), or {@code null} if not found or on error
     */
    public User fetchUser(String username) {
        String query = "SELECT *, (->is_a.out.*)[0] AS reg_data, (->is_a.out->of_type.out.*)[0] AS user_data " +
                "FROM user WHERE username = $username";
        try {
            Response response = driver.queryBind(query, Map.of("username", username));
            return userFromValue(response.take(0).getArray().get(0));
        } catch (Exception e) {
            System.err.println("Error fetching user: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates and stores a new notification for the given target.
     *
     * @param request the notification's content and target recipient
     */
    public void sendNotification(NotificationRequest request) {
        Notification note = new Notification(request.content, request.target);
        driver.create("notification", note);
    }

    /**
     * Counts how many notifications addressed to the given user (either
     * directly, by id, or by their role/type) have not yet been marked as
     * viewed.
     *
     * @param user the user to count unread notifications for
     * @return the number of unread notifications
     */
    public long getUnreadNotifications(User user) {
        String query = "SELECT count() FROM notification " +
                "WHERE (target = $id OR target = $type) " +
                "AND id NOTINSIDE (SELECT VALUE out FROM viewed_notification WHERE in = $id) " +
                "GROUP ALL";

        Response response = driver.queryBind(query, Map.of("id", user.getUserId(), "type", user.getType()));
        Value result = response.take(0);

        return result.getArray().get(0).getObject().get("count").getLong();
    }

    /**
     * Retrieves notifications addressed to the given user, ordered from
     * most to least recent.
     *
     * @param user    the user to retrieve notifications for
     * @param listAll if {@code true}, returns all notifications regardless
     *                of read status; if {@code false}, returns only unread ones
     * @return the matching notifications, newest first
     */
    public List<Notification> getNotifications(User user, boolean listAll) {
        String query = "SELECT * FROM notification WHERE (target = $id OR target = $type) ";

        if (!listAll)
            query += "AND id NOTINSIDE (SELECT VALUE out FROM viewed_notification WHERE in = $id) ";
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

    /**
     * Marks a notification as viewed by the given user.
     *
     * @param user         the user who viewed the notification
     * @param notification the notification that was viewed
     */
    public void markAsRead(User user, Notification notification) {
        driver.relate(user.getUserId(), "viewed_notification", notification.getId());
    }

    /**
     * Retrieves system action logs, optionally filtered by the acting
     * user's name, ordered from most to least recent.
     *
     * @param search a case-insensitive substring to match against the
     *               acting user's name, or an empty string to return all logs
     * @return the matching log entries, newest first
     */
    public List<Log> getLogs(String search) {
        String query = "SELECT action, details, created_at, user.name AS userName FROM log";

        if (!search.isEmpty())
            query += " WHERE string::contains(string::lowercase(type::string(user.name)), string::lowercase($search))";

        query += " ORDER BY created_at DESC";

        Response response = search.isEmpty()
                ? driver.query(query)
                : driver.queryBind(query, Map.of("search", search));

        List<Log> logs = new ArrayList<>();
        for (Value element : response.take(0).getArray()) {
            var obj = element.getObject();
            Log log = new Log();

            log.setAction(obj.get("action").getString());
            log.setCreatedAt(obj.get("created_at").getDateTime());
            log.setDetails(obj.get("details").getString());
            log.setUserName(
                    (obj.get("userName") != null && !obj.get("userName").isNone()) ? obj.get("userName").getString()
                            : "Deleted User");

            logs.add(log);
        }
        return logs;
    }

    /**
     * Retrieves the full action history (e.g. submission, status changes)
     * for a specific repair, ordered chronologically from oldest to newest.
     * <p>
     * Matches log entries whose {@code details} text mentions the given
     * repair's record id, since log entries reference repairs by embedding
     * their id directly in the message rather than via a relation.
     *
     * @param repairId the id of the repair to retrieve the history for
     * @return the matching log entries, oldest first
     */
    public List<Log> getLogsForRepair(RecordId repairId) {
        String query = "SELECT action, details, created_at, user.name AS userName FROM log "
                + "WHERE string::contains(details, $repairId) ORDER BY created_at ASC";

        Response response = driver.queryBind(query, Map.of("repairId", repairId.toString()));

        List<Log> logs = new ArrayList<>();
        for (Value element : response.take(0).getArray()) {
            var obj = element.getObject();
            Log log = new Log();

            log.setAction(obj.get("action").getString());
            log.setCreatedAt(obj.get("created_at").getDateTime());
            log.setDetails(obj.get("details").getString());
            log.setUserName(
                (obj.get("userName") != null && !obj.get("userName").isNone()) ? obj.get("userName").getString()
                    : "Deleted User");

            logs.add(log);
        }
        return logs;
    }

    /**
     * Retrieves all users other than {@code currUser}, optionally filtered
     * by a case-insensitive substring match against name or username.
     *
     * @param search    a substring to filter by, or an empty string to return all users
     * @param currUser  the currently logged-in user, excluded from the results
     * @return the matching users (as {@link Admin}, {@link Employee}, or {@link Client})
     */
    public List<User> getUsers(String search, User currUser) {
        String query = "SELECT *, (->is_a.out.*)[0] AS reg_data, (->is_a.out->of_type.out.*)[0] AS user_data " +
                "FROM user WHERE id != $currentId ";

        if (!search.isEmpty())
            query += "AND (string::contains(string::lowercase(name), string::lowercase($search)) " +
                    "OR string::contains(string::lowercase(username), string::lowercase($search))) ";

        Response response = driver.queryBind(query, Map.of(
                "currentId", currUser.getUserId(),
                "search", search));

        List<User> users = new ArrayList<>();
        for (Value element : response.take(0).getArray()) {
            User u = userFromValue(element);
            if (u != null)
                users.add(u);
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
                    .setImage(obj.get("image").getString())
                    .setStatus(UserStatus.valueOf(obj.get("status").getString()).toString())
                    .build();
        }

        var regData = obj.get("reg_data").getObject();
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
                    .setImage(obj.get("image").getString())
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
                .setImage(obj.get("image").getString())
                .setStatus(UserStatus.valueOf(obj.get("status").getString()).toString())
                .build();
    }

    /**
     * Updates the account status of the given user.
     *
     * @param user   the user whose status should be changed
     * @param status the new status value (see {@link UserStatus})
     */
    public void setUserStatus(User user, String status) {
        String query = "UPDATE user SET status = $status WHERE id = $id";
        driver.queryBind(query, Map.of("id", user.getUserId(), "status", status));
    }

    /**
     * Persists changes to an existing user's data, including any
     * type-specific fields (employee/client) and registrable-user fields,
     * within a single transaction.
     * <p>
     * Statements are issued bottom-up (type-specific table first, then
     * {@code registrable_user}, then {@code user} last) to avoid traversing
     * back through a table already accessed earlier in the same
     * transaction, which SurrealDB does not resolve correctly.
     * <p>
     * Each statement's result is explicitly consumed via {@code take(0)} so
     * that per-statement database errors (e.g. failed field validation)
     * are surfaced as exceptions rather than silently ignored.
     *
     * @param user the user with updated field values to persist
     * @throws RuntimeException if any statement fails; the transaction is
     *                          cancelled before the exception propagates
     */
    public void updateUser(User user) {
        Transaction transaction = driver.beginTransaction();

        try {
            Response response;

            if (user instanceof RegistrableUser reg) {
                if (reg instanceof Employee employee) {
                    response = transaction.query(
                        "UPDATE (SELECT VALUE ->is_a.out->of_type.out FROM " + user.getUserId() + ")[0][0] MERGE " + toSQL(Employee.toMap(employee))
                    );
                    response.take(0);
                } else if (reg instanceof Client client) {
                    response = transaction.query(
                        "UPDATE (SELECT VALUE ->is_a.out->of_type.out FROM " + user.getUserId() + ")[0][0] MERGE " + toSQL(Client.toMap(client))
                    );
                    response.take(0);
                }

                response = transaction.query(
                    "UPDATE (SELECT VALUE ->is_a.out FROM " + user.getUserId() + ")[0] MERGE " + toSQL(RegistrableUser.toMap(reg))
                );
                response.take(0);
            }

            response = transaction.query("UPDATE " + user.getUserId() + " MERGE " + toSQL(User.toMap(user)));
            response.take(0);

            transaction.commit();
            System.out.println("Updating user...");
        } catch (Exception e) {
            transaction.cancel();
            System.err.println("Error updating user: " + e.getMessage());
            System.err.println("Transaction rolled back.");
            throw e;
        }
    }

    /**
     * Persists a new part to the database.
     *
     * @param part the part to create
     */
    public void savePart(Part part) {
        driver.create("part", part);
    }

    /**
     * Persists changes to an existing part by merging the given object's
     * fields into the stored record.
     *
     * @param part the part with updated field values
     */
    public void updatePart(Part part) {
        driver.update(part.getId(), UpType.MERGE, part);
    }

    /**
     * Retrieves parts, optionally filtered by a case-insensitive substring
     * match against designation or manufacturer.
     *
     * @param search a substring to filter by, or an empty string to return all parts
     * @return the matching parts
     */
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

    /**
     * Fetches a single part by id.
     *
     * @param id the part's record id
     * @return the matching part
     * @throws java.util.NoSuchElementException if no part with the given id exists
     */
    public Part fetchPart(RecordId id) {
        Optional<Part> part = driver.select(Part.class, id);
        return part.get();
    }

    /**
     * Persists a new piece of equipment and relates it to the client who
     * submitted it, within a single transaction. A unique SKU is generated
     * (regenerated on collision) before saving.
     *
     * @param equipment the equipment to create
     * @param user      the client submitting the equipment
     */
    public void saveEquipment(Equipment equipment, User user) {
        while (!skuExists(equipment.getSku())) {
            equipment.regenerateSku();
        }

        Transaction transaction = driver.beginTransaction();
        try {
            String query = "CREATE equipment CONTENT " + toSQL(Equipment.toMap(equipment));
            Response response = transaction.query(query);
            RecordId equipmentId = response.take(0).getArray().get(0).getObject().get("id").getRecordId();
            equipment.setId(equipmentId);

            RecordId clientId = transaction.query(
                    "(SELECT VALUE ->is_a.out->of_type.out FROM " + user.getUserId() + ")[0][0]").take(0).getArray()
                    .get(0).getRecordId();

            transaction.query("RELATE " + clientId + " -> inserts -> " + equipmentId);

            transaction.commit();
        } catch (Exception e) {
            transaction.cancel();
            System.err.println("Error saving equipment: " + e.getMessage());
            System.err.println("Transaction rolled back.");
        }
    }

    private boolean skuExists(int sku) {
        Value result = driver.run("fn::check_sku", sku);
        return result.getBoolean();
    }

    /**
     * Retrieves the equipment submitted by a given user, optionally
     * filtered by a case-insensitive substring match against brand or
     * model, ordered alphabetically by brand.
     *
     * @param userId the id of the client whose equipment should be retrieved
     * @param search a substring to filter by, or an empty string to return all equipment
     * @return the matching equipment, ordered by brand
     */
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

    /**
     * Creates a new repair request for the given equipment on behalf of the
     * given client, relating both, then notifies all admins of the new
     * request.
     *
     * @param equipment the equipment the repair request is for
     * @param user      the client submitting the request
     * @throws RuntimeException if the transaction fails; it is cancelled
     *                          before the exception propagates
     */
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

    /**
     * Retrieves repairs, scoped to the given user (all repairs for admins,
     * only the user's own repairs otherwise), and optionally filtered by
     * repair code or client name, status, and a start-date range. Each
     * result's client name is resolved and attached afterwards.
     *
     * @param search      a substring to match against repair code or client name
     * @param filterState a specific repair status to filter by, or an empty
     *                    string to include all statuses
     * @param startDate   if non-null, only include repairs starting on or after this date
     * @param endDate     if non-null, only include repairs starting on or before this date
     * @param user        the user requesting the list, used to scope results by role
     * @return the matching repairs, each with its client name populated
     */
    public List<Repair> getRepairs(String search, String filterState, ZonedDateTime startDate, ZonedDateTime endDate, User user) {
        String query = "SELECT * FROM repair WHERE 1 = 1 ";
        Map<String, Object> params = new HashMap<>();

        if (!user.getType().equals("ADMIN"))
            query += " AND id IN (SELECT VALUE out FROM user_repair WHERE in = " + user.getUserId() + ")";

        if (!filterState.isEmpty())
            query += "AND state = '" + filterState + "' ";

        if (!search.isEmpty()) {
            query += "AND (string::contains(string::lowercase(repair_code), string::lowercase($search)) "
                + "OR id IN (SELECT VALUE out FROM user_repair WHERE in.type = 'CLIENT' AND string::contains(string::lowercase(in.name), string::lowercase($search)))) ";
            params.put("search", search);
        }

        if (startDate != null) {
            query += "AND start_date >= $startDate ";
            params.put("startDate", startDate);
        }

        if (endDate != null) {
            query += "AND start_date <= $endDate ";
            params.put("endDate", endDate);
        }

        Response response = params.isEmpty()
                ? driver.query(query)
                : driver.queryBind(query, params);

        List<Repair> list = new ArrayList<>();
        for (Value element : response.take(0).getArray())
            list.add(repairFromValue(element));

        attachClientNames(list);
        return list;
    }

    private void attachClientNames(List<Repair> repairs) {
        for (Repair repair : repairs) {
            String repairId = repair.getId().toString();
            String nameQuery = "SELECT VALUE in.name FROM user_repair WHERE out = " + repairId + " AND in.type = 'CLIENT'";
            Response response = driver.query(nameQuery);

            for (Value element : response.take(0).getArray()) {
                repair.setClientName(element.getString());
                break;
            }
        }
    }

    /**
     * Fetches a single repair by id.
     *
     * @param id the repair's record id
     * @return the matching repair
     */
    public Repair fetchRepair(RecordId id) {
        Response response = driver.queryBind("SELECT * FROM $id", Map.of("id", id));
        return repairFromValue(response.take(0).getArray().get(0));
    }

    /**
     * Updates a repair's state, optionally recording a rejection reason or
     * a final cost, and notifies the relevant party (the client, or all
     * admins if an employee rejected the repair) of the change.
     *
     * @param repair the repair to update
     * @param reason the rejection reason, used only when {@code state} is
     *               {@code REJECTED_BY_ADMIN} or {@code REJECTED_BY_EMPLOYEE};
     *               ignored otherwise
     * @param state  the new repair state (see {@code RepairStatus})
     * @param cost   the final cost to record, or {@code null} if not applicable
     */
    public void updateRepairState(Repair repair, String reason, String state, Double cost) {
        String query = "UPDATE " + repair.getId() + " SET state = '" + state + "'";
        try {
            if (state.equals("REJECTED_BY_ADMIN") || state.equals("REJECTED_BY_EMPLOYEE"))
                query += ", observations = '" + reason + "'";
            else if (cost != null)
                query += ", cost = " + cost.doubleValue();

            driver.query(query);

            String message = messageForStateRepair(repair, state, reason);
            if (message == null)
                return;

            if (state.equals("REJECTED_BY_EMPLOYEE")) {
                sendNotification(new NotificationRequest(message, UserType.ADMIN.toString()));
            } else {
                RecordId clientId = driver.query(
                        "(SELECT VALUE in FROM user_repair WHERE out = " + repair.getId()
                                + " AND in.type = 'CLIENT')[0]")
                        .take(0).getRecordId();
                sendNotification(new NotificationRequest(message, clientId));
            }
        } catch (Exception e) {
            System.err.println("Failed to notify client: " + e.getMessage());
        }
    }

    private String messageForStateRepair(Repair repair, String state, String reason) {
        return switch (state) {
            case "REJECTED_BY_ADMIN" -> "Your repair request " + repair.getRepairCode() + " was rejected by the admin."
                    + (reason.isEmpty() ? "" : " Reason: " + reason);
            case "REJECTED_BY_EMPLOYEE" -> "Your repair request " + repair.getRepairCode()
                    + " was rejected by the assigned employee." + (reason.isEmpty() ? "" : " Reason: " + reason);
            case "COMPLETED" -> "Your repair request " + repair.getRepairCode() + " was marked as completed.";
            default -> null;
        };
    }

    /**
     * Retrieves active employees who are not yet assigned to the given
     * repair, available to be assigned.
     *
     * @param repairId the id of the repair to find available employees for
     * @return the list of eligible employees
     */
    public List<Employee> getAvailableEmployees(RecordId repairId) {
        String query = "SELECT *, (->is_a.out.*)[0] AS reg_data, (->is_a.out->of_type.out.*)[0] AS user_data " +
                "FROM user WHERE type = 'EMPLOYEE' AND status = 'ACTIVE' " +
                "AND id NOTINSIDE (SELECT VALUE in FROM user_repair WHERE out = $repairId)";
        Response response = driver.queryBind(query, Map.of("repairId", repairId));
        List<Employee> list = new ArrayList<>();
        for (Value element : response.take(0).getArray())
            list.add((Employee) userFromValue(element));
        return list;
    }

    /**
     * Assigns an employee to a repair: sets the repair's state to
     * {@code ACCEPTED}, relates the employee to the repair, and notifies
     * the employee of the assignment. Runs within a single transaction.
     *
     * @param repair   the repair to assign
     * @param employee the employee to assign to the repair
     * @throws RuntimeException if the transaction fails; it is cancelled
     *                          before the exception propagates
     */
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
        Value clientName = obj.get("clientName");

        if (repairCode != null && !repairCode.isNone())
            repair.setRepairCode(repairCode.getString());
        repair.setState(state.getString());
        if (observations != null && !observations.isNone())
            repair.setObservations(observations.getString());
        repair.setStartDate(startDate.getDateTime());
        if (endDate != null && !endDate.isNone())
            repair.setEndDate(endDate.getDateTime());
        if (cost != null && !cost.isNull())
            repair.setCost(cost.getDouble());
        if (clientName != null && !clientName.isNone())
            repair.setClientName(clientName.getString());

        return repair;
    }
}