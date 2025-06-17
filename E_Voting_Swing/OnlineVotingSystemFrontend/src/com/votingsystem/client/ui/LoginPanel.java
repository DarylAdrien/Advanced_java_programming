
package com.votingsystem.client.ui;

import com.votingsystem.client.util.ApiClient;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LoginPanel extends JPanel {
    private OnlineVotingApp parentFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerNowButton; // New button for direct registration
    private JButton backButton;

    public LoginPanel(OnlineVotingApp parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout()); // Use BorderLayout for overall structure
        setBackground(new Color(240, 248, 255)); // AliceBlue, consistent with RegistrationPanel

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        headerPanel.setBackground(new Color(70, 130, 180)); // SteelBlue, consistent with RegistrationPanel
        JLabel titleLabel = new JLabel("Welcome Back! Login to Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel (Card-like container for the form) ---
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(Color.WHITE); // White background for the card
        mainContentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 2), // LightBlue border
            new EmptyBorder(40, 50, 40, 50) // Increased padding
        ));
        // Wrap the main content panel in another panel to center it
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(new Color(240, 248, 255)); // Match main background
        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.anchor = GridBagConstraints.CENTER;
        wrapperGbc.insets = new Insets(50, 0, 50, 0); // Vertical spacing for the card
        wrapperPanel.add(mainContentPanel, wrapperGbc);
        add(wrapperPanel, BorderLayout.CENTER);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10); // Increased insets for better spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow fields to expand horizontally

        int row = 0;

        // Username
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel usernameLabel = new JLabel("Username:");
        styleLabel(usernameLabel);
        mainContentPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(25); // Increased field size
        styleTextField(usernameField);
        gbc.gridx = 1;
        mainContentPanel.add(usernameField, gbc);
        row++;

        // Password
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel passwordLabel = new JLabel("Password:");
        styleLabel(passwordLabel);
        mainContentPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(25); // Increased field size
        styleTextField(passwordField);
        gbc.gridx = 1;
        mainContentPanel.add(passwordField, gbc);
        row++;

        // Login Button
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10); // More space above login button
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(34, 139, 34)); // ForestGreen
        mainContentPanel.add(loginButton, gbc);
        row++;

        // "Don't have an account?" text
        gbc.gridy = row;
        gbc.insets = new Insets(10, 10, 5, 10); // Adjust padding
        JLabel noAccountLabel = new JLabel("<html>Don't have an account?</html>", SwingConstants.CENTER);
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        noAccountLabel.setForeground(new Color(50, 50, 50));
        mainContentPanel.add(noAccountLabel, gbc);
        row++;

        // Register Now Button
        gbc.gridy = row;
        gbc.insets = new Insets(5, 10, 10, 10); // Adjust padding
        registerNowButton = new JButton("Register Now");
        styleButton(registerNowButton, new Color(70, 130, 180)); // SteelBlue
        mainContentPanel.add(registerNowButton, gbc);
        row++;

        // Back Button
        gbc.gridy = row;
        gbc.insets = new Insets(20, 10, 10, 10); // More space above back button
        backButton = new JButton("Back to Home");
        styleButton(backButton, new Color(105, 105, 105)); // DimGray
        mainContentPanel.add(backButton, gbc);
        row++;


        addListeners();
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Bold labels
        label.setForeground(new Color(25, 25, 112)); // MidnightBlue
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)), // LightBlue border
            new EmptyBorder(5, 8, 5, 8) // Inner padding
        ));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 17)); // Slightly larger font
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1), // Darker border
            new EmptyBorder(12, 30, 12, 30) // More padding
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect"); // For some LaF
    }

    private void addListeners() {
        loginButton.addActionListener(e -> attemptLogin());
        registerNowButton.addActionListener(e -> parentFrame.showPanel("Register")); // Link to RegistrationPanel
        backButton.addActionListener(e -> parentFrame.showPanel("Landing"));
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim(); // Trim whitespace
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both your username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, String> formData = new HashMap<>();
        formData.put("username", username);
        formData.put("password", password);

        // Disable buttons during API call
        loginButton.setEnabled(false);
        registerNowButton.setEnabled(false);
        backButton.setEnabled(false);

        // Execute API call in a separate thread to avoid freezing the UI
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return ApiClient.post("/login", formData);
            }

            @Override
            protected void done() {
                // Re-enable buttons
                loginButton.setEnabled(true);
                registerNowButton.setEnabled(true);
                backButton.setEnabled(true);

                try {
                    JSONObject response = get();
                    if (response != null && response.optBoolean("success", false)) { // Use optBoolean for safer parsing
                        int userId = response.optInt("userId");
                        String roleName = response.optString("roleName");
                        int roleId = response.optInt("roleId");
                        boolean twoFactorEnabled = response.optBoolean("twoFactorEnabled", false);

                        JOptionPane.showMessageDialog(LoginPanel.this, response.optString("message", "Login successful!"), "Login Success", JOptionPane.INFORMATION_MESSAGE);

                        // In a real application, if twoFactorEnabled is true,
                        // you would typically navigate to an OTP verification panel here.
                        // For this example, we'll proceed to the dashboard,
                        // assuming OTP is handled in a subsequent step or for specific actions.
//                        parentFrame.navigateToOtpVerification(userId, roleName, roleId);
                        parentFrame.onLoginSuccess(username, roleName, userId, roleId);
                        clearFields(); // Clear fields after successful login
                    } else {
                        String message = (response != null) ? response.optString("message", "Invalid username or password.") : "No response from server.";
                        JOptionPane.showMessageDialog(LoginPanel.this, "Login Failed: " + message, "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginPanel.this, "An unexpected error occurred during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}