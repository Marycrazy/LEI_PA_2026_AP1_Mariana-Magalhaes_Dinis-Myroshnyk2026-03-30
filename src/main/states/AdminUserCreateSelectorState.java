package main.states;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import main.enums.UserStatus;
import main.utils.FormBuilder;

public class AdminUserCreateSelectorState extends State {
    private JRadioButton rbAdmin = new JRadioButton("Admin");
    private JRadioButton rbEmployee = new JRadioButton("Employee");
    private JRadioButton rbClient = new JRadioButton("Client");

    @Override
    public JPanel buildView() {
        rbAdmin.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(rbAdmin);
        group.add(rbEmployee);
        group.add(rbClient);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> back());

        JButton btnNext = new JButton("Next");
        btnNext.addActionListener(e -> {
            String type;
            if (rbAdmin.isSelected()) type = "admin";
            else if (rbEmployee.isSelected()) type = "employee";
            else type = "client";
            createUser(type);
        });

        return new FormBuilder("Create User")
            .addFullWidthRow(new JLabel("Select account type:"))
            .addRow(rbAdmin, rbEmployee, rbClient)
            .addButtonRow(btnBack, btnNext)
            .build();
    }

    private void createUser(String type) {
        System.out.println("Creating " + type + "...");
        next(new UserCreateState(type, UserStatus.ACTIVE.toString()));
    }
}