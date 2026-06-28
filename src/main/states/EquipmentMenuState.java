package main.states;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.utils.MenuBuilder;

public class EquipmentMenuState extends State {
    @Override
    public JPanel buildView() {
        return new MenuBuilder()
            .addButton("List my equipment", "List my equipment", () -> next(new EquipmentListState(user)))
            .addButton("Add equipment", "Submit a new equipment", () -> JOptionPane.showMessageDialog(null, "equipment - adicionar")) // TODO: temporary button
            .addButton("Back", "Return to previous menu", this::back)
            .build();
    }
}