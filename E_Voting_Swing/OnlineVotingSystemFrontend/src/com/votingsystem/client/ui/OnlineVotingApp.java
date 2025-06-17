

// src/com/votingsystem/client/ui/OnlineVotingApp.java
package com.votingsystem.client.ui;

import javax.swing.*;
import java.awt.*;
// Removed unused imports: ActionEvent, ActionListener as they are now handled by lambdas or within LandingPagePanel

public class OnlineVotingApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegistrationPanel registrationPanel;
    private DashboardPanel dashboardPanel; // Placeholder for the main dashboard after login

    // New LandingPagePanel instance
    private LandingPagePanel landingPagePanel;

    public OnlineVotingApp() {
        setTitle("Online Voting System");
        setSize(1000, 750); // Increased size to accommodate more content
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels (pass this JFrame reference for navigation)
        loginPanel = new LoginPanel(this);
        registrationPanel = new RegistrationPanel(this);
        dashboardPanel = new DashboardPanel(this); // Assuming DashboardPanel exists
        landingPagePanel = new LandingPagePanel(this); // Initialize the new landing page

        // Add panels to mainPanel with unique names
        // Use the new LandingPagePanel instead of the old landingPanel
        mainPanel.add(landingPagePanel, "Landing");
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(registrationPanel, "Register");
        mainPanel.add(dashboardPanel, "Dashboard");

        add(mainPanel);
        cardLayout.show(mainPanel, "Landing"); // Show the new landing page first
    }

    // This helper method is no longer directly used in OnlineVotingApp's constructor for main buttons
    // but is kept as an example or if other panels use it.
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setPreferredSize(new Dimension(150, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Navigates to the specified panel.
     * @param panelName The name of the panel to show (e.g., "Login", "Register", "Dashboard", "Landing").
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        // Reset fields when navigating back to login/register, if needed
        if ("Login".equals(panelName)) {
            loginPanel.clearFields();
        } else if ("Register".equals(panelName)) {
            registrationPanel.clearFields();
        }
    }

    /**
     * Method to be called upon successful login.
     * @param username The username of the logged-in user.
     * @param roleName The role name of the logged-in user.
     * @param userId The ID of the logged-in user.
     */
    public void onLoginSuccess(String username, String roleName, int userId, int roleId) {
        dashboardPanel.setLoggedInUser(username, roleName, userId, roleId);
        showPanel("Dashboard");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OnlineVotingApp().setVisible(true);
        });
    }
}