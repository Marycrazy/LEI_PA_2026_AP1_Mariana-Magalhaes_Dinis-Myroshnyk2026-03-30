package main.states;

import java.util.Map;
import main.utils.Input;
import main.utils.PressKey;

public abstract class DetailState<T> extends State {
    protected abstract String getTitle();

    protected abstract void renderFields();

    protected abstract Map<String, String> getActions();

    protected abstract void handleAction(String key);

    protected void onEnter() {}

    private boolean entered = false;

    @Override
    public void render() {
        if (!entered) { onEnter(); entered = true; }

        System.out.println("--- " + getTitle() + " ---\n");
        renderFields();
        System.out.println();

        Map<String, String> actions = getActions();
        if (!actions.isEmpty()) {
            for (Map.Entry<String, String> e : actions.entrySet())
                System.out.println(e.getKey() + ". " + e.getValue());
        }
        System.out.println("0. Back");
        System.out.print("Choice: ");
    }

    @Override
    public void handleInput() {
        String input = Input.getScanner().nextLine().trim().toUpperCase();

        if (input.equals("0")) { back(); return; }

        if (getActions().containsKey(input)) {
            handleAction(input);
        } else {
            System.out.println("Invalid option.");
            PressKey.enter();
        }
    }
}