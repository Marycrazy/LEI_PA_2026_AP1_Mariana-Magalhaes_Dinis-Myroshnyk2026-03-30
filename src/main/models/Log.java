package main.models;

import java.time.ZonedDateTime;

import com.surrealdb.RecordId;

public class Log {
    private RecordId id;
    private String action;
    private ZonedDateTime created_at;
    private String details;
    private String user_name;

    public RecordId getId() {return id;}
    public String getAction() {return action;}
    public ZonedDateTime getCreatedAt() {return created_at;}
    public String getDetails() {return details;}
    public String getUserName() {return user_name;}

    public void setAction(String action) {this.action = action;}
    public void setCreatedAt(ZonedDateTime created_at) {this.created_at = created_at;}
    public void setDetails(String details) {this.details = details;}
    public void setUserName(String user_name) {this.user_name = user_name;}

}