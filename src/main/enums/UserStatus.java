package main.enums;

public enum UserStatus {
    ACTIVE, INACTIVE, REJECTED, PENDING;

    public String toString() {
        return this.name().toUpperCase();
    }
}