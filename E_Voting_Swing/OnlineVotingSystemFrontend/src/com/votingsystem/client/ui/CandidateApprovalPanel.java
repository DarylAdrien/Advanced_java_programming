//// src/com/votingsystem/client/ui/CandidateApprovalPanel.java
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
//public class CandidateApprovalPanel extends JPanel {
//    private DashboardPanel parentDashboard;
//    private JTable candidatesTable;
//    private DefaultTableModel candidatesTableModel;
//
//    public CandidateApprovalPanel(DashboardPanel parentDashboard) {
//        this.parentDashboard = parentDashboard;
//        setLayout(new BorderLayout());
//        setBorder(BorderFactory.createTitledBorder("Candidate Approval Management"));
//
//        candidatesTableModel = new DefaultTableModel(new String[]{"ID", "User ID", "User Name", "Full Name", "Election", "Party", "Manifesto", "Status"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false; // All cells non-editable in table
//            }
//        };
//        candidatesTable = new JTable(candidatesTableModel);
//        candidatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        candidatesTable.getTableHeader().setReorderingAllowed(false);
//        JScrollPane scrollPane = new JScrollPane(candidatesTable);
//        add(scrollPane, BorderLayout.CENTER);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton refreshButton = new JButton("Refresh Candidates");
//        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
//        refreshButton.addActionListener(e -> loadCandidates());
//        buttonPanel.add(refreshButton);
//
//        JButton approveButton = new JButton("Approve Selected");
//        styleButton(approveButton, new Color(34, 139, 34)); // ForestGreen
//        approveButton.addActionListener(e -> updateCandidateStatus("approve"));
//        buttonPanel.add(approveButton);
//
//        JButton rejectButton = new JButton("Reject Selected");
//        styleButton(rejectButton, new Color(220, 20, 60)); // Crimson
//        rejectButton.addActionListener(e -> updateCandidateStatus("reject"));
//        buttonPanel.add(rejectButton);
//
//        add(buttonPanel, BorderLayout.SOUTH);
//
//        loadCandidates(); // Load candidates when panel is created
//    }
//
//    private void loadCandidates() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listAll"); // List all candidates for admin, including pending
//                return ApiClient.get("/candidate", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    candidatesTableModel.setRowCount(0); // Clear existing data
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray candidates = response.getJSONArray("candidates");
//                        for (int i = 0; i < candidates.length(); i++) {
//                            JSONObject candidate = candidates.getJSONObject(i);
//                            Vector<Object> row = new Vector<>();
//                            row.add(candidate.getInt("candidateId"));
//                            row.add(candidate.getInt("userId"));
//                            row.add(candidate.getString("userName"));
//                            row.add(candidate.getString("userFullName"));
//                            row.add(candidate.getString("electionName"));
//                            row.add(candidate.optString("partyAffiliation", "N/A"));
//                            row.add(candidate.optString("manifesto", "N/A"));
//                            row.add(candidate.getString("approvalStatus"));
//                            candidatesTableModel.addRow(row);
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "Failed to load candidates: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "An error occurred loading candidates: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void updateCandidateStatus(String action) {
//        int selectedRow = candidatesTable.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select a candidate to " + action + ".", "Selection Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int candidateId = (int) candidatesTableModel.getValueAt(selectedRow, 0);
//        String currentStatus = (String) candidatesTableModel.getValueAt(selectedRow, 7);
//
//        if ("approve".equals(action) && "APPROVED".equalsIgnoreCase(currentStatus)) {
//            JOptionPane.showMessageDialog(this, "Candidate is already APPROVED.", "Status Check", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
//        if ("reject".equals(action) && "REJECTED".equalsIgnoreCase(currentStatus)) {
//            JOptionPane.showMessageDialog(this, "Candidate is already REJECTED.", "Status Check", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("action", action); // "approve" or "reject"
//        formData.put("candidateId", String.valueOf(candidateId));
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
//                        JOptionPane.showMessageDialog(CandidateApprovalPanel.this, response.getString("message"), "Success", JOptionPane.INFORMATION_MESSAGE);
//                        loadCandidates(); // Refresh table after update
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "Failed to " + action + " candidate: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

public class CandidateApprovalPanel extends JPanel {
    private DashboardPanel parentDashboard;
    private JTable candidatesTable;
    private DefaultTableModel candidatesTableModel;
    private JButton refreshButton;
    private JButton approveButton;
    private JButton rejectButton;

    public CandidateApprovalPanel(DashboardPanel parentDashboard) {
        this.parentDashboard = parentDashboard;
        setLayout(new BorderLayout(15, 15)); // Add gaps for better spacing
        setBackground(new Color(245, 245, 250)); // Consistent background with Dashboard

        // Set a more engaging title for the panel
        setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 15, 15, 15), // Outer padding
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2), // CornflowerBlue border
                "Candidate Applications for Approval",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(70, 130, 180) // SteelBlue title color
            )
        ));

        // --- Table Setup ---
        candidatesTableModel = new DefaultTableModel(new String[]{"ID", "User ID", "User Name", "Full Name", "Election", "Party", "Manifesto", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells non-editable in table
            }
        };
        candidatesTable = new JTable(candidatesTableModel);
        candidatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidatesTable.getTableHeader().setReorderingAllowed(false);
        candidatesTable.setRowHeight(25); // Increase row height for better readability
        candidatesTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Set table font

        // Custom renderer for Status column
        candidatesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 7) { // Status column
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

        // Table Header Styling
        JTableHeader header = candidatesTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));
        header.setBackground(new Color(204, 229, 255)); // Light Blue
        header.setForeground(new Color(25, 25, 112)); // Midnight Blue
        header.setPreferredSize(new Dimension(header.getWidth(), 30)); // Increase header height


        JScrollPane scrollPane = new JScrollPane(candidatesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230))); // LightBlue border for scroll pane
        add(scrollPane, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Centered flow layout, more spacing
        buttonPanel.setBackground(getBackground()); // Match panel background

        refreshButton = new JButton("Refresh List"); // More descriptive text
        styleButton(refreshButton, new Color(100, 149, 237)); // CornflowerBlue
        refreshButton.addActionListener(e -> loadCandidates());
        buttonPanel.add(refreshButton);

        approveButton = new JButton("Approve Selected");
        styleButton(approveButton, new Color(46, 139, 87)); // SeaGreen, consistent with registration
        approveButton.addActionListener(e -> updateCandidateStatus("approve"));
        buttonPanel.add(approveButton);

        rejectButton = new JButton("Reject Selected");
        styleButton(rejectButton, new Color(220, 50, 60)); // A more vibrant red
        rejectButton.addActionListener(e -> updateCandidateStatus("reject"));
        buttonPanel.add(rejectButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCandidates(); // Load candidates when panel is created
    }

    private void loadCandidates() {
        // Disable buttons during API call
        setButtonsEnabled(false);

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listAll"); // List all candidates for admin, including pending
                return ApiClient.get("/candidate", params);
            }

            @Override
            protected void done() {
                // Re-enable buttons
                setButtonsEnabled(true);

                try {
                    JSONObject response = get();
                    candidatesTableModel.setRowCount(0); // Clear existing data
                    if (response != null && response.optBoolean("success", false)) {
                        JSONArray candidates = response.optJSONArray("candidates");
                        if (candidates != null) {
                            for (int i = 0; i < candidates.length(); i++) {
                                JSONObject candidate = candidates.getJSONObject(i);
                                Vector<Object> row = new Vector<>();
                                row.add(candidate.optInt("candidateId"));
                                row.add(candidate.optInt("userId"));
                                row.add(candidate.optString("userName"));
                                row.add(candidate.optString("userFullName"));
                                row.add(candidate.optString("electionName"));
                                row.add(candidate.optString("partyAffiliation", "N/A"));
                                row.add(candidate.optString("manifesto", "N/A"));
                                row.add(candidate.optString("approvalStatus"));
                                candidatesTableModel.addRow(row);
                            }
                        } else {
                            JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "No candidates found or candidates data is empty.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        String msg = (response != null) ? response.optString("message", "Unknown error.") : "No response from server.";
                        JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "Failed to load candidates: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "An error occurred loading candidates: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateCandidateStatus(String action) {
        int selectedRow = candidatesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate to " + action + ".", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int candidateId = (int) candidatesTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) candidatesTableModel.getValueAt(selectedRow, 7);
        String candidateName = (String) candidatesTableModel.getValueAt(selectedRow, 3); // Get full name for message

        if ("approve".equals(action) && "APPROVED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, candidateName + " is already APPROVED.", "Status Check", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if ("reject".equals(action) && "REJECTED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, candidateName + " is already REJECTED.", "Status Check", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String confirmationMessage = "Are you sure you want to " + action.toUpperCase() + " " + candidateName + "'s application?";
        int confirm = JOptionPane.showConfirmDialog(this, confirmationMessage, "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }

        Map<String, String> formData = new HashMap<>();
        formData.put("action", action); // "approve" or "reject"
        formData.put("candidateId", String.valueOf(candidateId));

        // Disable buttons during API call
        setButtonsEnabled(false);

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return ApiClient.post("/candidate", formData);
            }

            @Override
            protected void done() {
                // Re-enable buttons
                setButtonsEnabled(true);

                try {
                    JSONObject response = get();
                    if (response != null && response.optBoolean("success", false)) {
                        JOptionPane.showMessageDialog(CandidateApprovalPanel.this, response.optString("message", "Operation successful!"), "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadCandidates(); // Refresh table after update
                    } else {
                        String msg = (response != null) ? response.optString("message", "Unknown error.") : "No response from server.";
                        JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "Failed to " + action + " candidate: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(CandidateApprovalPanel.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 15)); // Slightly larger font
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1), // Darker border for depth
            new EmptyBorder(10, 20, 10, 20) // More padding
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect"); // For some LaF
    }

    private void setButtonsEnabled(boolean enabled) {
        refreshButton.setEnabled(enabled);
        approveButton.setEnabled(enabled);
        rejectButton.setEnabled(enabled);
    }
}