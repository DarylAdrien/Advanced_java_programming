//// src/com/votingsystem/client/ui/ElectionManagementPanel.java
//package com.votingsystem.client.ui;
//
//import com.votingsystem.client.util.ApiClient;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//
//public class ElectionManagementPanel extends JPanel {
//    private DashboardPanel parentDashboard;
//
//    private JTabbedPane electionTabs;
//    private JPanel createElectionPanel;
//    private JPanel manageElectionsPanel;
//    private JPanel createConstituencyPanel;
//    private JPanel manageConstituenciesPanel;
//
//    // Create Election fields
//    private JTextField newElectionNameField;
//    private JTextArea newElectionDescriptionArea;
//    private JTextField newElectionStartField; // YYYY-MM-DDTHH:MM:SS
//    private JTextField newElectionEndField;   // YYYY-MM-DDTHH:MM:SS
//
//    // Manage Elections table
//    private JTable electionsTable;
//    private DefaultTableModel electionsTableModel;
//
//    // Create Constituency fields
//    private JTextField newConstituencyNameField;
//    private JTextArea newConstituencyDescriptionArea;
//
//    // Manage Constituencies table
//    private JTable constituenciesTable;
//    private DefaultTableModel constituenciesTableModel;
//
//    public ElectionManagementPanel(DashboardPanel parentDashboard) {
//        this.parentDashboard = parentDashboard;
//        setLayout(new BorderLayout());
//        setBorder(BorderFactory.createTitledBorder("Election & Constituency Management"));
//
//        electionTabs = new JTabbedPane();
//        electionTabs.setFont(new Font("Arial", Font.PLAIN, 14));
//
//        createElectionPanel = createCreateElectionPanel();
//        manageElectionsPanel = createManageElectionsPanel();
//        createConstituencyPanel = createCreateConstituencyPanel();
//        manageConstituenciesPanel = createManageConstituenciesPanel();
//
//        electionTabs.addTab("Create Election", createElectionPanel);
//        electionTabs.addTab("Manage Elections", manageElectionsPanel);
//        electionTabs.addTab("Create Constituency", createConstituencyPanel);
//        electionTabs.addTab("Manage Constituencies", manageConstituenciesPanel);
//
//        // Listener to refresh data when a tab is selected
//        electionTabs.addChangeListener(e -> {
//            int selectedIndex = electionTabs.getSelectedIndex();
//            if (selectedIndex == 1) { // "Manage Elections" tab
//                loadElections();
//            } else if (selectedIndex == 3) { // "Manage Constituencies" tab
//                loadConstituencies();
//            }
//        });
//
//        add(electionTabs, BorderLayout.CENTER);
//    }
//
//    // --- Create Election Panel ---
//    private JPanel createCreateElectionPanel() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        panel.setBackground(new Color(255, 250, 240)); // FloralWhite
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(8, 8, 8, 8);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel title = new JLabel("Create New Election", SwingConstants.CENTER);
//        title.setFont(new Font("Arial", Font.BOLD, 20));
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(title, gbc);
//
//        gbc.gridwidth = 1; // Reset gridwidth
//
//        gbc.gridy++;
//        panel.add(new JLabel("Election Name:"), gbc);
//        gbc.gridx = 1;
//        newElectionNameField = new JTextField(25);
//        panel.add(newElectionNameField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        panel.add(new JLabel("Description:"), gbc);
//        gbc.gridx = 1;
//        newElectionDescriptionArea = new JTextArea(5, 25);
//        newElectionDescriptionArea.setLineWrap(true);
//        newElectionDescriptionArea.setWrapStyleWord(true);
//        JScrollPane scrollPane = new JScrollPane(newElectionDescriptionArea);
//        panel.add(scrollPane, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        panel.add(new JLabel("Start Date/Time (YYYY-MM-DDTHH:MM:SS):"), gbc);
//        gbc.gridx = 1;
//        newElectionStartField = new JTextField(25);
//        panel.add(newElectionStartField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        panel.add(new JLabel("End Date/Time (YYYY-MM-DDTHH:MM:SS):"), gbc);
//        gbc.gridx = 1;
//        newElectionEndField = new JTextField(25);
//        panel.add(newElectionEndField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.gridwidth = 2;
//        JButton createButton = new JButton("Create Election");
//        styleButton(createButton, new Color(70, 130, 180)); // SteelBlue
//        createButton.addActionListener(e -> createElection());
//        panel.add(createButton, gbc);
//
//        return panel;
//    }
//
//    private void createElection() {
//        String name = newElectionNameField.getText().trim();
//        String description = newElectionDescriptionArea.getText().trim();
//        String startDateTime = newElectionStartField.getText().trim();
//        String endDateTime = newElectionEndField.getText().trim();
//
//        if (name.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Election Name, Start Date/Time, and End Date/Time are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        try {
//            // Basic date/time format validation
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//            LocalDateTime start = LocalDateTime.parse(startDateTime, formatter);
//            LocalDateTime end = LocalDateTime.parse(endDateTime, formatter);
//
//            if (end.isBefore(start)) {
//                JOptionPane.showMessageDialog(this, "End Date/Time must be after Start Date/Time.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//        } catch (DateTimeParseException ex) {
//            JOptionPane.showMessageDialog(this, "Invalid Date/Time format. Please use YYYY-MM-DDTHH:MM:SS (e.g., 2025-12-31T23:59:59).", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", "createElection");
//        formData.put("electionName", name);
//        formData.put("description", description);
//        formData.put("startDateTime", startDateTime);
//        formData.put("endDateTime", endDateTime);
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/admin/election", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        clearCreateElectionFields();
//                        loadElections(); // Refresh the manage table
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to create election: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void clearCreateElectionFields() {
//        newElectionNameField.setText("");
//        newElectionDescriptionArea.setText("");
//        newElectionStartField.setText("");
//        newElectionEndField.setText("");
//    }
//
//    // --- Manage Elections Panel ---
//    private JPanel createManageElectionsPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        electionsTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Start Date", "End Date", "Status", "Created By"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                // Allow editing for Name, Description, Dates, Status
//                return column > 0 && column < 6; // ID and Created By are not editable
//            }
//        };
//        electionsTable = new JTable(electionsTableModel);
//        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        electionsTable.getTableHeader().setReorderingAllowed(false);
//        JScrollPane scrollPane = new JScrollPane(electionsTable);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton refreshButton = new JButton("Refresh");
//        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
//        refreshButton.addActionListener(e -> loadElections());
//        buttonPanel.add(refreshButton);
//
//        JButton updateButton = new JButton("Update Selected Election");
//        styleButton(updateButton, new Color(60, 179, 113)); // MediumSeaGreen
//        updateButton.addActionListener(e -> updateSelectedElection());
//        buttonPanel.add(updateButton);
//
//        panel.add(buttonPanel, BorderLayout.SOUTH);
//
//        // Load elections initially when panel is created
//        loadElections();
//        return panel;
//    }
//
//    private void loadElections() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listElections");
//                return ApiClient.get("/admin/election", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    electionsTableModel.setRowCount(0); // Clear existing data
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray elections = response.getJSONArray("elections");
//                        for (int i = 0; i < elections.length(); i++) {
//                            JSONObject election = elections.getJSONObject(i);
//                            Vector<Object> row = new Vector<>();
//                            row.add(election.getInt("electionId"));
//                            row.add(election.getString("electionName"));
//                            row.add(election.getString("description"));
//                            row.add(election.getString("startDateTime"));
//                            row.add(election.getString("endDateTime"));
//                            row.add(election.getString("status"));
//                            row.add(election.getString("createdByName"));
//                            electionsTableModel.addRow(row);
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to load elections: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void updateSelectedElection() {
//        int selectedRow = electionsTable.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select an election to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int electionId = (int) electionsTableModel.getValueAt(selectedRow, 0);
//        String name = (String) electionsTableModel.getValueAt(selectedRow, 1);
//        String description = (String) electionsTableModel.getValueAt(selectedRow, 2);
//        String startDateTime = (String) electionsTableModel.getValueAt(selectedRow, 3);
//        String endDateTime = (String) electionsTableModel.getValueAt(selectedRow, 4);
//        String status = (String) electionsTableModel.getValueAt(selectedRow, 5);
//
//        // Basic validation for updated fields
//        if (name.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty() || status.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Updated fields cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        try {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//            LocalDateTime start = LocalDateTime.parse(startDateTime, formatter);
//            LocalDateTime end = LocalDateTime.parse(endDateTime, formatter);
//
//            if (end.isBefore(start)) {
//                JOptionPane.showMessageDialog(this, "End Date/Time must be after Start Date/Time.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            // Basic status validation
//            if (!("SCHEDULED".equals(status) || "ACTIVE".equals(status) || "COMPLETED".equals(status))) {
//                JOptionPane.showMessageDialog(this, "Status must be 'SCHEDULED', 'ACTIVE', or 'COMPLETED'.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//        } catch (DateTimeParseException ex) {
//            JOptionPane.showMessageDialog(this, "Invalid Date/Time format. Use YYYY-MM-DDTHH:MM:SS.", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", "updateElection");
//        formData.put("electionId", String.valueOf(electionId));
//        formData.put("electionName", name);
//        formData.put("description", description);
//        formData.put("startDateTime", startDateTime);
//        formData.put("endDateTime", endDateTime);
//        formData.put("status", status);
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/admin/election", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        loadElections(); // Refresh table after update
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to update election: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//
//    // --- Create Constituency Panel ---
//    private JPanel createCreateConstituencyPanel() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        panel.setBackground(new Color(240, 248, 255)); // AliceBlue
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(8, 8, 8, 8);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel title = new JLabel("Create New Constituency", SwingConstants.CENTER);
//        title.setFont(new Font("Arial", Font.BOLD, 20));
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(title, gbc);
//
//        gbc.gridwidth = 1; // Reset gridwidth
//
//        gbc.gridy++;
//        panel.add(new JLabel("Constituency Name:"), gbc);
//        gbc.gridx = 1;
//        newConstituencyNameField = new JTextField(25);
//        panel.add(newConstituencyNameField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        panel.add(new JLabel("Description:"), gbc);
//        gbc.gridx = 1;
//        newConstituencyDescriptionArea = new JTextArea(5, 25);
//        newConstituencyDescriptionArea.setLineWrap(true);
//        newConstituencyDescriptionArea.setWrapStyleWord(true);
//        JScrollPane scrollPane = new JScrollPane(newConstituencyDescriptionArea);
//        panel.add(scrollPane, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.gridwidth = 2;
//        JButton createButton = new JButton("Create Constituency");
//        styleButton(createButton, new Color(70, 130, 180)); // SteelBlue
//        createButton.addActionListener(e -> createConstituency());
//        panel.add(createButton, gbc);
//
//        return panel;
//    }
//
//    private void createConstituency() {
//        String name = newConstituencyNameField.getText().trim();
//        String description = newConstituencyDescriptionArea.getText().trim();
//
//        if (name.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Constituency Name is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", "createConstituency");
//        formData.put("name", name);
//        formData.put("description", description);
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/admin/election", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        clearCreateConstituencyFields();
//                        loadConstituencies(); // Refresh the manage table
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to create constituency: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void clearCreateConstituencyFields() {
//        newConstituencyNameField.setText("");
//        newConstituencyDescriptionArea.setText("");
//    }
//
//    // --- Manage Constituencies Panel ---
//    private JPanel createManageConstituenciesPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        constituenciesTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Description"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                // Allow editing for Name, Description
//                return column > 0; // ID is not editable
//            }
//        };
//        constituenciesTable = new JTable(constituenciesTableModel);
//        constituenciesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        constituenciesTable.getTableHeader().setReorderingAllowed(false);
//        JScrollPane scrollPane = new JScrollPane(constituenciesTable);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton refreshButton = new JButton("Refresh");
//        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
//        refreshButton.addActionListener(e -> loadConstituencies());
//        buttonPanel.add(refreshButton);
//
//        // Note: Update Constituency functionality is a placeholder.
//        // The backend ElectionManagementServlet needs the updateConstituency method to be fully implemented.
//        JButton updateButton = new JButton("Update Selected Constituency (Not fully implemented)");
//        styleButton(updateButton, new Color(60, 179, 113)); // MediumSeaGreen
//        updateButton.setEnabled(false); // Disable for now
//        buttonPanel.add(updateButton);
//
//        panel.add(buttonPanel, BorderLayout.SOUTH);
//
//        loadConstituencies();
//        return panel;
//    }
//
//    private void loadConstituencies() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listConstituencies");
//                return ApiClient.get("/admin/election", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    constituenciesTableModel.setRowCount(0); // Clear existing data
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray constituencies = response.getJSONArray("constituencies");
//                        for (int i = 0; i < constituencies.length(); i++) {
//                            JSONObject constituency = constituencies.getJSONObject(i);
//                            Vector<Object> row = new Vector<>();
//                            row.add(constituency.getInt("constituencyId"));
//                            row.add(constituency.getString("name"));
//                            row.add(constituency.optString("description", "")); // Use optString for optional fields
//                            constituenciesTableModel.addRow(row);
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to load constituencies: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred loading constituencies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
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

// src/com/votingsystem/client/ui/ElectionManagementPanel.java
//package com.votingsystem.client.ui;
//
//import com.votingsystem.client.util.ApiClient;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import java.awt.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//
//public class ElectionManagementPanel extends JPanel {
//    private DashboardPanel parentDashboard;
//
//    private JTabbedPane electionTabs;
//    // Panels are now private fields to allow styling from any method
//    private JPanel createElectionPanel;
//    private JPanel manageElectionsPanel;
//    private JPanel createConstituencyPanel;
//    private JPanel manageConstituenciesPanel;
//
//    // Create Election fields
//    private JTextField newElectionNameField;
//    private JTextArea newElectionDescriptionArea;
//    private JTextField newElectionStartField; // YYYY-MM-DDTHH:MM:SS
//    private JTextField newElectionEndField;   // YYYY-MM-DDTHH:MM:SS
//
//    // Manage Elections table
//    private JTable electionsTable;
//    private DefaultTableModel electionsTableModel;
//
//    // Create Constituency fields
//    private JTextField newConstituencyNameField;
//    private JTextArea newConstituencyDescriptionArea;
//
//    // Manage Constituencies table
//    private JTable constituenciesTable;
//    private DefaultTableModel constituenciesTableModel;
//
//    public ElectionManagementPanel(DashboardPanel parentDashboard) {
//        this.parentDashboard = parentDashboard;
//        setLayout(new BorderLayout());
//        setBackground(new Color(240, 242, 245)); // Very light grey-blue background for the main panel
//
//        // Main panel border styling
//        setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createEmptyBorder(20, 20, 20, 20), // Outer padding
//            BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Subtle line border
//                "Election & Constituency Management",
//                javax.swing.border.TitledBorder.CENTER, // Center title
//                javax.swing.border.TitledBorder.TOP,
//                new Font("Arial", Font.BOLD, 24), // Larger, bold title
//                new Color(44, 62, 80) // Darker color for main title
//            )
//        ));
//
//        electionTabs = new JTabbedPane();
//        electionTabs.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for tabs
//        electionTabs.setBackground(new Color(230, 235, 240)); // Slightly darker tab background
//        electionTabs.setForeground(new Color(50, 70, 90)); // Darker text for tabs
//        electionTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT); // Allow scrolling if many tabs
//
//        createElectionPanel = createCreateElectionPanel();
//        manageElectionsPanel = createManageElectionsPanel();
//        createConstituencyPanel = createCreateConstituencyPanel();
//        manageConstituenciesPanel = createManageConstituenciesPanel();
//
//        electionTabs.addTab("Create Election", createElectionPanel);
//        electionTabs.addTab("Manage Elections", manageElectionsPanel);
//        electionTabs.addTab("Create Constituency", createConstituencyPanel);
//        electionTabs.addTab("Manage Constituencies", manageConstituenciesPanel);
//
//        // Listener to refresh data when a tab is selected
//        electionTabs.addChangeListener(e -> {
//            int selectedIndex = electionTabs.getSelectedIndex();
//            if (selectedIndex == 1) { // "Manage Elections" tab
//                loadElections();
//            } else if (selectedIndex == 3) { // "Manage Constituencies" tab
//                loadConstituencies();
//            }
//        });
//
//        add(electionTabs, BorderLayout.CENTER);
//    }
//
//    /**
//     * Helper method to apply consistent styling to JTables.
//     * @param table The JTable to style.
//     */
//    private void styleTable(JTable table) {
//        table.setFont(new Font("Arial", Font.PLAIN, 14));
//        table.setRowHeight(28);
//        table.setGridColor(new Color(220, 220, 220));
//        table.setSelectionBackground(new Color(174, 214, 241)); // Light blue selection
//        table.setSelectionForeground(Color.BLACK);
//
//        JTableHeader tableHeader = table.getTableHeader();
//        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));
//        tableHeader.setBackground(new Color(70, 130, 180)); // SteelBlue
//        tableHeader.setForeground(Color.WHITE);
//        tableHeader.setReorderingAllowed(false);
//        tableHeader.setResizingAllowed(true);
//    }
//
//    /**
//     * Helper method to apply consistent styling to JButtons, including hover effects.
//     * @param button The JButton to style.
//     * @param bgColor The background color for the button.
//     */
//    private void styleButton(JButton button, Color bgColor) {
//        button.setFont(new Font("Arial", Font.BOLD, 15));
//        button.setBackground(bgColor);
//        button.setForeground(Color.WHITE);
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(bgColor.darker().darker(), 1),
//                BorderFactory.createEmptyBorder(10, 25, 10, 25)
//        ));
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//
//        button.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                button.setBackground(bgColor.brighter());
//            }
//
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                button.setBackground(bgColor);
//            }
//        });
//    }
//
//    // --- Create Election Panel ---
//    private JPanel createCreateElectionPanel() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Increased padding
//        panel.setBackground(new Color(255, 255, 255)); // Pure white background for form
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10); // More spacing between form elements
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel title = new JLabel("Create New Election", SwingConstants.CENTER);
//        title.setFont(new Font("Arial", Font.BOLD, 26)); // Larger, prominent title
//        title.setForeground(new Color(44, 62, 80)); // Dark text color
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        gbc.weightx = 1.0; // Allow title to expand
//        panel.add(title, gbc);
//
//        gbc.gridwidth = 1; // Reset gridwidth for form fields
//        gbc.weightx = 0; // Labels don't expand
//        gbc.anchor = GridBagConstraints.WEST; // Align labels to the left
//
//        gbc.gridy++;
//        panel.add(new JLabel("Election Name:"), gbc);
//        gbc.gridx = 1;
//        gbc.weightx = 1.0; // Field expands
//        newElectionNameField = new JTextField(25);
//        newElectionNameField.putClientProperty("JComponent.roundRect", true); // Modern look
//        panel.add(newElectionNameField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.weightx = 0;
//        panel.add(new JLabel("Description:"), gbc);
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        newElectionDescriptionArea = new JTextArea(5, 25);
//        newElectionDescriptionArea.setLineWrap(true);
//        newElectionDescriptionArea.setWrapStyleWord(true);
//        newElectionDescriptionArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Subtle border
//        JScrollPane scrollPane = new JScrollPane(newElectionDescriptionArea);
//        scrollPane.setPreferredSize(new Dimension(newElectionNameField.getPreferredSize().width, newElectionDescriptionArea.getPreferredSize().height));
//        panel.add(scrollPane, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.weightx = 0;
//        panel.add(new JLabel("Start Date/Time (YYYY-MM-DDTHH:MM:SS):"), gbc);
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        newElectionStartField = new JTextField(25);
//        newElectionStartField.putClientProperty("JComponent.roundRect", true);
//        panel.add(newElectionStartField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.weightx = 0;
//        panel.add(new JLabel("End Date/Time (YYYY-MM-DDTHH:MM:SS):"), gbc);
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        newElectionEndField = new JTextField(25);
//        newElectionEndField.putClientProperty("JComponent.roundRect", true);
//        panel.add(newElectionEndField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.gridwidth = 2;
//        gbc.anchor = GridBagConstraints.CENTER; // Center the button
//        gbc.insets = new Insets(20, 10, 10, 10); // More space above button
//        JButton createButton = new JButton("Create Election");
//        styleButton(createButton, new Color(52, 152, 219)); // Bright blue for create
//        createButton.addActionListener(e -> createElection());
//        panel.add(createButton, gbc);
//
//        // Add some vertical glue at the bottom to push components to the top
//        gbc.gridy++;
//        gbc.weighty = 1.0; // This row takes all extra vertical space
//        panel.add(Box.createVerticalGlue(), gbc);
//
//        return panel;
//    }
//
//    private void createElection() {
//        String name = newElectionNameField.getText().trim();
//        String description = newElectionDescriptionArea.getText().trim();
//        String startDateTime = newElectionStartField.getText().trim();
//        String endDateTime = newElectionEndField.getText().trim();
//
//        if (name.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Election Name, Start Date/Time, and End Date/Time are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        try {
//            // Basic date/time format validation
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//            LocalDateTime start = LocalDateTime.parse(startDateTime, formatter);
//            LocalDateTime end = LocalDateTime.parse(endDateTime, formatter);
//
//            if (end.isBefore(start)) {
//                JOptionPane.showMessageDialog(this, "End Date/Time must be after Start Date/Time.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//        } catch (DateTimeParseException ex) {
//            JOptionPane.showMessageDialog(this, "Invalid Date/Time format. Please use YYYY-MM-DDTHH:MM:SS (e.g., 2025-12-31T23:59:59).", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", "createElection");
//        formData.put("electionName", name);
//        formData.put("description", description);
//        formData.put("startDateTime", startDateTime);
//        formData.put("endDateTime", endDateTime);
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/admin/election", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        clearCreateElectionFields();
//                        // Automatically switch to "Manage Elections" tab after creation
//                        electionTabs.setSelectedIndex(1);
//                        loadElections(); // Refresh the manage table
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to create election: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void clearCreateElectionFields() {
//        newElectionNameField.setText("");
//        newElectionDescriptionArea.setText("");
//        newElectionStartField.setText("");
//        newElectionEndField.setText("");
//    }
//
//    // --- Manage Elections Panel ---
//    private JPanel createManageElectionsPanel() {
//        JPanel panel = new JPanel(new BorderLayout(10, 10));
//        panel.setBackground(new Color(255, 255, 255));
//        panel.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createEmptyBorder(10, 10, 10, 10), // Padding
//            BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(180, 180, 180)),
//                "Manage Existing Elections",
//                javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP,
//                new Font("Arial", Font.BOLD, 18), new Color(66, 135, 245)
//            )
//        ));
//
//        electionsTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Start Date", "End Date", "Status", "Created By"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                // Allow editing for Name, Description, Dates, Status
//                // ID (0) and Created By (6) are not editable
//                return column > 0 && column < 6;
//            }
//        };
//        electionsTable = new JTable(electionsTableModel);
//        styleTable(electionsTable); // Apply custom table styling
//        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane scrollPane = new JScrollPane(electionsTable);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5)); // More spacing
//        buttonPanel.setBackground(panel.getBackground()); // Match panel background
//
//        JButton refreshButton = new JButton("Refresh Elections");
//        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
//        refreshButton.addActionListener(e -> loadElections());
//        buttonPanel.add(refreshButton);
//
//        JButton updateButton = new JButton("Update Selected Election");
//        styleButton(updateButton, new Color(60, 179, 113)); // MediumSeaGreen
//        updateButton.addActionListener(e -> updateSelectedElection());
//        buttonPanel.add(updateButton);
//
//        panel.add(buttonPanel, BorderLayout.SOUTH);
//
//        // No initial load here, it's done via tab change listener for efficiency
//        return panel;
//    }
//
//    private void loadElections() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listElections");
//                return ApiClient.get("/admin/election", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    electionsTableModel.setRowCount(0); // Clear existing data
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray elections = response.getJSONArray("elections");
//                        if (elections.length() == 0) {
//                            addNoDataRow(electionsTableModel, "No elections found. Create one using the 'Create Election' tab.");
//                        } else {
//                            for (int i = 0; i < elections.length(); i++) {
//                                JSONObject election = elections.getJSONObject(i);
//                                Vector<Object> row = new Vector<>();
//                                row.add(election.getInt("electionId"));
//                                row.add(election.getString("electionName"));
//                                row.add(election.getString("description"));
//                                row.add(election.getString("startDateTime"));
//                                row.add(election.getString("endDateTime"));
//                                row.add(election.getString("status"));
//                                row.add(election.getString("createdByName"));
//                                electionsTableModel.addRow(row);
//                            }
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to load elections: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                        addNoDataRow(electionsTableModel, "Error loading elections: " + msg);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                    addNoDataRow(electionsTableModel, "An unexpected error occurred while loading elections.");
//                }
//            }
//        }.execute();
//    }
//
//    private void updateSelectedElection() {
//        int selectedRow = electionsTable.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select an election to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        // Check if it's a "no data" row
//        if (!(electionsTableModel.getValueAt(selectedRow, 0) instanceof Integer)) {
//             JOptionPane.showMessageDialog(this, "Please select a valid election entry.", "Selection Error", JOptionPane.WARNING_MESSAGE);
//             return;
//        }
//
//        int electionId = (int) electionsTableModel.getValueAt(selectedRow, 0);
//        String name = (String) electionsTableModel.getValueAt(selectedRow, 1);
//        String description = (String) electionsTableModel.getValueAt(selectedRow, 2);
//        String startDateTime = (String) electionsTableModel.getValueAt(selectedRow, 3);
//        String endDateTime = (String) electionsTableModel.getValueAt(selectedRow, 4);
//        String status = (String) electionsTableModel.getValueAt(selectedRow, 5);
//
//        // Basic validation for updated fields
//        if (name.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty() || status.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Updated fields (Name, Dates, Status) cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        try {
////            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            LocalDateTime start = LocalDateTime.parse(startDateTime, formatter);
//            LocalDateTime end = LocalDateTime.parse(endDateTime, formatter);
//
//            if (end.isBefore(start)) {
//                JOptionPane.showMessageDialog(this, "End Date/Time must be after Start Date/Time.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            // Basic status validation
//            if (!("SCHEDULED".equals(status) || "ACTIVE".equals(status) || "COMPLETED".equals(status))) {
//                JOptionPane.showMessageDialog(this, "Status must be 'SCHEDULED', 'ACTIVE', or 'COMPLETED'.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//        } catch (DateTimeParseException ex) {
//            JOptionPane.showMessageDialog(this, "Invalid Date/Time format. Use YYYY-MM-DDTHH:MM:SS (e.g., 2025-12-31T23:59:59).", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", "updateElection");
//        formData.put("electionId", String.valueOf(electionId));
//        formData.put("electionName", name);
//        formData.put("description", description);
//        formData.put("startDateTime", startDateTime);
//        formData.put("endDateTime", endDateTime);
//        formData.put("status", status);
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/admin/election", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        loadElections(); // Refresh table after update
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to update election: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    // --- Create Constituency Panel ---
//    private JPanel createCreateConstituencyPanel() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Increased padding
//        panel.setBackground(new Color(255, 255, 255)); // Pure white background
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel title = new JLabel("Create New Constituency", SwingConstants.CENTER);
//        title.setFont(new Font("Arial", Font.BOLD, 26));
//        title.setForeground(new Color(44, 62, 80));
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        gbc.weightx = 1.0;
//        panel.add(title, gbc);
//
//        gbc.gridwidth = 1; // Reset gridwidth
//        gbc.weightx = 0;
//        gbc.anchor = GridBagConstraints.WEST;
//
//        gbc.gridy++;
//        panel.add(new JLabel("Constituency Name:"), gbc);
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        newConstituencyNameField = new JTextField(25);
//        newConstituencyNameField.putClientProperty("JComponent.roundRect", true);
//        panel.add(newConstituencyNameField, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.weightx = 0;
//        panel.add(new JLabel("Description:"), gbc);
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        newConstituencyDescriptionArea = new JTextArea(5, 25);
//        newConstituencyDescriptionArea.setLineWrap(true);
//        newConstituencyDescriptionArea.setWrapStyleWord(true);
//        newConstituencyDescriptionArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
//        JScrollPane scrollPane = new JScrollPane(newConstituencyDescriptionArea);
//        scrollPane.setPreferredSize(new Dimension(newConstituencyNameField.getPreferredSize().width, newConstituencyDescriptionArea.getPreferredSize().height));
//        panel.add(scrollPane, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy++;
//        gbc.gridwidth = 2;
//        gbc.anchor = GridBagConstraints.CENTER;
//        gbc.insets = new Insets(20, 10, 10, 10);
//        JButton createButton = new JButton("Create Constituency");
//        styleButton(createButton, new Color(52, 152, 219)); // Bright blue for create
//        createButton.addActionListener(e -> createConstituency());
//        panel.add(createButton, gbc);
//
//        // Add some vertical glue at the bottom
//        gbc.gridy++;
//        gbc.weighty = 1.0;
//        panel.add(Box.createVerticalGlue(), gbc);
//
//        return panel;
//    }
//
//    private void createConstituency() {
//        String name = newConstituencyNameField.getText().trim();
//        String description = newConstituencyDescriptionArea.getText().trim();
//
//        if (name.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Constituency Name is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", "createConstituency");
//        formData.put("name", name);
//        formData.put("description", description);
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/admin/election", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        clearCreateConstituencyFields();
//                        // Automatically switch to "Manage Constituencies" tab after creation
//                        electionTabs.setSelectedIndex(3);
//                        loadConstituencies(); // Refresh the manage table
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to create constituency: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void clearCreateConstituencyFields() {
//        newConstituencyNameField.setText("");
//        newConstituencyDescriptionArea.setText("");
//    }
//
//    // --- Manage Constituencies Panel ---
//    private JPanel createManageConstituenciesPanel() {
//        JPanel panel = new JPanel(new BorderLayout(10, 10));
//        panel.setBackground(new Color(255, 255, 255));
//        panel.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createEmptyBorder(10, 10, 10, 10), // Padding
//            BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(180, 180, 180)),
//                "Manage Existing Constituencies",
//                javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP,
//                new Font("Arial", Font.BOLD, 18), new Color(66, 135, 245)
//            )
//        ));
//
//        constituenciesTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Description"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                // Allow editing for Name, Description
//                return column > 0; // ID is not editable
//            }
//        };
//        constituenciesTable = new JTable(constituenciesTableModel);
//        styleTable(constituenciesTable); // Apply custom table styling
//        constituenciesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane scrollPane = new JScrollPane(constituenciesTable);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
//        buttonPanel.setBackground(panel.getBackground());
//
//        JButton refreshButton = new JButton("Refresh Constituencies");
//        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
//        refreshButton.addActionListener(e -> loadConstituencies());
//        buttonPanel.add(refreshButton);
//
//        // Note: Update Constituency functionality is a placeholder.
//        // The backend ElectionManagementServlet needs the updateConstituency method to be fully implemented.
//        JButton updateButton = new JButton("Update Selected Constituency (Not fully implemented)");
//        styleButton(updateButton, new Color(60, 179, 113)); // MediumSeaGreen
//        updateButton.setEnabled(false); // Disable for now
//        buttonPanel.add(updateButton);
//
//        panel.add(buttonPanel, BorderLayout.SOUTH);
//
//        // No initial load here, it's done via tab change listener for efficiency
//        return panel;
//    }
//
//    private void loadConstituencies() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listConstituencies");
//                return ApiClient.get("/admin/election", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    constituenciesTableModel.setRowCount(0); // Clear existing data
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray constituencies = response.getJSONArray("constituencies");
//                        if (constituencies.length() == 0) {
//                            addNoDataRow(constituenciesTableModel, "No constituencies found. Create one using the 'Create Constituency' tab.");
//                        } else {
//                            for (int i = 0; i < constituencies.length(); i++) {
//                                JSONObject constituency = constituencies.getJSONObject(i);
//                                Vector<Object> row = new Vector<>();
//                                row.add(constituency.getInt("constituencyId"));
//                                row.add(constituency.getString("name"));
//                                row.add(constituency.optString("description", "")); // Use optString for optional fields
//                                constituenciesTableModel.addRow(row);
//                            }
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to load constituencies: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                        addNoDataRow(constituenciesTableModel, "Error loading constituencies: " + msg);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred loading constituencies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                    addNoDataRow(constituenciesTableModel, "An unexpected error occurred while loading constituencies.");
//                }
//            }
//        }.execute();
//    }
//
//    /**
//     * Adds a "no data" message row to a given table model.
//     * @param model The DefaultTableModel to modify.
//     * @param message The message to display.
//     */
//    private void addNoDataRow(DefaultTableModel model, String message) {
//        model.setRowCount(0); // Ensure table is empty
//        Vector<Object> noDataRow = new Vector<>();
//        for (int i = 0; i < model.getColumnCount(); i++) {
//            noDataRow.add(""); // Fill with empty strings
//        }
//        // Place message in the second column for better visibility if ID is first
//        if (model.getColumnCount() > 1) {
//            noDataRow.set(1, message);
//        } else if (model.getColumnCount() > 0) {
//            noDataRow.set(0, message);
//        }
//        model.addRow(noDataRow);
//    }
//}

// src/com/votingsystem/client/ui/ElectionManagementPanel.java
package com.votingsystem.client.ui;

import com.votingsystem.client.util.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ElectionManagementPanel extends JPanel {
    private DashboardPanel parentDashboard;

    private JTabbedPane electionTabs;
    private JPanel createElectionPanel;
    private JPanel manageElectionsPanel;
    private JPanel createConstituencyPanel;
    private JPanel manageConstituenciesPanel;

    // Create Election fields
    private JTextField newElectionNameField;
    private JTextArea newElectionDescriptionArea;
    private JTextField newElectionStartField; // YYYY-MM-DDTHH:MM:SS
    private JTextField newElectionEndField;   // YYYY-MM-DDTHH:MM:SS

    // Manage Elections table
    private JTable electionsTable;
    private DefaultTableModel electionsTableModel;

    // Create Constituency fields
    private JTextField newConstituencyNameField;
    private JTextArea newConstituencyDescriptionArea;

    // Manage Constituencies table
    private JTable constituenciesTable;
    private DefaultTableModel constituenciesTableModel;

    // Define the desired date-time format consistently
    private static final DateTimeFormatter DESIRED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ElectionManagementPanel(DashboardPanel parentDashboard) {
        this.parentDashboard = parentDashboard;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245)); // Very light grey-blue background for the main panel

        // Main panel border styling
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20), // Outer padding
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Subtle line border
                "Election & Constituency Management",
                javax.swing.border.TitledBorder.CENTER, // Center title
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 24), // Larger, bold title
                new Color(44, 62, 80) // Darker color for main title
            )
        ));

        electionTabs = new JTabbedPane();
        electionTabs.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for tabs
        electionTabs.setBackground(new Color(230, 235, 240)); // Slightly darker tab background
        electionTabs.setForeground(new Color(50, 70, 90)); // Darker text for tabs
        electionTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT); // Allow scrolling if many tabs

        createElectionPanel = createCreateElectionPanel();
        manageElectionsPanel = createManageElectionsPanel();
        createConstituencyPanel = createCreateConstituencyPanel();
        manageConstituenciesPanel = createManageConstituenciesPanel();

        electionTabs.addTab("Create Election", createElectionPanel);
        electionTabs.addTab("Manage Elections", manageElectionsPanel);
        electionTabs.addTab("Create Constituency", createConstituencyPanel);
        electionTabs.addTab("Manage Constituencies", manageConstituenciesPanel);

        // Listener to refresh data when a tab is selected
        electionTabs.addChangeListener(e -> {
            int selectedIndex = electionTabs.getSelectedIndex();
            if (selectedIndex == 1) { // "Manage Elections" tab
                loadElections();
            } else if (selectedIndex == 3) { // "Manage Constituencies" tab
                loadConstituencies();
            }
        });

        add(electionTabs, BorderLayout.CENTER);
    }

    /**
     * Helper method to apply consistent styling to JTables.
     * @param table The JTable to style.
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(174, 214, 241)); // Light blue selection
        table.setSelectionForeground(Color.BLACK);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));
        tableHeader.setBackground(new Color(70, 130, 180)); // SteelBlue
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(true);
    }

    /**
     * Helper method to apply consistent styling to JButtons, including hover effects.
     * @param button The JButton to style.
     * @param bgColor The background color for the button.
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker().darker(), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    /**
     * Helper method to parse a date-time string using multiple common formats.
     * This makes the parsing more robust to variations from the API.
     * @param dateTimeString The string to parse.
     * @return A LocalDateTime object if parsing is successful, null otherwise.
     */
