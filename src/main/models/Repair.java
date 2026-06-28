package main.models;

import java.time.ZonedDateTime;
import com.surrealdb.RecordId;

/**
 * Tracks an individual technical maintenance job lifecycle, documenting cost metrics,
 * scheduling milestones, and diagnostic details.
 */
public class Repair {
    private RecordId id;
    private String repair_code;
    private String state;
    private String observations;
    private ZonedDateTime start_date;
    private ZonedDateTime end_date;
    private double cost;

    /** Cached representation element used directly for local views; omitted from core database mappings. */
    private String client_name;

    /**
     * Default constructor for instances of Repair jobs.
     */
    public Repair() {}

    public RecordId getId() { return id; }
    public String getRepairCode() { return repair_code; }
    public String getState() { return state; }
    public String getObservations() { return observations; }
    public ZonedDateTime getStartDate() { return start_date; }
    public ZonedDateTime getEndDate() { return end_date; }
    public double getCost() { return cost; }
    public String getClientName() { return client_name; }

    public void setId(RecordId id) { this.id = id; }
    public void setRepairCode(String code) { this.repair_code = code; }
    public void setState(String state) { this.state = state; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setStartDate(ZonedDateTime start_date) { this.start_date = start_date; }
    public void setEndDate(ZonedDateTime end_date) { this.end_date = end_date; }
    public void setCost(double cost) { this.cost = cost; }
    public void setClientName(String client_name) { this.client_name = client_name; }
}