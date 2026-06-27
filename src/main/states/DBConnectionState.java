package main.states;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.DatabaseManager;
import main.PropertiesManager;
import main.utils.FormBuilder;
import main.utils.FormValidator;

public class DBConnectionState extends State {
    private JTextField txtConnect = new JTextField(textFieldCols);
    private JTextField txtNamespace = new JTextField(textFieldCols);
    private JTextField txtDatabase = new JTextField(textFieldCols);
    private JTextField txtUsername = new JTextField(textFieldCols);
    private JPasswordField txtPassword = new JPasswordField(textFieldCols);
    private JTextField txtEmail = new JTextField(textFieldCols);
    private JTextField txtKey = new JTextField(textFieldCols);
    private JTextField txtDownloadUrl = new JTextField(textFieldCols);
    private JTextField txtUploadUrl = new JTextField(textFieldCols);
    private JTextField txtUploadToken = new JTextField(textFieldCols);

    @Override
    public JPanel buildView() {
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> State.exit());

        JButton btnSubmit = new JButton("Save and connect");
        btnSubmit.addActionListener(e -> submit());

        return new FormBuilder("Database & Service Configuration")
            .addFullWidthRow(new JLabel("No properties file found. Enter connection details:"))
            .addField("Connection URL:", txtConnect)
            .addField("Namespace:", txtNamespace)
            .addField("Database:", txtDatabase)
            .addField("Username:", txtUsername)
            .addField("Password:", txtPassword)
            .addField("Email:", txtEmail)
            .addField("Email App Key:", txtKey)
            .addField("Download URL:", txtDownloadUrl)
            .addField("Upload URL:", txtUploadUrl)
            .addField("Upload Token:", txtUploadToken)
            .addButtonRow(btnExit, btnSubmit)
            .build();
    }

    private void submit() {
        FormValidator validator = new FormValidator()
            .require("Connection URL", txtConnect)
            .require("Namespace", txtNamespace)
            .require("Database", txtDatabase)
            .require("Username", txtUsername)
            .require("Password", txtPassword);

        if (!validator.isValid()) {
            JOptionPane.showMessageDialog(null, validator.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PropertiesManager props = new PropertiesManager();
        props.setProperty("connect", txtConnect.getText());
        props.setProperty("namespace", txtNamespace.getText());
        props.setProperty("database", txtDatabase.getText());
        props.setProperty("username", txtUsername.getText());
        props.setProperty("password", new String(txtPassword.getPassword()));
        props.setProperty("email", txtEmail.getText());
        props.setProperty("key", txtKey.getText());
        props.setProperty("download_url", txtDownloadUrl.getText());
        props.setProperty("upload_url", txtUploadUrl.getText());
        props.setProperty("upload_token", txtUploadToken.getText());

        if (!props.saveFile()) {
            JOptionPane.showMessageDialog(null, "Error saving database configuration.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DatabaseManager.getInstance().connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not connect to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        next(new SignInState());
    }
}