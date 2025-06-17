//// src/com/votingsystem/client/ui/CandidatePanel.java
//package com.votingsystem.client.ui;
//
//import com.votingsystem.client.util.ApiClient;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//
//public class CandidatePanel extends JPanel {
//    private DashboardPanel parentDashboard;
//    private JTabbedPane candidateTabs;
//
//    // My Candidacy Tab
//    private JTable myCandidacyTable;
//    private DefaultTableModel myCandidacyTableModel;
//
//    // Register Candidacy Tab
//    private JComboBox<String> electionComboBox;
//    private JTextField partyAffiliationField;
//    private JTextArea manifestoArea;
//    private Map<String, Integer> electionMap; // Map election name to ID
//
//    public CandidatePanel(DashboardPanel parentDashboard) {
//        this.parentDashboard = parentDashboard;
//        setLayout(new BorderLayout());
//        setBorder(BorderFactory.createTitledBorder("Candidate Management"));
//
//        candidateTabs = new JTabbedPane();
//        candidateTabs.setFont(new Font("Arial", Font.PLAIN, 14));
//
//        JPanel myCandidacyPanel = createMyCandidacyPanel();
//        JPanel registerCandidacyPanel = createRegisterCandidacyPanel();
//
//        candidateTabs.addTab("My Candidacy", myCandidacyPanel);
//        candidateTabs.addTab("Register for Election", registerCandidacyPanel);
//
//        candidateTabs.addChangeListener(e -> {
//            int selectedIndex = candidateTabs.getSelectedIndex();
//            if (selectedIndex == 0) { // "My Candidacy" tab
//                loadMyCandidacies();
//            } else if (selectedIndex == 1) { // "Register for Election" tab
//                loadAvailableElectionsForRegistration();
//            }
//        });
//
//        add(candidateTabs, BorderLayout.CENTER);
//    }
//
//    // --- My Candidacy Panel ---
//    private JPanel createMyCandidacyPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        myCandidacyTableModel = new DefaultTableModel(new String[]{"Candidacy ID", "Election Name", "Party", "Manifesto", "Status", "Registration Date"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        myCandidacyTable = new JTable(myCandidacyTableModel);
//        myCandidacyTable.getTableHeader().setReorderingAllowed(false);
//        JScrollPane scrollPane = new JScrollPane(myCandidacyTable);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        JButton refreshButton = new JButton("Refresh My Candidacies");
//        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
//        refreshButton.addActionListener(e -> loadMyCandidacies());
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        buttonPanel.add(refreshButton);
//        panel.add(buttonPanel, BorderLayout.SOUTH);
//
//        loadMyCandidacies(); // Initial load
//        return panel;
//    }
//
//    private void loadMyCandidacies() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                // Fetch all candidates and filter by current user's ID
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listAll"); // Candidate role can view their own, Admin sees all
//                return ApiClient.get("/candidate", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    myCandidacyTableModel.setRowCount(0); // Clear existing data
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray candidates = response.getJSONArray("candidates");
//                        int currentUserId = parentDashboard.getLoggedInUserId();
//                        for (int i = 0; i < candidates.length(); i++) {
//                            JSONObject candidate = candidates.getJSONObject(i);
//                            if (candidate.getInt("userId") == currentUserId) { // Filter for current user's candidacies
//                                Vector<Object> row = new Vector<>();
//                                row.add(candidate.getInt("candidateId"));
//                                row.add(candidate.getString("electionName"));
//                                row.add(candidate.optString("partyAffiliation", "N/A"));
//                                row.add(candidate.optString("manifesto", "N/A"));
//                                row.add(candidate.getString("approvalStatus"));
//                                row.add(candidate.getString("registrationDate"));
//                                myCandidacyTableModel.addRow(row);
//                            }
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(CandidatePanel.this, "Failed to load candidacies: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(CandidatePanel.this, "An error occurred loading candidacies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    // --- Register Candidacy Panel ---
//    private JPanel createRegisterCandidacyPanel() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        panel.setBackground(new Color(255, 250, 240)); // FloralWhite
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(8, 8, 8, 8);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel title = new JLabel("Register for an Election", SwingConstants.CENTER);
//        title.setFont(new Font("Arial", Font.BOLD, 20));
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(title, gbc);
//
//        gbc.gridwidth = 1; // Reset gridwidth
//
//        gbc.gridy++;
//        panel.add(new JLabel("Select Election:"), gbc);
//        gbc.gridx = 1;
//        electionComboBox = new JComboBox<>();
//        electionComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
//        panel.add(electionComboBox, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        panel.add(new JLabel("Party Affiliation:"), gbc);
//        gbc.gridx = 1;
//        partyAffiliationField = new JTextField(25);
//        panel.add(partyAffiliationField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        panel.add(new JLabel("Manifesto:"), gbc);
//        gbc.gridx = 1;
//        manifestoArea = new JTextArea(8, 25);
//        manifestoArea.setLineWrap(true);
//        manifestoArea.setWrapStyleWord(true);
//        JScrollPane scrollPane = new JScrollPane(manifestoArea);
//        panel.add(scrollPane, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.gridwidth = 2;
//        JButton registerButton = new JButton("Register Candidacy");
//        styleButton(registerButton, new Color(70, 130, 180)); // SteelBlue
//        registerButton.addActionListener(e -> registerCandidacy());
//        panel.add(registerButton, gbc);
//
//        loadAvailableElectionsForRegistration(); // Initial load
//        return panel;
//    }
//
//    private void loadAvailableElectionsForRegistration() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listElections");
//                params.put("status", "SCHEDULED"); // Only allow registration for scheduled elections
//                return ApiClient.get("/admin/election", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    electionComboBox.removeAllItems();
//                    electionMap = new HashMap<>(); // Reset map
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray elections = response.getJSONArray("elections");
//                        for (int i = 0; i < elections.length(); i++) {
//                            JSONObject election = elections.getJSONObject(i);
//                            String electionName = election.getString("electionName");
//                            int electionId = election.getInt("electionId");
//                            electionComboBox.addItem(electionName);
//                            electionMap.put(electionName, electionId);
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(CandidatePanel.this, "Failed to load elections for registration: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(CandidatePanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void registerCandidacy() {
//        String selectedElectionName = (String) electionComboBox.getSelectedItem();
//        String partyAffiliation = partyAffiliationField.getText().trim();
//        String manifesto = manifestoArea.getText().trim();
//
//        if (selectedElectionName == null || selectedElectionName.isEmpty() || partyAffiliation.isEmpty() || manifesto.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "All fields are required for candidacy registration.", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int electionId = electionMap.getOrDefault(selectedElectionName, -1);
//        if (electionId == -1) {
//            JOptionPane.showMessageDialog(this, "Selected election not found.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", "register");
//        formData.put("electionId", String.valueOf(electionId));
//        formData.put("partyAffiliation", partyAffiliation);
//        formData.put("manifesto", manifesto);
//        // userId is automatically picked up by the servlet from the session
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/candidate", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(CandidatePanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        clearRegisterCandidacyFields();
//                        loadMyCandidacies(); // Refresh the candidacy list
//                        candidateTabs.setSelectedIndex(0); // Go to "My Candidacy" tab
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(CandidatePanel.this, "Failed to register candidacy: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(CandidatePanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void clearRegisterCandidacyFields() {
//        electionComboBox.setSelectedIndex(-1); // No selection
//        partyAffiliationField.setText("");
//        manifestoArea.setText("");
//    }
//
//    private void styleButton(JButton button, Color bgColor) {
//        button.setFont(new Font("Arial", Font.BOLD, 14));
//        button.setBackground(bgColor);
//        button.setForeground(Color.WHITE);
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//    }
//}

package com.votingsystem.client.ui;

import com.votingsystem.client.util.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CandidatePanel extends JPanel {
    private DashboardPanel parentDashboard;
    private JTabbedPane candidateTabs;

    // My Candidacy Tab components
    private JTable myCandidacyTable;
    private DefaultTableModel myCandidacyTableModel;
    private JButton refreshMyCandidaciesButton; // Renamed for clarity

    // Register Candidacy Tab components
    private JComboBox<String> electionComboBox;
    private JTextField partyAffiliationField;
    private JTextArea manifestoArea;
    private JButton registerCandidacyButton; // Renamed for clarity
    private Map<String, Integer> electionMap; // Map election name to ID

    public CandidatePanel(DashboardPanel parentDashboard) {
        this.parentDashboard = parentDashboard;
        setLayout(new BorderLayout(15, 15)); // Add gaps for better spacing
        setBackground(new Color(245, 245, 250)); // Consistent background with Dashboard

        // Panel Title
        setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 15, 15, 15), // Outer padding
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2), // CornflowerBlue border
                "My Candidacy Management", // More descriptive title
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(70, 130, 180) // SteelBlue title color
            )
        ));

        candidateTabs = new JTabbedPane(JTabbedPane.TOP); // Tabs at the top
        candidateTabs.setFont(new Font("Arial", Font.BOLD, 15)); // Slightly larger tab font
        candidateTabs.setForeground(new Color(25, 25, 112)); // Dark blue for active tabs
        candidateTabs.setBackground(new Color(220, 230, 240)); // Light blue for tab background

        JPanel myCandidacyPanel = createMyCandidacyPanel();
        JPanel registerCandidacyPanel = createRegisterCandidacyPanel();

        candidateTabs.addTab("My Candidacies", myCandidacyPanel); // Renamed tab
        candidateTabs.addTab("Register for Election", registerCandidacyPanel);

        candidateTabs.addChangeListener(e -> {
            int selectedIndex = candidateTabs.getSelectedIndex();
            if (selectedIndex == 0) { // "My Candidacies" tab
                loadMyCandidacies();
            } else if (selectedIndex == 1) { // "Register for Election" tab
                loadAvailableElectionsForRegistration();
                clearRegisterCandidacyFields(); // Clear fields when switching to this tab
            }
        });

        add(candidateTabs, BorderLayout.CENTER);
    }

    // --- My Candidacy Panel ---
    private JPanel createMyCandidacyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Added gaps
        panel.setBorder(new EmptyBorder(15, 15, 15, 15)); // More padding
        panel.setBackground(Color.WHITE); // White background for content area

        myCandidacyTableModel = new DefaultTableModel(new String[]{"Candidacy ID", "Election Name", "Party", "Manifesto", "Status", "Registration Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myCandidacyTable = new JTable(myCandidacyTableModel);
        myCandidacyTable.getTableHeader().setReorderingAllowed(false);
        myCandidacyTable.setRowHeight(25); // Increase row height
        myCandidacyTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Table data font
        myCandidacyTable.setFillsViewportHeight(true); // Make table fill viewport height

        // Table Header Styling
        JTableHeader header = myCandidacyTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));
        header.setBackground(new Color(204, 229, 255)); // Light Blue
        header.setForeground(new Color(25, 25, 112)); // Midnight Blue
        header.setPreferredSize(new Dimension(header.getWidth(), 30)); // Increase header height

        // Custom renderer for Status column
        myCandidacyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4) { // Status column (index 4 based on new columns)
                    String status = (String) value;
                    switch (status) {
                        case "PENDING":
                            c.setBackground(new Color(255, 255, 204)); // Light Yellow
                            c.setForeground(new Color(153, 102, 0)); // Darker yellow/brown
                            break;
                        case "APPROVED":
                            c.setBackground(new Color(204, 255, 204)); // Light Green
                            c.setForeground(new Color(0, 102, 0)); // Dark Green
                            break;
                        case "REJECTED":
                            c.setBackground(new Color(255, 204, 204)); // Light Red
                            c.setForeground(new Color(153, 0, 0)); // Dark Red
                            break;
                        default:
                            c.setBackground(table.getBackground());
                            c.setForeground(table.getForeground());
                    }
                } else {
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }
                // Apply selection background/foreground if selected
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(myCandidacyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230))); // LightBlue border
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Centered, spaced buttons
        buttonPanel.setBackground(Color.WHITE); // Match panel background

        refreshMyCandidaciesButton = new JButton("Refresh My Candidacies");
        styleButton(refreshMyCandidaciesButton, new Color(100, 149, 237)); // CornflowerBlue
        refreshMyCandidaciesButton.addActionListener(e -> loadMyCandidacies());
        buttonPanel.add(refreshMyCandidaciesButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMyCandidacies() {
        refreshMyCandidaciesButton.setEnabled(false); // Disable button during load

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listAll"); // Candidate role can view their own, Admin sees all
                return ApiClient.get("/candidate", params);
            }

            @Override
            protected void done() {
                refreshMyCandidaciesButton.setEnabled(true); // Re-enable button

                try {
                    JSONObject response = get();
                    myCandidacyTableModel.setRowCount(0); // Clear existing data
                    if (response != null && response.optBoolean("success", false)) {
                        JSONArray candidates = response.optJSONArray("candidates");
                        int currentUserId = parentDashboard.getLoggedInUserId();
                        if (candidates != null) {
                            boolean foundCandidacies = false;
                            for (int i = 0; i < candidates.length(); i++) {
                                JSONObject candidate = candidates.getJSONObject(i);
                                if (candidate.optInt("userId") == currentUserId) { // Filter for current user's candidacies
                                    Vector<Object> row = new Vector<>();
                                    row.add(candidate.optInt("candidateId"));
                                    row.add(candidate.optString("electionName"));
                                    row.add(candidate.optString("partyAffiliation", "N/A"));
                                    row.add(candidate.optString("manifesto", "N/A"));
                                    row.add(candidate.optString("approvalStatus"));
                                    row.add(candidate.optString("registrationDate"));
                                    myCandidacyTableModel.addRow(row);
                                    foundCandidacies = true;
                                }
                            }
                            if (!foundCandidacies) {
                                JOptionPane.showMessageDialog(CandidatePanel.this, "You have not registered for any candidacies yet.", "No Candidacies", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(CandidatePanel.this, "No candidacy data available from the server.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        String msg = (response != null) ? response.optString("message", "Unknown error.") : "No response from server.";
                        JOptionPane.showMessageDialog(CandidatePanel.this, "Failed to load your candidacies: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(CandidatePanel.this, "An error occurred loading candidacies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // --- Register Candidacy Panel ---
    private JPanel createRegisterCandidacyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40)); // Increased padding for form
        panel.setBackground(Color.WHITE); // White background for form

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); // More spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow components to stretch

        int row = 0;

        JLabel formTitle = new JLabel("Submit Your Candidacy Application", SwingConstants.CENTER);
        formTitle.setFont(new Font("Arial", Font.BOLD, 22));
        formTitle.setForeground(new Color(25, 25, 112)); // MidnightBlue
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(formTitle, gbc);

        gbc.gridwidth = 1; // Reset gridwidth

        // Election Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel electionLabel = new JLabel("Select Election:");
        styleLabel(electionLabel);
        panel.add(electionLabel, gbc);
        gbc.gridx = 1;
        electionComboBox = new JComboBox<>();
        styleComboBox(electionComboBox);
        panel.add(electionComboBox, gbc);
        row++;

        // Party Affiliation
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel partyLabel = new JLabel("Party Affiliation:");
        styleLabel(partyLabel);
        panel.add(partyLabel, gbc);
        gbc.gridx = 1;
        partyAffiliationField = new JTextField(30); // Increased field size
        styleTextField(partyAffiliationField);
        panel.add(partyAffiliationField, gbc);
        row++;

        // Manifesto
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel manifestoLabel = new JLabel("Manifesto:");
        styleLabel(manifestoLabel);
        panel.add(manifestoLabel, gbc);
        gbc.gridx = 1;
        manifestoArea = new JTextArea(10, 30); // Increased area size
        manifestoArea.setLineWrap(true);
        manifestoArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(manifestoArea);
        styleTextArea(manifestoArea, scrollPane); // Style the text area and its scroll pane
        panel.add(scrollPane, gbc);
        row++;

        // Register Button
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 10, 10); // More space above button
        registerCandidacyButton = new JButton("Submit Candidacy Application"); // More descriptive text
        styleButton(registerCandidacyButton, new Color(70, 130, 180)); // SteelBlue
        registerCandidacyButton.addActionListener(e -> registerCandidacy());
        panel.add(registerCandidacyButton, gbc);
        row++;

        return panel;
    }

    private void loadAvailableElectionsForRegistration() {
        registerCandidacyButton.setEnabled(false); // Disable button during load

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listElections");
                params.put("status", "SCHEDULED"); // Only allow registration for scheduled elections
                return ApiClient.get("/admin/election", params);
            }

            @Override
            protected void done() {
                registerCandidacyButton.setEnabled(true); // Re-enable button

                try {
                    JSONObject response = get();
                    electionComboBox.removeAllItems();
                    electionMap = new HashMap<>(); // Reset map
                    if (response != null && response.optBoolean("success", false)) {
                        JSONArray elections = response.optJSONArray("elections");
                        if (elections != null && elections.length() > 0) {
                            for (int i = 0; i < elections.length(); i++) {
                                JSONObject election = elections.getJSONObject(i);
                                String electionName = election.optString("electionName");
                                int electionId = election.optInt("electionId");
                                electionComboBox.addItem(electionName);
                                electionMap.put(electionName, electionId);
                            }
                            // Optionally select the first item if available
                            if (electionComboBox.getItemCount() > 0) {
                                electionComboBox.setSelectedIndex(0);
                            }
                        } else {
                            electionComboBox.addItem("No Scheduled Elections Available");
                            electionComboBox.setEnabled(false);
                            registerCandidacyButton.setEnabled(false);
                            JOptionPane.showMessageDialog(CandidatePanel.this, "No scheduled elections are currently open for candidacy registration.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        String msg = (response != null) ? response.optString("message", "Unknown error.") : "No response from server.";
                        JOptionPane.showMessageDialog(CandidatePanel.this, "Failed to load elections for registration: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                        electionComboBox.addItem("Error loading elections");
                        electionComboBox.setEnabled(false);
                        registerCandidacyButton.setEnabled(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(CandidatePanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    electionComboBox.addItem("Error loading elections");
                    electionComboBox.setEnabled(false);
                    registerCandidacyButton.setEnabled(false);
                }
            }
        }.execute();
    }

    private void registerCandidacy() {
        String selectedElectionName = (String) electionComboBox.getSelectedItem();
        String partyAffiliation = partyAffiliationField.getText().trim();
        String manifesto = manifestoArea.getText().trim();

        if (selectedElectionName == null || selectedElectionName.isEmpty() || partyAffiliation.isEmpty() || manifesto.isEmpty() || selectedElectionName.equals("No Scheduled Elections Available") || selectedElectionName.equals("Error loading elections")) {
            JOptionPane.showMessageDialog(this, "Please ensure an election is selected and all fields are filled for candidacy registration.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int electionId = electionMap.getOrDefault(selectedElectionName, -1);
        if (electionId == -1) {
            JOptionPane.showMessageDialog(this, "Selected election ID not found. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable button during API call
        registerCandidacyButton.setEnabled(false);

        Map<String, String> formData = new HashMap<>();
        formData.put("action", "register");
        formData.put("electionId", String.valueOf(electionId));
        formData.put("partyAffiliation", partyAffiliation);
        formData.put("manifesto", manifesto);
        // userId is automatically picked up by the servlet from the session

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return ApiClient.post("/candidate", formData);
            }

            @Override
            protected void done() {
                registerCandidacyButton.setEnabled(true); // Re-enable button

                try {
                    JSONObject response = get();
                    if (response != null && response.optBoolean("success", false)) {
                        JOptionPane.showMessageDialog(CandidatePanel.this, response.optString("message", "Candidacy registered successfully!"), "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearRegisterCandidacyFields();
                        loadMyCandidacies(); // Refresh the candidacy list
                        candidateTabs.setSelectedIndex(0); // Go to "My Candidacy" tab
                    } else {
                        String msg = (response != null) ? response.optString("message", "Unknown error.") : "No response from server.";
                        JOptionPane.showMessageDialog(CandidatePanel.this, "Failed to register candidacy: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(CandidatePanel.this, "An error occurred during candidacy registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void clearRegisterCandidacyFields() {
        if (electionComboBox.getItemCount() > 0 && !electionComboBox.getSelectedItem().equals("No Scheduled Elections Available") && !electionComboBox.getSelectedItem().equals("Error loading elections")) {
            electionComboBox.setSelectedIndex(0); // Select first item if available and not an info message
        } else {
            electionComboBox.setSelectedIndex(-1); // No selection
        }
        partyAffiliationField.setText("");
        manifestoArea.setText("");
    }

    // --- General Styling Methods ---
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(25, 25, 112)); // MidnightBlue
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleTextArea(JTextArea textArea, JScrollPane scrollPane) {
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setBorder(new EmptyBorder(5, 8, 5, 8)); // Padding inside text area
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230))); // Border for scroll pane
    }
}