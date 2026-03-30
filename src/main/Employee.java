package main;

import java.time.ZonedDateTime;
import java.util.Map;

import com.surrealdb.RecordId;

public  class Employee extends RegistrableUser {
    private RecordId id;
    private String specialization;
    private ZonedDateTime start_date;

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
            employee.start_date = this.start_date;
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
            // "start_date", start_date //database already generates this value
        );
    }
}