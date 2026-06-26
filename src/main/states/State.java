package main.states;

import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.models.User;

public abstract class State {
    protected static User user;
    private static Stack<State> stateStack = new Stack<>();
    private static JFrame frame;

    public static void init(JFrame f) {
        frame = f;
    }

    public abstract JPanel buildView();

    public void enter() {
        stateStack.push(this);
        show();
    }

    public void next(State nextState) {
        nextState.enter();
    }

    public void back() {
        if (!stateStack.isEmpty()) stateStack.pop();
        if (!stateStack.isEmpty()) {
            stateStack.peek().show();
        }
    }

    private void show() {
        JPanel content = buildView();
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        frame.setContentPane(scrollPane);
        frame.revalidate();
        frame.repaint();
    }

    public static void exit() {
        frame.dispose();
        System.exit(0);
    }
}