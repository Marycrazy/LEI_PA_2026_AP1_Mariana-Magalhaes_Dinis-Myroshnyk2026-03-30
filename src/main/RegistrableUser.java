package main;

import java.util.Map;

import com.surrealdb.RecordId;

public abstract class RegistrableUser extends User {
    protected RecordId id;
    protected String nif, phone, address;

    public abstract static class Builder<T extends Builder<T>> extends User.Builder<T> {
        protected String nif, phone, address;

        public T setNif(String nif) { this.nif = nif; return self(); }
        public T setPhone(String phone) { this.phone = phone; return self(); }
        public T setAddress(String address) { this.address = address; return self(); }
    }

    public RecordId getRegistrableUserId() { return id; }
    public String getNif() { return nif; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    public void setRegistrableUserId(RecordId id) { this.id = id; }

    public static Map<String, Object> toMap(RegistrableUser regUser) {
        return Map.of(
            "nif", regUser.getNif(),
            "phone", regUser.getPhone(),
            "address", regUser.getAddress()
        );
    }
}