package main.enums;

public enum RepairStatus {
    PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, ARCHIVED, REJECTED_BY_ADMIN, REJECTED_BY_EMPLOYEE;

    public String toString() {
        return this.name().toUpperCase();
    }
}