package main.models;

import java.util.Map;
import com.surrealdb.RecordId;
import main.enums.UserStatus;
import main.enums.UserType;

/**
 * Represents a client account, extending {@link RegistrableUser} with commercial
 * metrics such as sector and business scale.
 */
public class Client extends RegistrableUser {
    /** The specific database record ID for the client table. */
    private RecordId id;
    /** The commercial industry sector and organization scale. */
    private String sector, scale;

    /**
     * Constructs a default Client instance.
     */
    public Client() {}

    /**
     * Builder pattern implementation for cleanly generating {@link Client} instances.
     */
    public static class Builder extends RegistrableUser.Builder<Builder> {
        private String sector, scale;

        public Builder setSector(String sector) { this.sector = sector; return this; }
        public Builder setScale(String scale) { this.scale = scale; return this; }

        @Override
        protected Builder self() { return this; }

        @Override
        public Client build() {
            Client client = new Client();
            client.setUserId(id);
            client.name = this.name;
            client.username = this.username;
            client.password = this.password;
            client.email = this.email;
            client.image = this.image;
            client.type = UserType.CLIENT.toString();
            client.status = (this.status != null) ? this.status : UserStatus.PENDING.toString();
            client.address = this.address;
            client.nif = this.nif;
            client.phone = this.phone;
            client.scale = this.scale;
            client.sector = this.sector;
            return client;
        }
    }

    public RecordId getClientId() { return id; }
    public String getSector() { return sector; }
    public String getScale() { return scale; }

    public void setClientId(RecordId id) { this.id = id; }

    /**
     * Maps client-specific attributes for database operations.
     *
     * @param client the client entity to map
     * @return a map with sector and scale entries
     */
    public static Map<String, Object> toMap(Client client) {
        return Map.of(
            "sector", client.getSector(),
            "scale", client.getScale()
        );
    }
}