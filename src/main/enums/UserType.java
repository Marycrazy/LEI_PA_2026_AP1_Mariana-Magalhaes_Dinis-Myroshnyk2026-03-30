package main.enums;

/**
 * Represents the role a user account has within the system, determining
 * which menus and actions are available to them.
 */
public enum UserType {
    /** Administrator: manages users, repairs, parts, and the action log. */
    ADMIN,
    /** Employee: carries out assigned repairs. */
    EMPLOYEE,
    /** Client: submits equipment and requests repairs. */
    CLIENT;

    /**
     * Returns the enum constant's name in upper case.
     *
     * @return the upper-case name of this user type
     */
    public String toString() {
        return this.name().toUpperCase();
    }
}