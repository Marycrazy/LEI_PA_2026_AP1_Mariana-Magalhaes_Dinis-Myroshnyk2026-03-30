package main.states;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.models.Part;
import main.models.User;
import main.DatabaseManager;

public class ListPartsState extends ListState<Part> {
    private String search = "";

    @Override
    protected String getTitle() {
        return "Parts";
    }

    @Override
    protected List<Part> fetchItems() {
        return DatabaseManager.getInstance().getParts(search);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"Designation", "Manufacturer", "Stock Quantity"};
    }

    @Override
    protected Object[] getRowValues(Part p) {
        return new Object[]{p.getDesignation(), p.getManufacturer(), p.getStockQuantity()};
    }

    @Override
    protected void onSelect(Part p) {}

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
