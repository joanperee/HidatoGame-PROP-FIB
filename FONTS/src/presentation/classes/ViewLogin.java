package presentation.classes;

import presentation.controller.PresentationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login and register class which is a subclass of JFrame.
 * All other views are put on top of this frame.
 */
public class ViewLogin extends JFrame implements ActionListener {
    /** Instance of PresentationController to talk with it. */
    private PresentationController presentationController;

    // All global GUI components used
    private JTextField textFieldUser;
    private JPasswordField passwordField;
    private JLabel confirmLabel;
    private JPasswordField passwordField2;
    private JButton buttonLogin;
    private JButton buttonRegister;

    /** Current state of the login/register. */
    private boolean isRegistering = false;

    /**
     * Default constructor and initializes all components.
     * 
     * @param presentationController
     */
    public ViewLogin(PresentationController presentationController) {
        this.presentationController = presentationController;
        initComponents();
    }

    /**
     * Initializes the layout and the components. Those are the
     * basic ones used in any login/register form.
     */
    private void initComponents() {
        setTitle("HIDATO GAME LOGIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel labelTitle = new JLabel("HIDATO GAME LOGIN");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(10, 10, 20, 10);
        add(labelTitle, c);

        JLabel labelUsername = new JLabel("Username: ");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);
        add(labelUsername, c);

        textFieldUser = new JTextField(15);
        c.gridx = 1;
        c.gridy = 1;
        add(textFieldUser, c);

        JLabel labelPassword = new JLabel("Password: ");
        c.gridx = 0;
        c.gridy = 2;
        add(labelPassword, c);

        passwordField = new JPasswordField(15);
        c.gridx = 1;
        c.gridy = 2;
        add(passwordField, c);

        confirmLabel = new JLabel("Confirm password: ");
        c.gridx = 0;
        c.gridy = 3;
        confirmLabel.setVisible(false);
        add(confirmLabel, c);

        passwordField2 = new JPasswordField(15);
        c.gridx = 1;
        c.gridy = 3;
        passwordField2.setVisible(false);
        add(passwordField2, c);

        buttonLogin = new JButton("Enter");
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(buttonLogin, c);
        buttonLogin.addActionListener(this);

        buttonRegister = new JButton("Register");
        c.gridx = 0;
        c.gridy = 5;
        add(buttonRegister, c);
        buttonRegister.addActionListener(this);
    }

    /**
     * When the user logs out this function is triggered to
     * change isRegistering boolean to false and repaint components.
     */
    public void reinitComponents() {
        isRegistering = false;
        initComponents();
    }

    /**
     * Treatment for all action listeners implemented.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        String username = textFieldUser.getText();
        String psswd = new String(passwordField.getPassword());

        if (button.equals(buttonLogin)) {
            if (isRegistering) {
                setRegisterMode(false);
            } else {
                presentationController.login(username, psswd);
            }
        } else if (button.equals(buttonRegister)) {
            if (!isRegistering) {
                setRegisterMode(true);
            } else {
                String confirm = new String(passwordField2.getPassword());
                presentationController.register(username, psswd, confirm);
                setRegisterMode(false);
            }
        }
    }

    /**
     * Helper method to change between register and login modes.
     * 
     * @param mode Can be register mode (1) or login mode (0).
     */
    private void setRegisterMode(boolean mode) {
        isRegistering = mode;
        confirmLabel.setVisible(mode);
        passwordField2.setVisible(mode);
        if (mode) {
            buttonRegister.setText("Confirm Registration");
            buttonLogin.setText("Cancel");
        } else {
            buttonRegister.setText("Register");
            buttonLogin.setText("Enter");
            textFieldUser.setText("");
            passwordField.setText("");
        }
        revalidate();
        repaint();
    }
}