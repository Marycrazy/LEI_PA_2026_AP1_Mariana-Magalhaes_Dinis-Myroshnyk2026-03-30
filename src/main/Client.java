package main;

import java.util.Map;

import com.surrealdb.RecordId;

public class Client extends RegistrableUser {
    private RecordId id;
    private String sector, scale;

    public Client() {}

    public static class Builder extends RegistrableUser.Builder<Builder> {
        private String sector, scale;

        public Builder setSector(String sector) { this.sector = sector; return this; }
        public Builder setScale(String scale) { this.scale = scale; return this; }

        @Override
        protected Builder self() { return this; }

        @Override
        public Client build() {
            Client client = new Client();
            client.name = this.name;
            client.username = this.username;
            client.password = this.password;
            client.email = this.email;
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

    public static Map<String, Object> toMap(Client client) {
        return Map.of(
            "sector", client.getSector(),
            "scale", client.getScale()
        );
    }
}