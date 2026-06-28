package main.states;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.models.Repair;

public class RepairListState extends ListState<Repair> {
    private String search      = "";
    private String filterState = "";
    private ZonedDateTime startDate = null;
    private ZonedDateTime endDate   = null;

    @Override
    protected String getTitle() {
        return "Repairs";
    }

    @Override
    protected List<Repair> fetchItems() {
        return DatabaseManager.getInstance().getRepairs(search, filterState, startDate, endDate, user);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"Repair code", "Client", "State", "observations", "start date", "end date", "cost"};
    }

    @Override
    protected Object[] getRowValues(Repair r) {
        return new Object[]{r.getRepairCode(), r.getClientName(), r.getState(), r.getObservations(), r.getStartDate(), r.getEndDate(), r.getCost()};
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
        JButton btnfilter = new JButton("Filter");

        JTextField txtStartDate = placeholderField("YYYY-MM-DD", 10);
        JTextField txtEndDate = placeholderField("YYYY-MM-DD", 10);
        JButton btnDateFilter = new JButton("Filter");

        Runnable applySearch = () -> {
            search = txtSearch.getText().trim();
            refresh();
        };

        txtSearch.addActionListener(e -> applySearch.run());
        btnSearch.addActionListener(e -> applySearch.run());
        btnfilter.addActionListener(e -> {
            filterState = ((String) filterStatus.getSelectedItem()).trim().toUpperCase().replace(' ', '_');
            refresh();
        });

        btnDateFilter.addActionListener(e -> {
            try {
                String startText = readDateField(txtStartDate);
                String endText = readDateField(txtEndDate);

                ZonedDateTime newStart = startText.isEmpty() ? null
                    : LocalDate.parse(startText).atStartOfDay(ZoneId.systemDefault());
                ZonedDateTime newEnd = endText.isEmpty() ? null
                    : LocalDate.parse(endText).atStartOfDay(ZoneId.systemDefault());

                if (newStart != null && newEnd != null && newStart.isAfter(newEnd)) {
                    JOptionPane.showMessageDialog(null, "Start date must be before end date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                startDate = newStart;
                endDate = newEnd;
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        extrasPanel.add(new JLabel("Search:"));
        extrasPanel.add(txtSearch);
        extrasPanel.add(btnSearch);
        extrasPanel.add(new JLabel("Filter by status:"));
        extrasPanel.add(filterStatus);
        extrasPanel.add(btnfilter);
        extrasPanel.add(new JLabel("From:"));
        extrasPanel.add(txtStartDate);
        extrasPanel.add(new JLabel("To:"));
        extrasPanel.add(txtEndDate);
        extrasPanel.add(btnDateFilter);
    }

    private JTextField placeholderField(String placeholder, int cols) {
        JTextField field = new JTextField(placeholder, cols);
        field.setForeground(java.awt.Color.GRAY);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(placeholder);
                    field.setForeground(java.awt.Color.GRAY);
                }
            }
        });

        return field;
    }

    private String readDateField(JTextField field) {
        String text = field.getText().trim();
        return text.equals("YYYY-MM-DD") ? "" : text;
    }
}