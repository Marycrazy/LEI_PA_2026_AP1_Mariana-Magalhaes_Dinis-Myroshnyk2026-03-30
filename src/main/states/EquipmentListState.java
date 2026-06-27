package main.states;

import main.models.User;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.models.Equipment;

public class EquipmentListState extends ListState<Equipment> {
    private final User owner;
    private String search = "";

    public EquipmentListState(User owner) {
        this.owner = owner;
    }

    @Override
    protected String getTitle() {
        return "Equipment";
    }

    @Override
    protected List<Equipment> fetchItems() {
        return DatabaseManager.getInstance().getEquipment(owner.getUserId(), search);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"batch", "model", "brand", "SKU", "manufacturing date", "last repair date", "last submission date"};
    }

    @Override
    protected Object[] getRowValues(Equipment e) {
        return new Object[]{e.getBatch(), e.getModel(), e.getBrand(), e.getSku(), e.getManufacturingDate(), e.getLastRepairDate(), e.getLastSubmissionDate()};
    }

    @Override
    protected void onSelect(Equipment e) {
        next(new EquipmentDetailState(e));
    }

    @Override
    protected void renderExtras(JPanel extrasPanel) {
        JTextField txtSearch = new JTextField(search, 18);
        JButton btnSearch = new JButton("Search");

        Runnable applySearch = () -> {
            search = txtSearch.getText().trim();
            refresh();
        };

        txtSearch.addActionListener(e -> applySearch.run());
        btnSearch.addActionListener(e -> applySearch.run());

        extrasPanel.add(new JLabel("Search:"));
        extrasPanel.add(txtSearch);
        extrasPanel.add(btnSearch);
    }
    
}
