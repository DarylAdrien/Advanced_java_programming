

package com.votingsystem.client.ui;

import com.votingsystem.client.util.ApiClient;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPanel extends JPanel {
    private OnlineVotingApp parentFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressField; // Changed to JTextArea for multi-line address
    private JComboBox<String> roleComboBox; // For selecting role
    private JButton registerButton;
    private JButton backButton;
    private JTextField aadhaarField;

    // Email pattern for basic validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    // Phone pattern (basic, adjust as needed for specific regions)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s()-]{7,20}$");
    // Aadhaar pattern (12 digits)
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^\\d{7}$");


    public RegistrationPanel(OnlineVotingApp parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout()); // Use BorderLayout for overall structure
        setBackground(new Color(240, 248, 255)); // AliceBlue for a softer look

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        headerPanel.setBackground(new Color(70, 130, 180)); // SteelBlue
        JLabel titleLabel = new JLabel("New User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel (Scrollable) ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); // More padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased insets for better spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow fields to expand horizontally

        int row = 0;

        // Username
        addFormField(contentPanel, gbc, "Username:", usernameField = new JTextField(25), row++);

        // Password
        addFormField(contentPanel, gbc, "Password:", passwordField = new JPasswordField(25), row++);

        // Confirm Password
        addFormField(contentPanel, gbc, "Confirm Password:", confirmPasswordField = new JPasswordField(25), row++);

        // Full Name
        addFormField(contentPanel, gbc, "Full Name:", fullNameField = new JTextField(25), row++);

        // Email
        addFormField(contentPanel, gbc, "Email:", emailField = new JTextField(25), row++);

        // Phone
        addFormField(contentPanel, gbc, "Phone:", phoneField = new JTextField(25), row++);

        // Address (using JScrollPane for JTextArea)
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel addressLabel = new JLabel("Address:");
        styleLabel(addressLabel);
        contentPanel.add(addressLabel, gbc);

        addressField = new JTextArea(4, 25); // 4 rows, 25 columns for address
        addressField.setFont(new Font("Arial", Font.PLAIN, 16));
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        JScrollPane addressScrollPane = new JScrollPane(addressField);
        addressScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addressScrollPane.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230))); // LightBlue border
        gbc.gridx = 1;
        gbc.weighty = 0.5; // Give address field some vertical weight
        gbc.insets = new Insets(10, 10, 10, 10); // Reset insets if needed
        contentPanel.add(addressScrollPane, gbc);
        row++;
        gbc.weighty = 0; // Reset weighty

        // Aadhaar Number
        addFormField(contentPanel, gbc, "Voter ID:", aadhaarField = new JTextField(25), row++);

        // Role (Limited for self-registration: Voter or Candidate)
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel roleLabel = new JLabel("Register As:");
        styleLabel(roleLabel);
        contentPanel.add(roleLabel, gbc);

        String[] roles = {"Voter", "Candidate"}; // Assuming roles 2 (Voter) and 3 (Candidate)
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setForeground(new Color(25, 25, 112)); // MidnightBlue
        gbc.gridx = 1;
        contentPanel.add(roleComboBox, gbc);
        row++;

        // --- Buttons Panel ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); // FlowLayout for buttons
        buttonsPanel.setBackground(new Color(240, 248, 255));

        registerButton = new JButton("Register Now");
        styleButton(registerButton, new Color(46, 139, 87)); // SeaGreen
        buttonsPanel.add(registerButton);

        backButton = new JButton("Back to Home");
        styleButton(backButton, new Color(105, 105, 105)); // DimGray
        buttonsPanel.add(backButton);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10); // More space above buttons
        contentPanel.add(buttonsPanel, gbc);


        // Add contentPanel to a JScrollPane if the form might exceed screen height
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        addListeners();
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        styleLabel(label);
        panel.add(label, gbc);

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)), // LightBlue border
            new EmptyBorder(5, 8, 5, 8) // Inner padding
        ));
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Bold labels
        label.setForeground(new Color(25, 25, 112)); // MidnightBlue
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
        registerButton.addActionListener(e -> attemptRegistration());
        backButton.addActionListener(e -> parentFrame.showPanel("Landing"));
    }

    private void attemptRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim(); // Get text from JTextArea
        String aadhaarNumber = aadhaarField.getText().trim();
        String selectedRole = (String) roleComboBox.getSelectedItem();

        // --- Enhanced Validation ---
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty() ||
            email.isEmpty() || phone.isEmpty() || address.isEmpty() || aadhaarNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match. Please re-enter.", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 8) { // Increased minimum password length for better security
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Basic password complexity (at least one digit, one lowercase, one uppercase)
        if (!Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$").matcher(password).matches()) {
             JOptionPane.showMessageDialog(this, "Password must contain at least one digit, one lowercase, and one uppercase letter.", "Registration Error", JOptionPane.WARNING_MESSAGE);
             return;
        }


        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g., example@domain.com).", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number (7-20 digits, may include +,-,(), spaces).", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isValidAadhaar(aadhaarNumber)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 7-digit Voter ID.", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return;
        }


        // Map selected role string to its ID (as per your DB schema)
        // Voter -> 2, Candidate -> 3
        int roleId;
        if ("Voter".equals(selectedRole)) {
            roleId = 2;
        } else if ("Candidate".equals(selectedRole)) {
            roleId = 3;
        } else {
            JOptionPane.showMessageDialog(this, "Invalid role selected.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        Map<String, String> formData = new HashMap<>();
        formData.put("username", username);
        formData.put("password", password);
        formData.put("fullName", fullName);
        formData.put("email", email);
        formData.put("phone", phone);
        formData.put("address", address);
        formData.put("aadhaarNumber", aadhaarNumber);
        formData.put("roleId", String.valueOf(roleId));

        // Disable button during API call
        registerButton.setEnabled(false);
        backButton.setEnabled(false);

        // Execute API call in a separate thread
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return ApiClient.post("/register", formData);
            }

            @Override
            protected void done() {
                registerButton.setEnabled(true); // Re-enable button
                backButton.setEnabled(true);     // Re-enable button
                try {
                    JSONObject response = get();
                    if (response != null && response.optBoolean("success", false)) { // Use optBoolean for safer parsing
                        JOptionPane.showMessageDialog(RegistrationPanel.this, response.optString("message", "Registration successful!"), "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                        clearFields(); // Clear fields on successful registration
                        parentFrame.showPanel("Login"); // Go to login after successful registration
                    } else {
                        String message = (response != null) ? response.optString("message", "Unknown error occurred.") : "No response from server.";
                        JOptionPane.showMessageDialog(RegistrationPanel.this, "Registration Failed: " + message, "Registration Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "An error occurred during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhone(String phone) {
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        return matcher.matches();
    }

    private boolean isValidAadhaar(String aadhaar) {
        Matcher matcher = AADHAAR_PATTERN.matcher(aadhaar);
        return matcher.matches();
    }


    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText(""); // Clear JTextArea
        aadhaarField.setText("");
        roleComboBox.setSelectedIndex(0); // Reset to first role
    }
}