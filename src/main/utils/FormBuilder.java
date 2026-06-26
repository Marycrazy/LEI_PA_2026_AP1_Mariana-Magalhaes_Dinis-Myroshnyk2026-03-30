package main.utils;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.ViewManager;

import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Cursor;

public class FormBuilder {
    private final JPanel panel;
    private final GridBagConstraints gbc;
    private int row = 0;

    public FormBuilder(String title) {
        this.panel = ViewManager.titledPanel(title);
        this.gbc = ViewManager.defaultConstraints();
    }

    public FormBuilder addField(String label, JComponent field) {
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);

        row++;
        return this;
    }

    public FormBuilder addButtonRow(JButton... buttons) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton b : buttons) buttonPanel.add(b);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        row++;
        return this;
    }

    public FormBuilder addLinkLabel(String html, Runnable onClick) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel link = new JLabel("<html><a href=''>" + html + "</a></html>", SwingConstants.CENTER);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onClick.run();
            }
        });
        panel.add(link, gbc);

        row++;
        return this;
    }

    public FormBuilder addRow(JComponent... components) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        for (JComponent c : components) {
            panel.add(c, gbc);
            gbc.gridx++;
        }

        row++;
        return this;
    }

    public FormBuilder addFullWidthRow(JComponent component) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(component, gbc);

        row++;
        return this;
    }

    public JPanel build() {
        return panel;
    }
}