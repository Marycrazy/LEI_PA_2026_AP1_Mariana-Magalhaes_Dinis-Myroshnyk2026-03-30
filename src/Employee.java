import java.time.ZonedDateTime;

import com.surrealdb.RecordId;

@SuppressWarnings("unused") // Temporary, until the model is implemented
public  class Employee extends RegistrableUser {
    private RecordId employeeId;
    private String specialization;
    private ZonedDateTime start_date;

    public Employee() {}

    public RecordId getEmployeeId() { return employeeId; }
}