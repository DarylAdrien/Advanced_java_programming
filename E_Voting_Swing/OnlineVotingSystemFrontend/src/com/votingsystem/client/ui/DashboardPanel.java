

package com.votingsystem.client.ui;

import com.votingsystem.client.util.ApiClient;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private OnlineVotingApp parentFrame;
    private JLabel welcomeLabel;
    private JLabel roleLabel;
    private JLabel userIdLabel;
    private JButton logoutButton;
    private JButton otpVerifyButton;
    private JTabbedPane roleSpecificTabs;

    private String loggedInUsername;
    private String loggedInRoleName;
    private int loggedInUserId;
    private int loggedInRoleId;

    // Role IDs (as per your database setup)
    private static final int ADMIN_ROLE_ID = 1;
    private static final int VOTER_ROLE_ID = 2;
    private static final int CANDIDATE_ROLE_ID = 3;

    public DashboardPanel(OnlineVotingApp parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(15, 15)); // Increased gaps for better spacing
        setBackground(new Color(245, 245, 250)); // Thistle-like light color for background

        // --- Header Panel (Top) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180)); // SteelBlue, consistent with other panels
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20)); // More padding

        // User Info Panel (Left side of header)
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false); // Make it transparent
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS)); // Stack labels vertically
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomeLabel = new JLabel("Welcome, ");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26)); // Larger, more prominent
        welcomeLabel.setForeground(Color.WHITE);
        userInfoPanel.add(welcomeLabel);

        roleLabel = new JLabel("Role: ");
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        roleLabel.setForeground(new Color(220, 220, 220)); // Lighter gray for secondary info
        userInfoPanel.add(roleLabel);

        userIdLabel = new JLabel("User ID: ");
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdLabel.setForeground(new Color(200, 200, 200)); // Even lighter gray
        userInfoPanel.add(userIdLabel);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);

        // Action Buttons Panel (Right side of header)
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); // Spacing between buttons
        actionButtonPanel.setOpaque(false); // Make it transparent

//        otpVerifyButton = new JButton("Verify 2-Step (OTP)");
//        styleActionButton(otpVerifyButton, new Color(255, 165, 0)); // Orange for attention
//        actionButtonPanel.add(otpVerifyButton);

        logoutButton = new JButton("Logout");
        styleActionButton(logoutButton, new Color(220, 50, 60)); // A more vibrant red
        actionButtonPanel.add(logoutButton);

        headerPanel.add(actionButtonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- Central Tabbed Pane for Role-Specific Content ---
        roleSpecificTabs = new JTabbedPane(JTabbedPane.TOP); // Tabs at the top
        roleSpecificTabs.setFont(new Font("Arial", Font.BOLD, 15)); // Slightly larger tab font
        roleSpecificTabs.setForeground(new Color(25, 25, 112)); // Dark blue for active tabs
        roleSpecificTabs.setBackground(new Color(220, 230, 240)); // Light blue for tab background

        // Add a nice border around the tabbed pane area
        JPanel tabbedPaneWrapper = new JPanel(new BorderLayout());
        tabbedPaneWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15)); // Padding around tabs
        tabbedPaneWrapper.setBackground(getBackground()); // Match dashboard background
        tabbedPaneWrapper.add(roleSpecificTabs, BorderLayout.CENTER);
        add(tabbedPaneWrapper, BorderLayout.CENTER);

        addListeners();
    }

    private void addListeners() {
        logoutButton.addActionListener(e -> attemptLogout());
//        otpVerifyButton.addActionListener(e -> showOtpVerificationDialog());
    }

    public void setLoggedInUser(String username, String roleName, int userId, int roleId) {
        this.loggedInUsername = username;
        this.loggedInRoleName = roleName;
        this.loggedInUserId = userId;
        this.loggedInRoleId = roleId;

        welcomeLabel.setText("Welcome, " + username + "!");
        roleLabel.setText("Role: " + roleName);
        userIdLabel.setText("User ID: " + userId);

        // Clear existing tabs before adding new ones
        roleSpecificTabs.removeAll();

        // Populate tabs based on role
        if (loggedInRoleId == ADMIN_ROLE_ID) {
            roleSpecificTabs.addTab("Election Management", createTabPanel("Manage Elections and Parameters", new ElectionManagementPanel(this)));
            roleSpecificTabs.addTab("Candidate Approvals", createTabPanel("Approve Candidate Registrations", new CandidateApprovalPanel(this)));
            roleSpecificTabs.addTab("View Results", createTabPanel("See Election Outcomes", new ResultsPanel(this)));
            // roleSpecificTabs.addTab("User Management", createTabPanel("Administer User Accounts", new UserManagementPanel(this))); // Future panel
        } else if (loggedInRoleId == VOTER_ROLE_ID) {
            roleSpecificTabs.addTab("Cast Vote", createTabPanel("Participate in Ongoing Elections", new VoterPanel(this)));
            roleSpecificTabs.addTab("View Results", createTabPanel("See Election Outcomes", new ResultsPanel(this)));
        } else if (loggedInRoleId == CANDIDATE_ROLE_ID) {
            roleSpecificTabs.addTab("My Candidacy", createTabPanel("Manage Your Candidacy Profile", new CandidatePanel(this)));
            roleSpecificTabs.addTab("View Results", createTabPanel("See Election Outcomes", new ResultsPanel(this)));
        } 
    }

    // Helper method to wrap tab content with a styled panel
    private JPanel createTabPanel(String titleText, JPanel contentPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE); // White background for the content area
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 1), // LightBlue border
            titleText, // Title for the panel
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            new Color(25, 25, 112)
        ));
        wrapper.add(contentPanel, BorderLayout.CENTER);
        return wrapper;
    }


    private void attemptLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Disable buttons during logout process
            logoutButton.setEnabled(false);
