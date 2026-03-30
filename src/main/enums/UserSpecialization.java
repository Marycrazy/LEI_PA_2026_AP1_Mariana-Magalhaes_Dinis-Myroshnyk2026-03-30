package main.enums;

public enum UserSpecialization {
    ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5");

    private String value;

    UserSpecialization(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}