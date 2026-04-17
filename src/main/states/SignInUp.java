package main.states;

import main.utils.Input;
import main.utils.PressKey;

public class SignInUp extends State {
    @Override
    public void render() {
        System.out.println("--- SIGN IN / SIGN UP ---");
        System.out.println("1. Sign in");
        System.out.println("2. Sign up");
        System.out.println("0. Exit");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine();

        switch (input) {
            case "1": new SignIn().enter(); break;
            case "2": new SignUp().enter(); break;
            case "0": System.exit(0); break;
            default:
                System.out.println("Invalid option!");
                PressKey.enter();
                break;
        }
    }
}