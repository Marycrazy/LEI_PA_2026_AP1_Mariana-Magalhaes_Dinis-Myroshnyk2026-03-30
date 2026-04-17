package main.states;

import java.util.Stack;

import main.models.User;
import main.utils.Clear;

public abstract class State {
    protected static User user;
    private static Stack<State> stateStack = new Stack<>();
    private static boolean isRunning = true;

    public void enter() { stateStack.push(this); }

    public static void start(State initState) {
        stateStack.push(initState);
        while (isRunning && !stateStack.isEmpty()) {
            Clear.screen();
            State curr = stateStack.peek();
            curr.render();
            curr.handleInput();
        }
    }

    public void next(State nextState) { stateStack.push(nextState); }

    public void back() { if (!stateStack.isEmpty()) stateStack.pop(); }

    public static void exit() { isRunning = false; }

    public abstract void render();
    public abstract void handleInput();
}