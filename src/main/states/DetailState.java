package main.states;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.utils.FormBuilder;

public abstract class DetailState<T> extends State {

    protected abstract String getTitle();
    protected abstract void renderFields(FormBuilder form);
    protected abstract List<JButton> getActions();

    protected void onEnter() {}

    @Override
    public JPanel buildView() {
        onEnter();

        FormBuilder form = new FormBuilder(getTitle());
        renderFields(form);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> back());

        List<JButton> actions = getActions();
        JButton[] buttons = new JButton[actions.size() + 1];
        buttons[0] = btnBack;
        for (int i = 0; i < actions.size(); i++) buttons[i + 1] = actions.get(i);

        form.addButtonRow(buttons);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(form.build(), BorderLayout.CENTER);
        return wrapper;
    }
}