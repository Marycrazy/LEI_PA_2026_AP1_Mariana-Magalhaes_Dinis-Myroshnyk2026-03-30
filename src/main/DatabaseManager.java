package main;

import java.util.Map;

import com.surrealdb.Response;
import com.surrealdb.Surreal;
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
        String query = "CREATE user CONTENT $data";
        Response response = driver.queryBind(query, Map.of("data", User.toMap(user)));
        user.setUserId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());

        if (user instanceof Admin) { return; }
        if (user instanceof RegistrableUser) {
            RegistrableUser regUser = (RegistrableUser) user;

            query = "CREATE registrable_user CONTENT $data";
            response = driver.queryBind(query, Map.of("data", RegistrableUser.toMap(regUser)));
            regUser.setRegistrableUserId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());
            driver.relate(user.getUserId(), "is_a", regUser.getRegistrableUserId());

            String table = (regUser instanceof Employee) ? "employee" : "client";
            query = "CREATE " + table + " CONTENT $data";

            if (regUser instanceof Employee employee) {
                response = driver.queryBind(query, Map.of("data", Employee.toMap(employee)));
                employee.setEmployeeId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());
                driver.relate(regUser.getRegistrableUserId(), "of_type", employee.getEmployeeId());
            } else if (regUser instanceof Client client) {
                response = driver.queryBind(query, Map.of("data", Client.toMap(client)));
                client.setClientId(response.take(0).getArray().get(0).getObject().get("id").getRecordId());
                driver.relate(regUser.getRegistrableUserId(), "of_type", client.getClientId());
            }
        }
    }

    // currently for testing purposes. makes no sense fetching a user from the database while having the object in memory.
    public User fetchUser(User user) {
        String query = "SELECT *, (->is_a.out.*)[0] AS reg_data, (->is_a.out->of_type.out.*)[0] AS user_data FROM type::record($id)";

        try {
            Response response = driver.queryBind(query, Map.of("id", user.getUserId()));
            var objData = response.take(0).getArray().get(0).getObject();
            var regData = objData.get("reg_data").getObject();
            var userData = objData.get("user_data").getArray().get(0).getObject();
            String userType = objData.get("type").getString();

            if (userType.equals("EMPLOYEE")) {
                return new Employee.Builder()
                    .setSpecialization(userData.get("specialization").getString())
                    .setStartDate(userData.get("start_date").getDateTime())
                    .setNif(regData.get("nif").getString())
                    .setPhone(regData.get("phone").getString())
                    .setAddress(regData.get("address").getString())
                    .setName(objData.get("name").getString())
                    .setUsername(objData.get("username").getString())
                    .setPassword(objData.get("password").getString())
                    .setEmail(objData.get("email").getString())
                    .setStatus(objData.get("status").getString())
                    .build();
            } else if (userType.equals("CLIENT")) {
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
                    .setStatus(objData.get("status").getString())
                    .build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching user: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}