package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.UserDAO;
import exception.DuplicateUserException;
import model.User;

public class RegisterPanel extends JPanel {

    private MainFrame mainFrame;
    private UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;

    // Builds the registration screen and connects its buttons.
    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout());

        JLabel headingLabel = new JLabel("Create a New Account", JLabel.CENTER);
        headingLabel.setFont(headingLabel.getFont().deriveFont(24.0f));

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        confirmPasswordField = new JPasswordField(18);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);

        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 160, 80, 160));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        add(headingLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        registerButton.addActionListener(new ActionListener() {
            // Validates the form and registers the new user.
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        backButton.addActionListener(new ActionListener() {
            // Returns to the login screen.
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
                mainFrame.showLogin();
            }
        });
    }

    // Validates the registration fields and creates the account.
    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in every field.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "The username must have at least 3 characters.",
                    "Invalid Username",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() > 50) {
            JOptionPane.showMessageDialog(this,
                    "The username cannot be longer than 50 characters.",
                    "Invalid Username",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "The password must have at least 4 characters.",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() > 100) {
            JOptionPane.showMessageDialog(this,
                    "The password cannot be longer than 100 characters.",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "The two passwords do not match.",
                    "Password Mismatch",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(username, password);

        try {
            userDAO.register(user);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully. Please log in.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            mainFrame.showLogin();
        } catch (DuplicateUserException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Username Taken",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to the database.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clears all text fields on the registration form.
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}
