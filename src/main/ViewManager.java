package main;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;

/**
 * Utility class responsible for managing user interface component creation
 * and layout configurations for Swing views.
 */
public class ViewManager {
    /**
     * Creates and returns a {@link JPanel} configured with a {@link GridBagLayout}
     * and an etched border containing the specified title.
     *
     * @param title the text to display as the panel's border title
     * @return a titled JPanel instance ready for component addition
     */
    public static JPanel titledPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP
            )
        );
        return panel;
    }

    /**
     * Generates a default set of {@link GridBagConstraints} with predefined padding insets
     * to maintain consistent component spacing across layout grids.
     *
     * @return a new GridBagConstraints instance with default 10px spacing insets
     */
    public static GridBagConstraints defaultConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        return gbc;
    }
}