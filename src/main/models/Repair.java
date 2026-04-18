package main.models;

import java.time.ZonedDateTime;
import com.surrealdb.RecordId;

public class Repair {
    private RecordId id;
    private String repair_code;
    private String state;
    private String observations;
    private ZonedDateTime start_date;
    private ZonedDateTime end_date;
    private double cost;

    public Repair() {}

    public RecordId getId() { return id; }
    public String getRepairCode() { return repair_code; }
    public String getState() { return state; }
    public String getObservations() { return observations; }
    public ZonedDateTime getStartDate() { return start_date; }
    public ZonedDateTime getEndDate() { return end_date; }
    public double getCost() { return cost; }

    public void setId(RecordId id) { this.id = id; }
    public void setRepairCode(String code) { this.repair_code = code; }
    public void setState(String state) { this.state = state; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setStartDate(ZonedDateTime start_date) { this.start_date = start_date; }
    public void setEndDate(ZonedDateTime end_date) { this.end_date = end_date; }
    public void setCost(double cost) { this.cost = cost; }
}