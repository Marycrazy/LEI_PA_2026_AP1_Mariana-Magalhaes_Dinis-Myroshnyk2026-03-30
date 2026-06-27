package main.states;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.utils.MenuBuilder;

public class ManagePartsMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder(user)
            .addButton("List Parts", "List all Parts", () -> next(new ListPartsState()))
            .addButton("Add Parts", "Add Parts", () -> JOptionPane.showMessageDialog(null, "Peças - adicionar")) // temporary button
            .addButton("Back", "Return to previous menu", this::back)
            .build();
    }
}
