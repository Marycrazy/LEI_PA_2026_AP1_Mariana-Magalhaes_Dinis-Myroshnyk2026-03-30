package main.enums;

/**
 * Represents the lifecycle states a repair request can be in, from initial
 * client submission through to completion or rejection at any stage.
 */
public enum RepairStatus {
    /** Submitted by a client, awaiting admin review. */
    PENDING,
    /** Approved by an admin and assigned to an employee. */
    ACCEPTED,
    /** Accepted by the assigned employee and currently being worked on. */
    IN_PROGRESS,
    /** Finished by the assigned employee. */
    COMPLETED,
    /** Closed and kept for record-keeping; no further action expected. */
    ARCHIVED,
    /** Rejected by an admin before assignment. */
    REJECTED_BY_ADMIN,
    /** Rejected by the assigned employee after being accepted by an admin. */
    REJECTED_BY_EMPLOYEE;

    /**
     * Returns the enum constant's name in upper case.
     *
     * @return the upper-case name of this status
     */
    public String toString() {
        return this.name().toUpperCase();
    }
}