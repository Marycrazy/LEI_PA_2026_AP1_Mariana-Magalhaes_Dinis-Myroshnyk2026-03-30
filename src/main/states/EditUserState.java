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

import java.io.File;

import main.DatabaseManager;
import main.models.Admin;
import main.models.Client;
import main.models.Employee;
import main.models.User;
import main.utils.FormBuilder;
import main.utils.FormValidator;
import main.utils.ImageService;

public class EditUserState extends State {
    private final User subject;

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
    private JLabel lblImageStatus;

    public EditUserState(User subject) {
        this.subject = subject;
    }

    @Override
    public JPanel buildView() {
        prefillFields();

        FormBuilder form = new FormBuilder("Edit " + subject.getUsername())
            .addField("Name:", txtName)
            .addField("Username:", txtUsername)
            .addField("Password:", txtPassword)
            .addField("Email:", txtEmail);

        if (subject instanceof Client || subject instanceof Employee) {
            form.addField("NIF:", txtNif)
                .addField("Phone:", txtPhone)
                .addField("Address:", txtAddress);
        }

        if (subject instanceof Client) {
            form.addField("Sector:", txtSector)
                .addField("Scale:", cmbScale);
        } else if (subject instanceof Employee) {
            form.addField("Specialization:", cmbSpecialization);
        }

        lblImageStatus = new JLabel(hasImage() ? "Current photo: " + subject.getImage() : "No image set");
        JButton btnChooseImage = new JButton("Change photo...");
        btnChooseImage.addActionListener(e -> chooseImage());
        form.addRow(btnChooseImage, lblImageStatus);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> back());

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> submit());

        form.addButtonRow(btnCancel, btnSave);

        return form.build();
    }

    private void prefillFields() {
        txtName.setText(subject.getName());
        txtUsername.setText(subject.getUsername());
        txtUsername.setEditable(false);
        txtEmail.setText(subject.getEmail());
        // password intentionally left blank: blank on submit means "keep current"

        if (subject instanceof Employee emp) {
            txtNif.setText(emp.getNif());
            txtPhone.setText(emp.getPhone());
            txtAddress.setText(emp.getAddress());
            cmbSpecialization.setSelectedItem(Integer.valueOf(emp.getSpecialization()));
        } else if (subject instanceof Client client) {
            txtNif.setText(client.getNif());
            txtPhone.setText(client.getPhone());
            txtAddress.setText(client.getAddress());
            txtSector.setText(client.getSector());
            cmbScale.setSelectedItem(client.getScale());
        }
    }

    private boolean hasImage() {
        return subject.getImage() != null && !subject.getImage().isBlank();
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
            .require("Email", txtEmail);
        // password not required: blank = keep current
        // username not validated: read-only

        if (subject instanceof Client || subject instanceof Employee) {
            validator
                .require("NIF", txtNif)
                .require("Phone", txtPhone)
                .require("Address", txtAddress);
        }

        if (subject instanceof Client) {
            validator.require("Sector", txtSector);
        }

        if (!validator.isValid()) {
            JOptionPane.showMessageDialog(null, validator.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String uploadImageIfChanged() throws Exception {
        return (selectedImage != null) ? ImageService.upload(selectedImage) : subject.getImage();
    }

    private void submit() {
        if (!validateFields()) return;

        String imagePath;
        try {
            imagePath = uploadImageIfChanged();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Image upload failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User updated = buildUpdatedUser(imagePath);

        try {
            DatabaseManager.getInstance().updateUser(updated);
            JOptionPane.showMessageDialog(null, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            back();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private User buildUpdatedUser(String imagePath) {
        String enteredPassword = new String(txtPassword.getPassword());
        String password = enteredPassword.isBlank() ? subject.getPassword() : enteredPassword;

        if (subject instanceof Employee emp) {
            return new Employee.Builder()
                .setId(subject.getUserId())
                .setName(txtName.getText())
                .setUsername(subject.getUsername())
                .setPassword(password)
                .setEmail(txtEmail.getText())
                .setImage(imagePath)
                .setNif(txtNif.getText())
                .setPhone(txtPhone.getText())
                .setAddress(txtAddress.getText())
                .setSpecialization(String.valueOf(cmbSpecialization.getSelectedItem()))
                .setStartDate(emp.getStartDate())
                .setStatus(subject.getStatus())
                .build();
        } else if (subject instanceof Client client) {
            return new Client.Builder()
                .setId(subject.getUserId())
                .setName(txtName.getText())
                .setUsername(subject.getUsername())
                .setPassword(password)
                .setEmail(txtEmail.getText())
                .setImage(imagePath)
                .setNif(txtNif.getText())
                .setPhone(txtPhone.getText())
                .setAddress(txtAddress.getText())
                .setSector(txtSector.getText())
                .setScale(String.valueOf(cmbScale.getSelectedItem()))
                .setStatus(subject.getStatus())
                .build();
        } else {
            return new Admin.Builder()
                .setId(subject.getUserId())
                .setName(txtName.getText())
                .setUsername(subject.getUsername())
                .setPassword(password)
                .setEmail(txtEmail.getText())
                .setImage(imagePath)
                .setStatus(subject.getStatus())
                .build();
        }
    }
}