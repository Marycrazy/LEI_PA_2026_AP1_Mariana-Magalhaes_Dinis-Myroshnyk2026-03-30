package main.enums;

public enum UserType {
    ADMIN, EMPLOYEE, CLIENT;

    public String toString() {
        return this.name().toUpperCase();
    }
}