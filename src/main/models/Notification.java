package main.models;

import java.time.ZonedDateTime;

import com.surrealdb.RecordId;

public class Notification {
    private RecordId id;
    private String content;
    private Object target;
    private ZonedDateTime created_at;

    public Notification() {}

    public Notification(String content, Object target) {
        this.content = content;
        this.target = target;
    }

    public RecordId getId() { return id; }
    public String getContent() { return content; }
    public Object getTarget() { return target; }
    public ZonedDateTime getCreatedAt() { return created_at; }
}