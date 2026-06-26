package main.states;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import jakarta.mail.MessagingException;
import java.io.File;
import java.time.ZonedDateTime;

import main.DatabaseManager;
import main.DatabaseManager.NotificationRequest;
import main.PropertiesManager;
import main.enums.UserStatus;
import main.enums.UserType;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.User;
import main.utils.Email;
import main.utils.FormBuilder;
import main.utils.FormValidator;
import main.utils.ImageUploader;

public class CreateUserState extends State {
    private final String type;
    private final String status;
    private PropertiesManager props = new PropertiesManager();
    private User createdUser;

    // common fields
    private JTextField txtName = new JTextField(textFieldCols);
    private JTextField txtUsername = new JTextField(textFieldCols);
    private JPasswordField txtPassword = new JPasswordField(textFieldCols);
    private JTextField txtEmail = new JTextField(textFieldCols);

    // registrable-only fields (employee/client)
    private JTextField txtNif = new JTextField(textFieldCols);
    private JTextField txtPhone = new JTextField(textFieldCols);
    private JTextField txtAddress = new JTextField(textFieldCols);

    // client-only
    private JTextField txtSector = new JTextField(textFieldCols);
    private JComboBox<String> cmbScale = new JComboBox<>(new String[]{"A", "B", "C", "D"});

    // employee-only
    private JComboBox<Integer> cmbSpecialization = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

    // image
    private File selectedImage;
    private JLabel lblImageStatus = new JLabel("No image selected");

    public CreateUserState(String type, String status) {
        this.type = type;
        this.status = status;
    }

    @Override
    public JPanel buildView() {
        FormBuilder form = new FormBuilder("Create " + capitalize(type))
            .addField("Name:", txtName)
            .addField("Username:", txtUsername)
            .addField("Password:", txtPassword)
            .addField("Email:", txtEmail);

        if (!type.equals("admin")) {
            form.addField("NIF:", txtNif)
                .addField("Phone:", txtPhone)
                .addField("Address:", txtAddress);
        }

        if (type.equals("client")) {
            form.addField("Sector:", txtSector)
                .addField("Scale:", cmbScale);
        } else if (type.equals("employee")) {
            form.addField("Specialization:", cmbSpecialization);
        }

        JButton btnChooseImage = new JButton("Choose photo...");
        btnChooseImage.addActionListener(e -> chooseImage());
        form.addRow(btnChooseImage, lblImageStatus);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> back());

        JButton btnSubmit = new JButton("Create");
        btnSubmit.addActionListener(e -> submit());

        form.addButtonRow(btnBack, btnSubmit);

        return form.build();
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedImage = chooser.getSelectedFile();
            lblImageStatus.setText(selectedImage.getName());
        }
    }

    private boolean validateFields() {
        FormValidator validator = new FormValidator()
            .require("Name", txtName)
            .require("Username", txtUsername)
            .require("Password", txtPassword)
            .require("Email", txtEmail);

        if (!type.equals("admin")) {
            validator
                .require("NIF", txtNif)
                .require("Phone", txtPhone)
                .require("Address", txtAddress);
        }

        if (type.equals("client")) {
            validator.require("Sector", txtSector);
        }

        if (!validator.isValid()) {
            JOptionPane.showMessageDialog(null, validator.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String uploadImageIfSelected() throws Exception {
        return (selectedImage != null) ? ImageUploader.upload(selectedImage) : "";
    }

    private boolean persistUser(String imagePath) {
        User user = buildUser(imagePath);
        try {
            DatabaseManager.getInstance().saveUser(user);
            this.createdUser = user;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not create user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void notifyIfPending() {
        if (!status.equals(UserStatus.PENDING.toString())) return;

        try {
            Email.sendRegistrationEmail(props, createdUser.getEmail(), createdUser.getName());
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

        DatabaseManager.getInstance().sendNotification(
            new NotificationRequest("User '" + createdUser.getUsername() + "' awaiting approval", UserType.ADMIN.toString())
        );
    }

    private void submit() {
    if (!validateFields()) return;

    String imagePath;
    try {
        imagePath = uploadImageIfSelected();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Image upload failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (!persistUser(imagePath)) return;

    notifyIfPending();

    JOptionPane.showMessageDialog(null, capitalize(type) + " created! Await for admin approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
    back();
    back();
}

    private User buildUser(String imagePath) {
        String password = new String(txtPassword.getPassword());
        String image = (imagePath != null) ? imagePath : "";

        switch (type) {
            case "admin":
                return new Admin.Builder()
                    .setName(txtName.getText())
                    .setUsername(txtUsername.getText())
                    .setPassword(password)
                    .setEmail(txtEmail.getText())
                    .setImage(image)
                    .setStatus(status)
                    .build();

            case "employee":
                return new Employee.Builder()
                    .setName(txtName.getText())
                    .setUsername(txtUsername.getText())
                    .setPassword(password)
                    .setEmail(txtEmail.getText())
                    .setImage(image)
                    .setNif(txtNif.getText())
                    .setPhone(txtPhone.getText())
                    .setAddress(txtAddress.getText())
                    .setSpecialization(String.valueOf(cmbSpecialization.getSelectedItem()))
                    .setStartDate(ZonedDateTime.now())
                    .setStatus(status)
                    .build();

            case "client":
                return new Client.Builder()
                    .setName(txtName.getText())
                    .setUsername(txtUsername.getText())
                    .setPassword(password)
                    .setEmail(txtEmail.getText())
                    .setImage(image)
                    .setNif(txtNif.getText())
                    .setPhone(txtPhone.getText())
                    .setAddress(txtAddress.getText())
                    .setSector(txtSector.getText())
                    .setScale(String.valueOf(cmbScale.getSelectedItem()))
                    .setStatus(status)
                    .build();

            default:
                return null;
        }
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}