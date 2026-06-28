package main.models;

import java.time.ZonedDateTime;
import com.surrealdb.RecordId;

/**
 * System alert message directed to targeted groups or single entities.
 */
public class Notification {
    private RecordId id;
    private String content;
    private Object target;
    private ZonedDateTime created_at;

    /**
     * Default empty constructor.
     */
    public Notification() {}

    /**
     * Generates a notification payload template.
     *
     * @param content message body descriptive details
     * @param target identifier or entity references capturing recipient bounds
     */
    public Notification(String content, Object target) {
        this.content = content;
        this.target = target;
    }

    public RecordId getId() { return id; }
    public String getContent() { return content; }
    public Object getTarget() { return target; }
    public ZonedDateTime getCreatedAt() { return created_at; }
}