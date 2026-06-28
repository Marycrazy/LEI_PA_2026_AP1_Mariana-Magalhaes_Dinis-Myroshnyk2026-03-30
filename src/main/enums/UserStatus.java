package main.enums;

/**
 * Represents the account status of a user, controlling whether they are
 * able to sign in and use the system.
 */
public enum UserStatus {
    /** Account is approved and usable. */
    ACTIVE,
    /** Account has been deactivated and can no longer sign in. */
    INACTIVE,
    /** Registration was reviewed and rejected by an admin. */
    REJECTED,
    /** Registration submitted and awaiting admin approval. */
    PENDING;

    /**
     * Returns the enum constant's name in upper case.
     *
     * @return the upper-case name of this status
     */
    public String toString() {
        return this.name().toUpperCase();
    }
}