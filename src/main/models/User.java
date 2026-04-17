package main.models;

import java.util.Map;

import com.surrealdb.RecordId;

public abstract class User {
    protected RecordId id;
    protected String name, username, password, email, type, status;

    // builder pattern to shut up CodeScene...
        // https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class
        //https://www.artima.com/weblogs/viewpost.jsp?thread=133275
        // https://www.baeldung.com/java-builder-pattern-inheritance

    public abstract static class Builder<T extends Builder<T>> {
        protected RecordId id;
        protected String name, username, password, email, type, status;

        public Builder<T> setId(RecordId id) { this.id = id; return self(); }
        public Builder<T> setName(String name) { this.name = name; return self(); }
        public Builder<T> setUsername(String username) { this.username = username; return self(); }
        public Builder<T> setPassword(String password) { this.password = password; return self(); }
        public Builder<T> setEmail(String email) { this.email = email; return self(); }
        public Builder<T> setStatus(String status) { this.status = status; return self(); }

        protected abstract T self();
        public abstract User build();
    }

    // getters
    public RecordId getUserId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getType() { return type; }
    public String getStatus() { return status; }

    // setters
    public void setUserId(RecordId id) { this.id = id; }

    // everthing else
    public static Map<String, Object> toMap(User user) {
        return Map.of(
            "name", user.getName(),
            "username", user.getUsername(),
            "password", user.getPassword(),
            "email", user.getEmail(),
            "type", user.getType(),
            "status", user.getStatus()
        );
    }
}