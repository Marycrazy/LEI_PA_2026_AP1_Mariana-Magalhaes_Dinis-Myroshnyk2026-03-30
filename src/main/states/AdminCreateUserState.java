package main.states;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import java.awt.*;

import main.DatabaseManager;
import main.enums.UserStatus;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.User;
import main.utils.PressKey;

public class AdminCreateUserState extends JFrame {

    public void render() {
        setTitle("Crete user");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Register",
                TitledBorder.LEFT,
                TitledBorder.TOP
            )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // label
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Select account type:"), gbc);

        // radio buttons
        JRadioButton rbAdmin = new JRadioButton("Admin");
        JRadioButton rbEmployee = new JRadioButton("Employee");
        JRadioButton rbClient = new JRadioButton("Client");
        rbEmployee.setSelected(true); // por omissão

        ButtonGroup group = new ButtonGroup();
        group.add(rbAdmin);
        group.add(rbEmployee);
        group.add(rbClient);

        gbc.gridx = 0; 
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(rbEmployee, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(rbClient, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(rbAdmin, gbc);



        // botões
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            new SignIn().setVisible(true);
            dispose();
        });

        JButton btnNext = new JButton("Next");
        btnNext.addActionListener(e -> {if (rbEmployee.isSelected()) createUser("employee");
            else if (rbClient.isSelected()) createUser("client");
            dispose();
        });

        buttons.add(btnBack);
        buttons.add(btnNext);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttons, gbc);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }


    private void createUser(String type) {
        User user = null;

        System.out.println("Creating " + type + "...");

        new CreateUserState(type, UserStatus.ACTIVE.toString()).setVisible(true);
        dispose();

        DatabaseManager.getInstance().saveUser(user);
        System.out.println(type.substring(0, 1).toUpperCase() + type.substring(1) + " created!");

        PressKey.enter();

    }
}