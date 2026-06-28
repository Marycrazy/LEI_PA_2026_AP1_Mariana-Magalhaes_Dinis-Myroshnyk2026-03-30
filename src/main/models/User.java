package main.models;

import java.util.Map;
import com.surrealdb.RecordId;

/**
 * Abstract base class representing a generic user in the system.
 * Provides core fields like credentials, contact info, and status,
 * along with an implementation of the Builder pattern for subclass inheritance.
 */
public abstract class User {
    /** The unique database record identifier for the user. */
    protected RecordId id;
    /** Personal and authentication fields for the user. */
    protected String name, username, password, email, type, status, image;

    /**
     * Abstract builder pattern implementation to support fluent inheritance
     * across different user sub-types.
     *
     * @param <T> the concrete builder subtype
     */
    public abstract static class Builder<T extends Builder<T>> {
        protected RecordId id;
        protected String name, username, password, email, type, status, image;

        public T setId(RecordId id) { this.id = id; return self(); }
        public T setName(String name) { this.name = name; return self(); }
        public T setUsername(String username) { this.username = username; return self(); }
        public T setPassword(String password) { this.password = password; return self(); }
        public T setEmail(String email) { this.email = email; return self(); }
        public T setStatus(String status) { this.status = status; return self(); }
        public T setImage(String image) { this.image = image; return self(); }

        /**
         * Returns the concrete builder instance ('this') to maintain method chaining.
         *
         * @return the concrete builder instance
         */
        protected abstract T self();

        /**
         * Instantiates and builds the concrete User object.
         *
         * @return a constructed instance of a User subclass
         */
        public abstract User build();
    }

    public RecordId getUserId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getImage() { return image; }

    public void setUserId(RecordId id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Converts the core user fields into a map format compatible with SurrealDB payloads.
     *
     * @param user the user instance to map
     * @return a map containing basic user credentials and profile details
     */
    public static Map<String, Object> toMap(User user) {
        return Map.of(
            "name", user.getName(),
            "username", user.getUsername(),
            "password", user.getPassword(),
            "email", user.getEmail(),
            "image", user.getImage(),
            "type", user.getType(),
            "status", user.getStatus()
        );
    }
}