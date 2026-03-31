package main;

import java.util.HashMap;
import java.util.Map;

import com.surrealdb.RecordId;

import main.utils.Input;

public class Client extends RegistrableUser {
    private RecordId id;
    private String sector, scale;

    private record InputField(String field, String dbFunc) {}

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

    public static Client create() {
        InputField[] fields = {
            new InputField("Name", null),
            new InputField("Username", "fn::check_username"),
            new InputField("Password", null),
            new InputField("Email", "fn::check_email"),
            new InputField("NIF", "fn::check_nif"),
            new InputField("Phone", "fn::check_phone"),
            new InputField("Address", null),
            new InputField("Sector", null),
            new InputField("Scale", "fn::check_scale")
        };

        Map<String, String> inputMap = new HashMap<>();

        for (InputField field : fields) {
            String input = (field.dbFunc() == null)
                ? Input.getInput(field.field())
                : Input.getInput(field.field(), field.dbFunc());
            if (input == null) return null;
            inputMap.put(field.field(), input);
        }

        Client client = (Client) new Client.Builder()
            .setSector(inputMap.get("Sector"))
            .setScale(inputMap.get("Scale"))
            .setNif(inputMap.get("NIF"))
            .setPhone(inputMap.get("Phone"))
            .setAddress(inputMap.get("Address"))
            .setName(inputMap.get("Name"))
            .setUsername(inputMap.get("Username"))
            .setPassword(inputMap.get("Password"))
            .setEmail(inputMap.get("Email"))
            .build();

        return client;
    }
}