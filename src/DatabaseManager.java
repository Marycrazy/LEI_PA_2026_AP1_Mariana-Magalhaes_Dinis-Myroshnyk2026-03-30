import com.surrealdb.Response;
import com.surrealdb.Surreal;
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
        Value result = response.take(0);
        return result.getArray().get(0).getObject().get("count").getLong() > 0;
    }

    public void createUser(User user) {
        driver.create(User.class, "user", user);

        if (user instanceof Admin) { return; }
        if (user instanceof RegistrableUser) {
            RegistrableUser regUser = (RegistrableUser) user;

            if (regUser instanceof Employee) {
                Employee employee = (Employee) regUser;
                driver.create(Employee.class, "employee", employee);
                driver.relate(regUser.getUserId(), "is_a", employee.getEmployeeId());
            }
            if (regUser instanceof Client) {
                Client client = (Client) regUser;
                driver.create(Client.class, "client", client);
                driver.relate(regUser.getUserId(), "is_a", client.getClientId());
            }
        }
    }
}