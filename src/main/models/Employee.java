package main.models;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.surrealdb.RecordId;

import main.enums.UserStatus;
import main.enums.UserType;
import main.utils.Input;

public  class Employee extends RegistrableUser {
    private RecordId id;
    private String specialization;
    private ZonedDateTime start_date;

    private record InputField(String field, String dbFunc) {}

    public Employee() {}

    public static class Builder extends RegistrableUser.Builder<Builder> {
        private String specialization;
        private ZonedDateTime start_date;

        public Builder setSpecialization(String specialization) { this.specialization = specialization; return this; }
        public Builder setStartDate(ZonedDateTime start_date) { this.start_date = start_date; return this; }

        @Override
        protected Builder self() { return this; }

        @Override
        public Employee build() {
            Employee employee = new Employee();
            employee.setUserId(id);
            employee.name = this.name;
            employee.username = this.username;
            employee.password = this.password;
            employee.email = this.email;
            employee.type = UserType.EMPLOYEE.toString();
            employee.status = (this.status != null) ? this.status : UserStatus.PENDING.toString();
            employee.address = this.address;
            employee.nif = this.nif;
            employee.phone = this.phone;
            employee.specialization = this.specialization;
            employee.start_date = (this.start_date != null) ? this.start_date : null;
            return employee;
        }
    }

    public RecordId getEmployeeId() { return id; }
    public String getSpecialization() { return specialization; }
    public ZonedDateTime getStartDate() { return start_date; }

    public void setEmployeeId(RecordId id) { this.id = id; }

    public static Map<String, Object> toMap(Employee employee) {
        return Map.of(
            "specialization", employee.getSpecialization()
        );
    }

    public static Employee create() {
        InputField[] fields = {
            new InputField("Name", null),
            new InputField("Username", "fn::check_username"),
            new InputField("Password", null),
            new InputField("Email", "fn::check_email"),
            new InputField("NIF", "fn::check_nif"),
            new InputField("Phone", "fn::check_phone"),
            new InputField("Address", null),
            new InputField("Specialization", "fn::check_specialization"),
        };

        Map<String, String> inputMap = new HashMap<>();

        for (InputField field : fields) {
            String input = (field.dbFunc() == null)
                ? Input.getInput(field.field())
                : Input.getInput(field.field(), field.dbFunc());
            if (input == null) return null;
            inputMap.put(field.field(), input);
        }

        Employee employee = (Employee) new Employee.Builder()
            .setSpecialization(inputMap.get("Specialization"))
            .setNif(inputMap.get("NIF"))
            .setPhone(inputMap.get("Phone"))
            .setAddress(inputMap.get("Address"))
            .setName(inputMap.get("Name"))
            .setUsername(inputMap.get("Username"))
            .setPassword(inputMap.get("Password"))
            .setEmail(inputMap.get("Email"))
            .build();

        return employee;
    }
}