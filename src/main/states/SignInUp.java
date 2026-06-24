package main.states;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class SignInUp extends State {
    @Override
    public JPanel buildView() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton signInBtn = new JButton("Sign in");
        // signInBtn.addActionListener(e -> next(new SignIn())); // TODO: uncomment when SignIn is implemented

        JButton signUpBtn = new JButton("Sign up");
        // signUpBtn.addActionListener(e -> next(new SignUp())); // TODO: uncomment when SignUp is implemented

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> State.exit());

        panel.add(signInBtn);
        panel.add(signUpBtn);
        panel.add(exitBtn);
        return panel;
    }
}