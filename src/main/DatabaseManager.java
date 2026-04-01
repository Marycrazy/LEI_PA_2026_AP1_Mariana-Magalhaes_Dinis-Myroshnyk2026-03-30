package main;

import java.time.ZonedDateTime;
import java.util.Map;

import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Transaction;
import com.surrealdb.Value;
import com.surrealdb.signin.RootCredential;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Surreal driver;

    private DatabaseManager() { this.driver = new Surreal(); }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void connect() {
        driver.connect("wss://surreal.nixvps.dev");
        driver.signin(new RootCredential("root", "toor"));
        driver.useNs("dev").useDb("pa");
    }

    public void close() { driver.close(); }

    public boolean hasAdmin() {
        String query = "SELECT count() FROM user WHERE type = 'ADMIN' AND status = 'ACTIVE' GROUP ALL";
        Response response = driver.query(query);
        return response.take(0).getArray().get(0).getObject().get("count").getLong() > 0;
    }

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

    public boolean validateField(String dbFunc,  String value) {
        try {
            driver.run(dbFunc, value);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean userExists(String username, String password) {
        Value result = driver.run("fn::user_exists", username, password);
        return result.getBoolean();
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
                    .setName(objData.get("name").getString())
                    .setUsername(objData.get("username").getString())
                    .setPassword(objData.get("password").getString())
                    .setEmail(objData.get("email").getString())
                    .build();
            }
        } catch (Exception e) { System.err.println("Error fetching user: " + e.getMessage()); }
        return null;
    }
}