//            otpVerifyButton.setEnabled(false);

            new SwingWorker<JSONObject, Void>() {
                @Override
                protected JSONObject doInBackground() throws Exception {
                    // Assuming you might add a /logout endpoint later for server-side session invalidation
                    // For now, client-side clear session is enough as per previous discussion.
                    // If you implement a /logout API, call ApiClient.post("/logout", new HashMap<>()); here.
                    ApiClient.clearSession(); // Clear client-side session/cookies
                    return new JSONObject().put("success", true).put("message", "Logged out successfully.");
                }

                @Override
                protected void done() {
                    logoutButton.setEnabled(true);
//                    otpVerifyButton.setEnabled(true);
                    try {
                        JSONObject response = get();
                        JOptionPane.showMessageDialog(DashboardPanel.this, response.getString("message"), "Logout", JOptionPane.INFORMATION_MESSAGE);
                        parentFrame.showPanel("Landing"); // Go back to landing page
                        clearUserInfo(); // Clear user info on logout
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(DashboardPanel.this, "An error occurred during logout: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void showOtpVerificationDialog() {
        // Disable buttons during OTP process
        otpVerifyButton.setEnabled(false);
        logoutButton.setEnabled(false);

        // First, request an OTP from the server
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "generate");
                return ApiClient.post("/otp", params); // Use POST for generating OTP
            }

            @Override
            protected void done() {
                otpVerifyButton.setEnabled(true); // Re-enable generate button after API call
                logoutButton.setEnabled(true);

                try {
                    JSONObject response = get();
                    if (response != null && response.optBoolean("success", false)) {
                        JOptionPane.showMessageDialog(DashboardPanel.this,
                                response.optString("message", "OTP sent successfully!"), "OTP Generation", JOptionPane.INFORMATION_MESSAGE);

                        // Now, prompt user for OTP
                        String otpEntered = JOptionPane.showInputDialog(DashboardPanel.this,
                                "Enter the OTP sent to your registered contact (email/phone):",
                                "OTP Verification", JOptionPane.PLAIN_MESSAGE);

                        if (otpEntered != null && !otpEntered.trim().isEmpty()) {
                            verifyOtp(otpEntered.trim());
                        } else {
                            JOptionPane.showMessageDialog(DashboardPanel.this, "OTP entry cancelled or empty.", "OTP Verification", JOptionPane.WARNING_MESSAGE);
                        }

                    } else {
                        String message = (response != null) ? response.optString("message", "Failed to generate OTP.") : "No response from server.";
                        JOptionPane.showMessageDialog(DashboardPanel.this, "OTP Generation Failed: " + message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DashboardPanel.this, "An error occurred during OTP generation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void verifyOtp(String otp) {
        // Disable buttons during verification
        otpVerifyButton.setEnabled(false);
        logoutButton.setEnabled(false);

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> formData = new HashMap<>();
                formData.put("action", "verify");
                formData.put("otpCode", otp);
                return ApiClient.post("/otp", formData);
            }

            @Override
            protected void done() {
                // Re-enable buttons
                otpVerifyButton.setEnabled(true);
                logoutButton.setEnabled(true);

                try {
                    JSONObject response = get();
                    if (response != null && response.optBoolean("success", false)) {
                        JOptionPane.showMessageDialog(DashboardPanel.this,
                                response.optString("message", "OTP verified successfully!"), "OTP Verification", JOptionPane.INFORMATION_MESSAGE);
                        // No specific UI update needed here, as the backend session already marks OTP as verified
                        // Subsequent sensitive actions will now pass the OTP check.
                    } else {
                        String message = (response != null) ? response.optString("message", "Failed to verify OTP.") : "No response from server.";
                        JOptionPane.showMessageDialog(DashboardPanel.this, "OTP Verification Failed: " + message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DashboardPanel.this, "An error occurred during OTP verification: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void styleActionButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Slightly larger and bolder
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1), // Darker border for depth
            new EmptyBorder(8, 20, 8, 20) // More padding
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect"); // For some LaF
    }

    private void clearUserInfo() {
        welcomeLabel.setText("Welcome, Guest!");
        roleLabel.setText("Role: N/A");
        userIdLabel.setText("User ID: N/A");
        roleSpecificTabs.removeAll(); // Clear all tabs
    }

    // Getters for role/user ID, useful for other panels
    public int getLoggedInRoleId() {
        return loggedInRoleId;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }
}