import com.surrealdb.RecordId;

enum UserScale { A, B, C, D }

@SuppressWarnings("unused") // Temporary, until the model is implemented
public class Client extends RegistrableUser {
    private RecordId clientId;
    private String sector;
    private UserScale scale;

    public Client() {}

    public RecordId getClientId() { return clientId; }
}