//    private LocalDateTime parseDateTimeStringRobustly(String dateTimeString) {
//        // Define an array of formatters to try, from most specific to more general
//        DateTimeFormatter[] formatters = {
//            DESIRED_DATE_TIME_FORMATTER, // yyyy-MM-dd'T'HH:mm:ss
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), // yyyy-MM-dd HH:mm:ss (with space)
//            DateTimeFormatter.ISO_LOCAL_DATE_TIME, // Handles yyyy-MM-ddTHH:mm:ss.SSS (with milliseconds)
//            DateTimeFormatter.ISO_OFFSET_DATE_TIME, // Handles with offset like +05:30
//            DateTimeFormatter.ISO_ZONED_DATE_TIME // Handles with zone like [Asia/Kolkata]
//        };
//
//        for (DateTimeFormatter formatter : formatters) {
//            try {
//                // Some ISO formatters may require a TemporalAccessor first if the string is ambiguous
//                return LocalDateTime.parse(dateTimeString, formatter);
//            } catch (DateTimeParseException e) {
//                // Try next formatter
//            }
//        }
//        // As a last resort, try parsing without a specific formatter if it's a standard ISO format
//        try {
//            return LocalDateTime.parse(dateTimeString);
//        } catch (DateTimeParseException e) {
//            System.err.println("Failed to parse date/time string: '" + dateTimeString + "' with any known formatter. Error: " + e.getMessage());
//            return null; // Return null if all parsing attempts fail
//        }
//    }


    // --- Create Election Panel ---
    private JPanel createCreateElectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Increased padding
        panel.setBackground(new Color(255, 255, 255)); // Pure white background for form

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // More spacing between form elements
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create New Election", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26)); // Larger, prominent title
        title.setForeground(new Color(44, 62, 80)); // Dark text color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // Allow title to expand
        panel.add(title, gbc);

        gbc.gridwidth = 1; // Reset gridwidth for form fields
        gbc.weightx = 0; // Labels don't expand
        gbc.anchor = GridBagConstraints.WEST; // Align labels to the left

        gbc.gridy++;
        panel.add(new JLabel("Election Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Field expands
        newElectionNameField = new JTextField(25);
        newElectionNameField.putClientProperty("JComponent.roundRect", true); // Modern look
        panel.add(newElectionNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        newElectionDescriptionArea = new JTextArea(5, 25);
        newElectionDescriptionArea.setLineWrap(true);
        newElectionDescriptionArea.setWrapStyleWord(true);
        newElectionDescriptionArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Subtle border
        JScrollPane scrollPane = new JScrollPane(newElectionDescriptionArea);
        scrollPane.setPreferredSize(new Dimension(newElectionNameField.getPreferredSize().width, newElectionDescriptionArea.getPreferredSize().height));
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("Start Date/Time (YYYY-MM-DDTHH:MM:SS):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        newElectionStartField = new JTextField(25);
        newElectionStartField.putClientProperty("JComponent.roundRect", true);
        panel.add(newElectionStartField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("End Date/Time (YYYY-MM-DDTHH:MM:SS):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        newElectionEndField = new JTextField(25);
        newElectionEndField.putClientProperty("JComponent.roundRect", true);
        panel.add(newElectionEndField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        gbc.insets = new Insets(20, 10, 10, 10); // More space above button
        JButton createButton = new JButton("Create Election");
        styleButton(createButton, new Color(52, 152, 219)); // Bright blue for create
        createButton.addActionListener(e -> createElection());
        panel.add(createButton, gbc);

        // Add some vertical glue at the bottom to push components to the top
        gbc.gridy++;
        gbc.weighty = 1.0; // This row takes all extra vertical space
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private void createElection() {
        String name = newElectionNameField.getText().trim();
        String description = newElectionDescriptionArea.getText().trim();
        String startDateTime = newElectionStartField.getText().trim();
        String endDateTime = newElectionEndField.getText().trim();

        if (name.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Election Name, Start Date/Time, and End Date/Time are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime start;
        LocalDateTime end;
        try {
            // Use the desired formatter for new election creation
            start = LocalDateTime.parse(startDateTime, DESIRED_DATE_TIME_FORMATTER);
            end = LocalDateTime.parse(endDateTime, DESIRED_DATE_TIME_FORMATTER);

            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End Date/Time must be after Start Date/Time.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date/Time format. Please use YYYY-MM-DDTHH:MM:SS (e.g., 2025-12-31T23:59:59).", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, String> formData = new HashMap<>();
        formData.put("action", "createElection");
        formData.put("electionName", name);
        formData.put("description", description);
        formData.put("startDateTime", startDateTime);
        formData.put("endDateTime", endDateTime);

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return ApiClient.post("/admin/election", formData);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    if (response != null && response.getBoolean("success")) {
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearCreateElectionFields();
                        // Automatically switch to "Manage Elections" tab after creation
                        electionTabs.setSelectedIndex(1);
                        loadElections(); // Refresh the manage table
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to create election: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void clearCreateElectionFields() {
        newElectionNameField.setText("");
        newElectionDescriptionArea.setText("");
        newElectionStartField.setText("");
        newElectionEndField.setText("");
    }

    // --- Manage Elections Panel ---
    private JPanel createManageElectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10), // Padding
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Manage Existing Elections",
                javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18), new Color(66, 135, 245)
            )
        ));

        electionsTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Status", "Created By"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for Name, Description, Dates, Status
                // ID (0) and Created By (6) are not editable
                return column > 0 && column <4;
            }
        };
        electionsTable = new JTable(electionsTableModel);
        styleTable(electionsTable); // Apply custom table styling
        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(electionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5)); // More spacing
        buttonPanel.setBackground(panel.getBackground()); // Match panel background

        JButton refreshButton = new JButton("Refresh Elections");
        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
        refreshButton.addActionListener(e -> loadElections());
        buttonPanel.add(refreshButton);

        JButton updateButton = new JButton("Update Selected Election");
        styleButton(updateButton, new Color(60, 179, 113)); // MediumSeaGreen
        updateButton.addActionListener(e -> updateSelectedElection());
        buttonPanel.add(updateButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // No initial load here, it's done via tab change listener for efficiency
        return panel;
    }

    private void loadElections() {
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listElections");
                return ApiClient.get("/admin/election", params);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    electionsTableModel.setRowCount(0); // Clear existing data
                    if (response != null && response.getBoolean("success")) {
                        JSONArray elections = response.getJSONArray("elections");
                        if (elections.length() == 0) {
                            addNoDataRow(electionsTableModel, "No elections found. Create one using the 'Create Election' tab.");
                        } else {
                            for (int i = 0; i < elections.length(); i++) {
                                JSONObject election = elections.getJSONObject(i);
                                Vector<Object> row = new Vector<>();
                                row.add(election.getInt("electionId"));
                                row.add(election.getString("electionName"));
                                row.add(election.getString("description"));

                                // --- FIX: Standardize date-time format when loading into table ---
//                                String startDateTimeApi = election.getString("startDateTime");
//                                String endDateTimeApi = election.getString("endDateTime");

//                                LocalDateTime startLDT = parseDateTimeStringRobustly(startDateTimeApi);
//                                LocalDateTime endLDT = parseDateTimeStringRobustly(endDateTimeApi);

                                // Format to the *exact* string required for update, or use original if parsing failed
//                                row.add(startLDT != null ? startLDT.format(DESIRED_DATE_TIME_FORMATTER) : startDateTimeApi);
//                                row.add(endLDT != null ? endLDT.format(DESIRED_DATE_TIME_FORMATTER) : endDateTimeApi);
                                // --- END FIX ---

                                row.add(election.getString("status"));
                                row.add(election.getString("createdByName"));
                                electionsTableModel.addRow(row);
                            }
                        }
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to load elections: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                        addNoDataRow(electionsTableModel, "Error loading elections: " + msg);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    addNoDataRow(electionsTableModel, "An unexpected error occurred while loading elections.");
                }
            }
        }.execute();
    }

    private void updateSelectedElection() {
        int selectedRow = electionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an election to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if it's a "no data" row (where ID column is not an Integer)
        if (!(electionsTableModel.getValueAt(selectedRow, 0) instanceof Integer)) {
             JOptionPane.showMessageDialog(this, "Please select a valid election entry.", "Selection Error", JOptionPane.WARNING_MESSAGE);
             return;
        }

        int electionId = (int) electionsTableModel.getValueAt(selectedRow, 0);
        String name = (String) electionsTableModel.getValueAt(selectedRow, 1);
        String description = (String) electionsTableModel.getValueAt(selectedRow, 2);
//        String startDateTime = (String) electionsTableModel.getValueAt(selectedRow, 3);
//        String endDateTime = (String) electionsTableModel.getValueAt(selectedRow, 4);
        String status = (String) electionsTableModel.getValueAt(selectedRow, 3);

        // --- DEBUG PRINT: See what string is being parsed ---
//        System.out.println("Attempting to parse startDateTime from table: '" + startDateTime + "'");
//        System.out.println("Attempting to parse endDateTime from table: '" + endDateTime + "'");
        // --- END DEBUG PRINT ---

        // Basic validation for updated fields
        if (name.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Updated fields (Name, Dates, Status) cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

//        LocalDateTime start;
//        LocalDateTime end;
//        try {
//            // --- FIX: Use the consistently defined formatter ---
////            start = LocalDateTime.parse(startDateTime, DESIRED_DATE_TIME_FORMATTER);
////            end = LocalDateTime.parse(endDateTime, DESIRED_DATE_TIME_FORMATTER);
////            // --- END FIX ---
//
////            if (end.isBefore(start)) {
////                JOptionPane.showMessageDialog(this, "End Date/Time must be after Start Date/Time.", "Input Error", JOptionPane.WARNING_MESSAGE);
////                return;
////            }
//            // Basic status validation
//            if (!("SCHEDULED".equals(status) || "ACTIVE".equals(status) || "COMPLETED".equals(status))) {
//                JOptionPane.showMessageDialog(this, "Status must be 'SCHEDULED', 'ACTIVE', or 'COMPLETED'.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//        } catch (DateTimeParseException ex) {
//            // This catch block should now ideally only be hit if the user manually types an invalid format
//            System.err.println("DateTimeParseException during update for startDateTime: '" + startDateTime + "' or endDateTime: '" + endDateTime + "'. Error: " + ex.getMessage());
//            JOptionPane.showMessageDialog(this, "Invalid Date/Time format. Please use YYYY-MM-DDTHH:MM:SS (e.g., 2025-12-31T23:59:59).", "Input Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }


        Map<String, String> formData = new HashMap<>();
        formData.put("action", "updateElection");
        formData.put("electionId", String.valueOf(electionId));
        formData.put("electionName", name);
        formData.put("description", description);
//        formData.put("startDateTime", startDateTime); // Send the string as is from the table
//        formData.put("endDateTime", endDateTime);     // Send the string as is from the table
        formData.put("status", status);

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return ApiClient.post("/admin/election", formData);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    if (response != null && response.getBoolean("success")) {
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadElections(); // Refresh table after update
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to update election: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    // --- Create Constituency Panel ---
    private JPanel createCreateConstituencyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Increased padding
        panel.setBackground(new Color(255, 255, 255)); // Pure white background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create New Constituency", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(title, gbc);

        gbc.gridwidth = 1; // Reset gridwidth
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++;
        panel.add(new JLabel("Constituency Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        newConstituencyNameField = new JTextField(25);
        newConstituencyNameField.putClientProperty("JComponent.roundRect", true);
        panel.add(newConstituencyNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        newConstituencyDescriptionArea = new JTextArea(5, 25);
        newConstituencyDescriptionArea.setLineWrap(true);
        newConstituencyDescriptionArea.setWrapStyleWord(true);
        newConstituencyDescriptionArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scrollPane = new JScrollPane(newConstituencyDescriptionArea);
        scrollPane.setPreferredSize(new Dimension(newConstituencyNameField.getPreferredSize().width, newConstituencyDescriptionArea.getPreferredSize().height));
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton createButton = new JButton("Create Constituency");
        styleButton(createButton, new Color(52, 152, 219)); // Bright blue for create
        createButton.addActionListener(e -> createConstituency());
        panel.add(createButton, gbc);

        // Add some vertical glue at the bottom
        gbc.gridy++;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private void createConstituency() {
        String name = newConstituencyNameField.getText().trim();
        String description = newConstituencyDescriptionArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Constituency Name is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, String> formData = new HashMap<>();
        formData.put("action", "createConstituency");
        formData.put("name", name);
        formData.put("description", description);

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return ApiClient.post("/admin/election", formData);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    if (response != null && response.getBoolean("success")) {
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearCreateConstituencyFields();
                        // Automatically switch to "Manage Constituencies" tab after creation
                        electionTabs.setSelectedIndex(3);
                        loadConstituencies(); // Refresh the manage table
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to create constituency: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void clearCreateConstituencyFields() {
        newConstituencyNameField.setText("");
        newConstituencyDescriptionArea.setText("");
    }

    // --- Manage Constituencies Panel ---
    private JPanel createManageConstituenciesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10), // Padding
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Manage Existing Constituencies",
                javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18), new Color(66, 135, 245)
            )
        ));

        constituenciesTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for Name, Description
                return column > 0; // ID is not editable
            }
        };
        constituenciesTable = new JTable(constituenciesTableModel);
        styleTable(constituenciesTable); // Apply custom table styling
        constituenciesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(constituenciesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setBackground(panel.getBackground());

        JButton refreshButton = new JButton("Refresh Constituencies");
        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
        refreshButton.addActionListener(e -> loadConstituencies());
        buttonPanel.add(refreshButton);

        // Note: Update Constituency functionality is a placeholder.
        // The backend ElectionManagementServlet needs the updateConstituency method to be fully implemented.
        JButton updateButton = new JButton("Update Selected Constituency (Not fully implemented)");
        styleButton(updateButton, new Color(60, 179, 113)); // MediumSeaGreen
        updateButton.setEnabled(false); // Disable for now
        buttonPanel.add(updateButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // No initial load here, it's done via tab change listener for efficiency
        return panel;
    }

    private void loadConstituencies() {
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listConstituencies");
                return ApiClient.get("/admin/election", params);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    constituenciesTableModel.setRowCount(0); // Clear existing data
                    if (response != null && response.getBoolean("success")) {
                        JSONArray constituencies = response.getJSONArray("constituencies");
                        if (constituencies.length() == 0) {
                            addNoDataRow(constituenciesTableModel, "No constituencies found. Create one using the 'Create Constituency' tab.");
                        } else {
                            for (int i = 0; i < constituencies.length(); i++) {
                                JSONObject constituency = constituencies.getJSONObject(i);
                                Vector<Object> row = new Vector<>();
                                row.add(constituency.getInt("constituencyId"));
                                row.add(constituency.getString("name"));
                                row.add(constituency.optString("description", "")); // Use optString for optional fields
                                constituenciesTableModel.addRow(row);
                            }
                        }
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(ElectionManagementPanel.this, "Failed to load constituencies: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                        addNoDataRow(constituenciesTableModel, "Error loading constituencies: " + msg);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ElectionManagementPanel.this, "An error occurred loading constituencies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    addNoDataRow(constituenciesTableModel, "An unexpected error occurred while loading constituencies.");
                }
            }
        }.execute();
    }

    /**
     * Adds a "no data" message row to a given table model.
     * @param model The DefaultTableModel to modify.
     * @param message The message to display.
     */
    private void addNoDataRow(DefaultTableModel model, String message) {
        model.setRowCount(0); // Ensure table is empty
        Vector<Object> noDataRow = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            noDataRow.add(""); // Fill with empty strings
        }
        // Place message in the second column for better visibility if ID is first
        if (model.getColumnCount() > 1) {
            noDataRow.set(1, message);
        } else if (model.getColumnCount() > 0) {
            noDataRow.set(0, message);
        }
        model.addRow(noDataRow);
    }
}