package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.User;
import thread.ClockThread;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private JLabel clockLabel;
    private ClockThread clockThread;

    // Builds the main window and adds the login, register and dashboard cards.
    public MainFrame() {
        setTitle("Digital Diary Management System");
        setSize(950, 650);
        setMinimumSize(new java.awt.Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);

        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(registerPanel, "REGISTER");

        add(cardPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clockLabel = new JLabel("Starting clock...");
        statusPanel.add(clockLabel);
        add(statusPanel, BorderLayout.SOUTH);

        clockThread = new ClockThread(clockLabel);
        clockThread.setDaemon(true);
        clockThread.start();

        addWindowListener(new WindowAdapter() {
            // Stops the clock thread when the window closes.
            @Override
            public void windowClosing(WindowEvent e) {
                if (clockThread != null) {
                    clockThread.stopClock();
                }
            }
        });

        showLogin();
    }

    // Shows the login screen.
    public void showLogin() {
        cardLayout.show(cardPanel, "LOGIN");
    }

    // Shows the registration screen.
    public void showRegister() {
        cardLayout.show(cardPanel, "REGISTER");
    }

    // Creates and shows the dashboard for the logged-in user.
    public void showDashboard(User user) {
        if (dashboardPanel != null) {
            cardPanel.remove(dashboardPanel);
        }

        dashboardPanel = new DashboardPanel(this, user);
        cardPanel.add(dashboardPanel, "DASHBOARD");
        cardLayout.show(cardPanel, "DASHBOARD");
    }

    // Logs out the current user and returns to the login screen.
    public void logout() {
        showLogin();
    }
}
