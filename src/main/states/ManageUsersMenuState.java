package main.states;

import main.utils.Input;
import main.utils.PressKey;

public class ManageUsersMenuState extends State {
    @Override
    public void render() {
        System.out.println("--- MANAGE USERS ---");
        System.out.println("1. Create user");
        System.out.println("2. List users");
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": new AdminCreateUserState().enter(); break;
            case "2": new ListUsersState().enter(); break;
            case "0": this.back(); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }
}