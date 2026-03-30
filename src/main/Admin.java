package main;

public class Admin extends User {
    public Admin() {}

    public static class Builder extends User.Builder<Builder> {
        @Override
        protected Builder self() { return this; }

        @Override
        public Admin build() {
            Admin admin = new Admin();
            admin.name = this.name;
            admin.username = this.username;
            admin.password = this.password;
            admin.email = this.email;
            admin.type = UserType.ADMIN.toString();
            admin.status = (this.status != null) ? this.status : UserStatus.ACTIVE.toString();
            return admin;
        }
    }
}