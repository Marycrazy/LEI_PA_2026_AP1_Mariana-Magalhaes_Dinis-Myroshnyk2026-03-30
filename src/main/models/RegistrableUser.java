package main.models;

import java.util.Map;

import com.surrealdb.RecordId;

/**
 * Base class for users who register additional contact/identification
 * details beyond the common {@link User} fields — namely {@link Employee}
 * and {@link Client}.
 * <p>
 * Backed by its own {@code registrable_user} database record, related to
 * the underlying {@code user} record via an {@code is_a} graph edge.
 */
public abstract class RegistrableUser extends User {
    protected RecordId id;
    protected String nif, phone, address;

    /**
     * Base builder for {@link RegistrableUser} subclasses, adding the
     * registrable-specific fields (NIF, phone, address) on top of
     * {@link User.Builder}.
     *
     * @param <T> the concrete builder subtype, for fluent method chaining
     */
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

    /**
     * Converts a registrable user's registrable-specific fields into a map
     * suitable for use as the {@code CONTENT}/{@code MERGE} payload of a
     * {@code registrable_user} table query.
     *
     * @param regUser the registrable user to convert
     * @return a map of field names to values for NIF, phone, and address
     */
    public static Map<String, Object> toMap(RegistrableUser regUser) {
        return Map.of(
            "nif", regUser.getNif(),
            "phone", regUser.getPhone(),
            "address", regUser.getAddress()
        );
    }
}