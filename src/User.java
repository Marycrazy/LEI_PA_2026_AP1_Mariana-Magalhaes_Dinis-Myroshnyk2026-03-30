import com.surrealdb.RecordId;

enum UserStatus { ACTIVE, INACTIVE, REJECTED, PENDING }
enum UserType { CLIENT, EMPLOYEE, ADMIN }

public abstract class User {
    protected RecordId userId;
    protected String name;
    protected String username;
    protected String password;
    protected String email;
    protected UserType type;
    protected UserStatus status;

    public RecordId getUserId() { return userId; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public UserType getType() { return type; }
    public UserStatus getStatus() { return status; }

    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setType(UserType type) { this.type = type; }
    public void setStatus(UserStatus status) { this.status = status; }
}