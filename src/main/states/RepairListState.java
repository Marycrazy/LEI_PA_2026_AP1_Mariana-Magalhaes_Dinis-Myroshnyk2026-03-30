package main.states;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.models.Repair;

public class RepairListState extends ListState<Repair> {
    private String search      = "";
    private String filterState = "";

    @Override
    protected String getTitle() {
        return "Equipment";
    }

    @Override
    protected List<Repair> fetchItems() {
        return DatabaseManager.getInstance().getRepairs(search, filterState, user);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"Repair code", "State", "observations", "start date", "end date", "cost"};
    }

    @Override
    protected Object[] getRowValues(Repair r) {
        return new Object[]{r.getRepairCode(), r.getState(), r.getObservations(), r.getStartDate(), r.getEndDate(), r.getCost()};
    }

    @Override
    protected void onSelect(Repair e) {
        next(new RepairDetailState(e));
    }

    @Override
    protected void renderExtras(JPanel extrasPanel) {
        JTextField txtSearch = new JTextField(search, 18);
        JButton btnSearch = new JButton("Search");
        JComboBox<String> filterStatus = new JComboBox<>(new String[]{"Pending", "Accepted", "Rejected by admin", "Rejected by employee", "In progress", "Completed", "Archived"});
        JButton btnfilter = new JButton("Filter by status");


        Runnable applySearch = () -> {
            search = txtSearch.getText().trim();
            refresh();
        };

        txtSearch.addActionListener(e -> applySearch.run());
        btnSearch.addActionListener(e -> applySearch.run());
        btnfilter.addActionListener(e -> {
            filterState = ((String) filterStatus.getSelectedItem()).trim().toUpperCase().replace(' ', '_');
            System.out.println(filterState);
            refresh();
        });

        extrasPanel.add(new JLabel("Search:"));
        extrasPanel.add(txtSearch);
        extrasPanel.add(btnSearch);
        extrasPanel.add(new JLabel("Filter by status:"));
        extrasPanel.add(filterStatus);
        extrasPanel.add(btnfilter);
    }
    
}
