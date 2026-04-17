package main.enums;

public enum UserScale {
    A, B, C, D;

    public String toString() {
        return this.name().toUpperCase();
    }
}