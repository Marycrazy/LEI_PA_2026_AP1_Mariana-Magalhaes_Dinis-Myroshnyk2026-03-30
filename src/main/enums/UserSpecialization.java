package main.enums;

/**
 * Represents an employee's specialization level, stored and displayed as a
 * numeric string from 1 (lowest) to 5 (highest).
 */
public enum UserSpecialization {
    /** Specialization level 1. */
    ONE("1"),
    /** Specialization level 2. */
    TWO("2"),
    /** Specialization level 3. */
    THREE("3"),
    /** Specialization level 4. */
    FOUR("4"),
    /** Specialization level 5. */
    FIVE("5");

    private String value;

    UserSpecialization(String value) {
        this.value = value;
    }

    /**
     * Returns this specialization level as its numeric string value.
     *
     * @return the numeric string (e.g. {@code "1"} through {@code "5"})
     */
    public String toString() {
        return this.value;
    }
}