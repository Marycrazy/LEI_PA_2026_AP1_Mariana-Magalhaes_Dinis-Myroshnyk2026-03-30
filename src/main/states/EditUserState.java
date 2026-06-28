package main.states;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
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
    private static final int PHOTO_SIZE = 150;

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
    private JLabel lblPhoto;

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

        lblPhoto = new JLabel("Loading...", SwingConstants.CENTER);
        lblPhoto.setPreferredSize(new Dimension(PHOTO_SIZE, PHOTO_SIZE));
        loadCurrentPhoto();

        JButton btnChooseImage = new JButton("Change photo...");
        btnChooseImage.addActionListener(e -> chooseImage());

        JPanel photoPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        photoPanel.add(lblPhoto);
        photoPanel.add(btnChooseImage);
        form.addFullWidthRow(photoPanel);

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
        // password is left blank

        if (subject instanceof Employee emp) {
            txtNif.setText(emp.getNif());
            txtNif.setEditable(false);
            txtPhone.setText(emp.getPhone());
            txtAddress.setText(emp.getAddress());
            cmbSpecialization.setSelectedItem(Integer.valueOf(emp.getSpecialization()));
        } else if (subject instanceof Client client) {
            txtNif.setText(client.getNif());
            txtNif.setEditable(false);
            txtPhone.setText(client.getPhone());
            txtAddress.setText(client.getAddress());
            txtSector.setText(client.getSector());
            cmbScale.setSelectedItem(client.getScale());
        }
    }

    private void loadCurrentPhoto() {
        String filename = subject.getImage();
        if (filename == null || filename.isBlank()) {
            lblPhoto.setText("No photo");
            return;
        }

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                Image raw = ImageService.download(filename);
                return new ImageIcon(scaleToFit(raw, PHOTO_SIZE));
            }

            @Override
            protected void done() {
                try {
                    lblPhoto.setIcon(get());
                    lblPhoto.setText(null);
                } catch (Exception e) {
                    lblPhoto.setText("No photo");
                }
            }
        }.execute();
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedImage = chooser.getSelectedFile();
            showLocalPreview(selectedImage);
        }
    }

    private void showLocalPreview(File file) {
        try {
            Image raw = javax.imageio.ImageIO.read(file);
            if (raw == null) throw new java.io.IOException("Unsupported image");
            lblPhoto.setIcon(new ImageIcon(scaleToFit(raw, PHOTO_SIZE)));
            lblPhoto.setText(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not preview image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            selectedImage = null;
        }
    }

    private Image scaleToFit(Image image, int maxSize) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);

        double scale = Math.min((double) maxSize / w, (double) maxSize / h);
        int newW = Math.max(1, (int) (w * scale));
        int newH = Math.max(1, (int) (h * scale));

        return image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }

    private boolean validateFields() {
        FormValidator validator = new FormValidator()
            .require("Name", txtName)
            .dbValidate("Email", txtEmail, "fn::check_email");

        if (subject instanceof Client || subject instanceof Employee) {
            validator
                .dbValidate("Phone", txtPhone, "fn::check_phone")
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

            if (subject.getUsername().equals(State.user.getUsername())) {
                State.user = updated;
            }

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
                .setNif(emp.getNif())
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
                .setNif(client.getNif())
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