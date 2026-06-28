package main.models;

import java.time.ZonedDateTime;
import java.util.Map;
import com.surrealdb.RecordId;
import main.enums.UserStatus;
import main.enums.UserType;

/**
 * Represents an employee within the system, expanding on {@link RegistrableUser}
 * with professional details like specialization and start date.
 */
public class Employee extends RegistrableUser {
    /** The specific database record ID for the employee role table. */
    private RecordId id;
    /** The technical or operational focus of the employee. */
    private String specialization;
    /** The date and time when the employee officially started work. */
    private ZonedDateTime start_date;

    /**
     * Constructs a default Employee instance.
     */
    public Employee() {}

    /**
     * Builder pattern implementation for creating structured {@link Employee} instances.
     */
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
            employee.image = this.image;
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

    /**
     * Maps employee-specific fields for SurrealDB operations.
     *
     * @param employee the employee instance
     * @return a map containing the specialization properties
     */
    public static Map<String, Object> toMap(Employee employee) {
        return Map.of(
            "specialization", employee.getSpecialization()
        );
    }
}