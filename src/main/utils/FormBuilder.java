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

/**
 * Builder utility for creating standardized Swing input forms using structured
 * layout architectures.
 */
public class FormBuilder {
    private final JPanel panel;
    private final GridBagConstraints gbc;
    private int row = 0;

    /**
     * Initiates a new layout form container window model wrapper context.
     *
     * @param title text string used for generating the labeled panel border header outline
     */
    public FormBuilder(String title) {
        this.panel = ViewManager.titledPanel(title);
        this.gbc = ViewManager.defaultConstraints();
    }

    /**
     * Inserts a standard input field into the layout, aligning a description label on the
     * left and the input component on the right.
     *
     * @param label descriptive instruction title string label attached left-side
     * @param field input or selection swing target component placed right-side
     * @return this builder instance for method chaining
     */
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

    /**
     * Appends a trailing horizontal layout panel block containing a series of operation action trigger buttons.
     *
     * @param buttons array of operational push button instances added into the form action row
     * @return this builder instance for method chaining
     */
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

    /**
     * Appends an interactive hyper-link style label at the center of the current row layout.
     *
     * @param html    raw content string containing the text to display inside the click bounds
     * @param onClick executable routine action triggered when the link is clicked
     * @return this builder instance for method chaining
     */
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

    /**
     * Appends a series of arbitrary Swing elements along a single row sequence line.
     *
     * @param components array of user interface elements added into the layout row
     * @return this builder instance for method chaining
     */
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

    /**
     * Adds a component that spans across both columns of the form grid layout.
     *
     * @param component component layout target to embed spanning full width
     * @return this builder instance for method chaining
     */
    public FormBuilder addFullWidthRow(JComponent component) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);

        row++;
        return this;
    }

    /**
     * Returns the fully constructed form panel container.
     *
     * @return a single populated container {@link JPanel} view object target
     */
    public JPanel build() {
        return panel;
    }
}