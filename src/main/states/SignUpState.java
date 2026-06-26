package main.states;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import main.enums.UserStatus;
import main.utils.FormBuilder;

public class SignUpState extends State {
    private JRadioButton rbEmployee = new JRadioButton("Employee");
    private JRadioButton rbClient = new JRadioButton("Client");

    @Override
    public JPanel buildView() {
        rbEmployee.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(rbEmployee);
        group.add(rbClient);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> back());

        JButton btnNext = new JButton("Next");
        btnNext.addActionListener(e -> {
            String type = rbEmployee.isSelected() ? "employee" : "client";
            createUser(type);
        });

        return new FormBuilder("Register")
            .addFullWidthRow(new JLabel("Select account type:"))
            .addRow(rbEmployee, rbClient)
            .addButtonRow(btnBack, btnNext)
            .build();
    }

    private void createUser(String type) {
        System.out.println("Creating " + type + "...");
        next(new CreateUserState(type, UserStatus.PENDING.toString()));
    }
}