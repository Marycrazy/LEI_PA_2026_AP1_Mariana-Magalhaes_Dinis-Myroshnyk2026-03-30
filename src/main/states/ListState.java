package main.states;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import main.ViewManager;

public abstract class ListState<T> extends State {

    protected abstract String getTitle();
    protected abstract List<T> fetchItems();
    protected abstract String[] getColumns();
    protected abstract Object[] getRowValues(T item);
    protected abstract void onSelect(T item);

    protected void renderExtras(JPanel extrasPanel) {}

    protected List<JButton> getExtraButtons() {
        return List.of();
    }

    @Override
    public JPanel buildView() {
        JPanel panel = ViewManager.titledPanel(getTitle());
        var gbc = ViewManager.defaultConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JPanel extrasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        renderExtras(extrasPanel);
        if (extrasPanel.getComponentCount() > 0) {
            panel.add(extrasPanel, gbc);
            gbc.gridy++;
        }

        List<T> items = fetchItems();

        DefaultTableModel model = new DefaultTableModel(getColumns(), 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        items.forEach(item -> model.addRow(getRowValues(item)));

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JButton btnView = new JButton("View Details");
        btnView.setEnabled(false);

        table.getSelectionModel().addListSelectionListener(e ->
            btnView.setEnabled(table.getSelectedRow() != -1)
        );

        btnView.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) onSelect(items.get(table.convertRowIndexToModel(row)));
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row != -1) onSelect(items.get(table.convertRowIndexToModel(row)));
                }
            }
        });

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        if (items.isEmpty()) {
            JLabel empty = new JLabel("No results found.", SwingConstants.CENTER);
            empty.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(empty, gbc);
        } else {
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(460, 260));
            panel.add(scroll, gbc);
        }

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> back());

        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtons.add(btnBack);
        getExtraButtons().forEach(bottomButtons::add);
        bottomButtons.add(btnView);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(bottomButtons, BorderLayout.SOUTH);
        return wrapper;
    }
}