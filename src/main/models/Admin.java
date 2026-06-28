package main.models;

import main.enums.UserStatus;
import main.enums.UserType;

/**
 * Represents an administrative user with elevated system privileges.
 * Inherits directly from the base {@link User} class.
 */
public class Admin extends User {

    /**
     * Constructs a default Admin instance.
     */
    public Admin() {}

    /**
     * Builder implementation dedicated to constructing {@link Admin} instances.
     */
    public static class Builder extends User.Builder<Builder> {
        @Override
        protected Builder self() { return this; }

        @Override
        public Admin build() {
            Admin admin = new Admin();
            admin.setUserId(id);
            admin.name = this.name;
            admin.username = this.username;
            admin.password = this.password;
            admin.email = this.email;
            admin.image = this.image;
            admin.type = UserType.ADMIN.toString();
            admin.status = (this.status != null) ? this.status : UserStatus.ACTIVE.toString();
            return admin;
        }
    }
}