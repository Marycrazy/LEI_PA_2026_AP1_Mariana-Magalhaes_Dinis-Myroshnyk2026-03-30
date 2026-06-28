package main.enums;

/**
 * Represents the size/scale tier of a client organization, used to
 * categorize clients for business purposes (e.g. pricing, prioritization).
 */
public enum UserScale {
    /** Smallest scale tier. */
    A,
    /** Second smallest scale tier. */
    B,
    /** Second largest scale tier. */
    C,
    /** Largest scale tier. */
    D;

    /**
     * Returns the enum constant's name in upper case.
     *
     * @return the upper-case name of this scale tier
     */
    public String toString() {
        return this.name().toUpperCase();
    }
}