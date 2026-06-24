package main.states;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import main.enums.UserStatus;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.User;
import main.utils.Validator;

import jakarta.mail.MessagingException;
import main.DatabaseManager;
import main.DatabaseManager.NotificationRequest;
import main.PropertiesManager;
import main.enums.UserType;
import main.utils.Email;





public class CreateUserState extends JFrame {
    private PropertiesManager props = new PropertiesManager();
    // campos comuns
    private JTextField txtName, txtUsername, txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblPhoto;
    private File selectedPhoto;

    // campos específicos Employee
    private JTextField txtSpecialization;

    // campos específicos Client
    private JTextField txtSector, txtScale;

    // campos comuns a Employee e Client (RegistrableUser)
    private JTextField txtNif, txtPhone, txtAddress;

    private String type; // "EMPLOYEE" ou "CLIENT"
    
    public CreateUserState(String type, String status) {
        setTitle("Create User " + type);
        this.type = type;
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // --- painel principal com scroll ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int row = 0;

         // --- campos comuns ---
        row = addField(formPanel, gbc, "Name:", txtName = new JTextField(15), row);
        row = addField(formPanel, gbc, "Username:", txtUsername = new JTextField(15), row);
        row = addField(formPanel, gbc, "Password:", txtPassword = new JPasswordField(15), row);
        row = addField(formPanel, gbc, "Email:", txtEmail = new JTextField(15), row);

        // --- foto ---
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Photo:"), gbc);

        lblPhoto = new JLabel("No photo selected");
        lblPhoto.setToolTipText("Click to select a photo");
        JButton btnPhoto = new JButton("Choose Photo");
        btnPhoto.addActionListener(e -> choosePhoto());

        gbc.gridx = 1; gbc.gridy = row++;
        formPanel.add(btnPhoto, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(lblPhoto, gbc);
        gbc.gridwidth = 1;

        // --- campos do RegistrableUser (Employee e Client) ---
        if (!type.equalsIgnoreCase("ADMIN")) {
            row = addField(formPanel, gbc, "NIF:", txtNif = new JTextField(15), row);
            row = addField(formPanel, gbc, "Phone:", txtPhone = new JTextField(15), row);
            row = addField(formPanel, gbc, "Address:", txtAddress = new JTextField(15), row);
        }

        // --- campos específicos ---
        if (type.equalsIgnoreCase("EMPLOYEE")) {
            row = addField(formPanel, gbc, "Specialization:", txtSpecialization = new JTextField(15), row);
        } else if (type.equalsIgnoreCase("CLIENT")) {
            row = addField(formPanel, gbc, "Sector:", txtSector = new JTextField(15), row);
            row = addField(formPanel, gbc, "Scale:", txtScale = new JTextField(15), row);
        }

        // --- scroll para o caso do formulário ser grande ---
        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(null);

        // --- botões em baixo ---
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back");
        JButton btnSubmit = new JButton("Submit");

        btnBack.addActionListener(e -> {
            new SignUp().setVisible(true);
            dispose();
        });

        // "handleSubmit()"
        btnSubmit.addActionListener(e -> handleSubmit(status));

        buttons.add(btnBack);
        buttons.add(btnSubmit);

        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        setVisible(true);
    }

    // método auxiliar para adicionar label + campo na mesma linha
    private int addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);

        return row + 1;
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPhoto = chooser.getSelectedFile();
            lblPhoto.setText(selectedPhoto.getName());
        }
    }

    private void handleSubmit(String status) {
         // montar o map com os valores do formulário
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("Name", txtName.getText().trim());
        inputMap.put("Username", txtUsername.getText().trim());
        inputMap.put("Password", new String(txtPassword.getPassword()).trim());
        inputMap.put("Email", txtEmail.getText().trim());

        if (selectedPhoto != null) {
            inputMap.put("Image", selectedPhoto.getAbsolutePath());
        }

        if (!type.equalsIgnoreCase("ADMIN")) {
            inputMap.put("NIF", txtNif.getText().trim());
            inputMap.put("Phone", txtPhone.getText().trim());
            inputMap.put("Address", txtAddress.getText().trim());
        }

        if (type.equalsIgnoreCase("EMPLOYEE")) {
            inputMap.put("Specialization", txtSpecialization.getText().trim());
        } else if (type.equalsIgnoreCase("CLIENT")) {
            inputMap.put("Sector", txtSector.getText().trim());
            inputMap.put("Scale", txtScale.getText().trim());
        }

        String error = validateFields(inputMap);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = null;
        if (type.equalsIgnoreCase("admin")) user = Admin.create();
        else if (type.equalsIgnoreCase("employee")) user = Employee.create();
        else if (type.equalsIgnoreCase("client")) user = Client.create(inputMap);
        if (user == null) return;
        else if(status.equals("ACTIVE")) user.setStatus(UserStatus.ACTIVE.toString());
        else user.setStatus(UserStatus.PENDING.toString());

        DatabaseManager.getInstance().saveUser(user);
        DatabaseManager.getInstance().sendNotification(new NotificationRequest("User '" + user.getUsername() + "' awaiting approval", UserType.ADMIN.toString()));
        try { Email.sendRegistrationEmail(props, user.getEmail(), user.getName()); } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
        System.out.println(type.substring(0, 1).toUpperCase() + type.substring(1) + " created!");
        System.out.println("Please wait while an admin reviews your request...");

    }

    private String validateFields(Map<String, String> inputMap) {
        String nameError = Validator.getError(inputMap.get("Name"));
        if (nameError != null) return "Name: " + nameError;

        String passwordError = Validator.getError(inputMap.get("Password"));
        if (passwordError != null) return "Password: " + passwordError;

        String usernameError = Validator.getError("fn::check_username", inputMap.get("Username"));
        if (usernameError != null) return "Username: " + usernameError;

        String emailError = Validator.getError("fn::check_email", inputMap.get("Email"));
        if (emailError != null) return "Email: " + emailError;

        if (!type.equalsIgnoreCase("ADMIN")) {

            String nifError = Validator.getError("fn::check_nif", inputMap.get("NIF"));
            if (nifError != null) return "NIF: " + nifError;

            String phoneError = Validator.getError("fn::check_phone", inputMap.get("Phone"));
            if (phoneError != null) return "Phone: " + phoneError;

            String addressError = Validator.getError(inputMap.get("Address"));
            if (addressError != null) return "Address: " + addressError;
        }

        if (type.equalsIgnoreCase("EMPLOYEE")) {
            String specError = Validator.getError("fn::check_specialization", inputMap.get("Specialization"));
            if (specError != null) return "Specialization: " + specError;

        } else if (type.equalsIgnoreCase("CLIENT")) {
            String scaleError = Validator.getError("fn::check_scale", inputMap.get("Scale"));
            if (scaleError != null) return "Scale: " + scaleError;

            String sectorError = Validator.getError(inputMap.get("Sector"));
            if (sectorError != null) return "Sector: " + sectorError;
        }

        return null;
    }



}
