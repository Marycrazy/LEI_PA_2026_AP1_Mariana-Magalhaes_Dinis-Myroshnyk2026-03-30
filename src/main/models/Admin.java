package main.models;

import java.util.HashMap;
import java.util.Map;

import main.enums.UserStatus;
import main.enums.UserType;
import main.utils.Input;

public class Admin extends User {
    private record InputField(String field, String dbFunc) {}

    public Admin() {}

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
            admin.type = UserType.ADMIN.toString();
            admin.status = (this.status != null) ? this.status : UserStatus.ACTIVE.toString();
            return admin;
        }
    }

    public static Admin create() {
        InputField[] fields = {
            new InputField("Name", null),
            new InputField("Username", "fn::check_username"),
            new InputField("Password", null),
            new InputField("Email", "fn::check_email"),
        };

        Map<String, String> inputMap = new HashMap<>();

        for (InputField field : fields) {
            String input = (field.dbFunc() == null)
                ? Input.getInput(field.field())
                : Input.getInput(field.field(), field.dbFunc());
            if (input == null) return null;
            inputMap.put(field.field(), input);
        }

        Admin admin = (Admin) new Admin.Builder()
            .setName(inputMap.get("Name"))
            .setUsername(inputMap.get("Username"))
            .setPassword(inputMap.get("Password"))
            .setEmail(inputMap.get("Email"))
            .build();

        return admin;
    }

    public static Admin edit(Admin admin) {
        InputField[] fields = {
            new InputField("Name", null),
            new InputField("Password", null),
            new InputField("Email", "fn::check_email"),
        };

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("Name", admin.getName());
        inputMap.put("Username", admin.getUsername());
        inputMap.put("Password", admin.getPassword());
        inputMap.put("Email", admin.getEmail());

        for (InputField field : fields) {
            String current = inputMap.get(field.field());
            String input = (field.dbFunc() == null)
                ? Input.getInput(field.field(), current, true)
                : Input.getInput(field.field(), field.dbFunc(), current);
            if (input == null) return null;
            inputMap.put(field.field(), input);
        }

        return (Admin) new Admin.Builder()
            .setId(admin.getUserId())
            .setName(inputMap.get("Name"))
            .setUsername(inputMap.get("Username"))
            .setPassword(inputMap.get("Password"))
            .setEmail(inputMap.get("Email"))
            .setStatus(admin.getStatus())
            .build();
    }
}