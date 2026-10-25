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
import exception.InvalidLoginException;
import model.User;

public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    // Builds the login screen and connects its buttons.
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout());

        JLabel headingLabel = new JLabel("Welcome to Digital Diary", JLabel.CENTER);
        headingLabel.setFont(headingLabel.getFont().deriveFont(24.0f));

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        loginButton = new JButton("Login");
        registerButton = new JButton("Create Account");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 180, 80, 180));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        add(headingLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            // Tries to log in with the entered details.
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            // Opens the registration screen.
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showRegister();
            }
        });
    }

    // Validates the fields and asks the UserDAO to log in.
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both a username and a password.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userDAO.login(username, password);
            passwordField.setText("");
            mainFrame.showDashboard(user);
        } catch (InvalidLoginException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to the database.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
