package main.states;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.models.Log;

public class LogsListState extends ListState<Log> {
    private String search = "";

    @Override
    protected String getTitle() {
        return "Logs";
    }

    @Override
    protected List<Log> fetchItems() {
        return DatabaseManager.getInstance().getLogs(search);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"User", "Action", "Details", "Date"};
    }

    @Override
    protected Object[] getRowValues(Log log) {
        return new Object[]{
            log.getUserName(),
            log.getAction(),
            log.getDetails(),
            log.getCreatedAt().toLocalDate()
        };
    }

    @Override
    protected void onSelect(Log u) {}

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