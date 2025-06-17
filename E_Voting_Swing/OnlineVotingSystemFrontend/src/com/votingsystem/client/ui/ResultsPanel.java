//// src/com/votingsystem/client/ui/ResultsPanel.java
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
//public class ResultsPanel extends JPanel {
//    private DashboardPanel parentDashboard;
//    private JComboBox<String> electionComboBox;
//    private JButton viewResultsButton;
//    private JTable resultsTable;
//    private DefaultTableModel resultsTableModel;
//    private Map<String, Integer> electionMap; // To store election name -> ID mapping
//
//    public ResultsPanel(DashboardPanel parentDashboard) {
//        this.parentDashboard = parentDashboard;
//        setLayout(new BorderLayout(10, 10));
//        setBorder(BorderFactory.createTitledBorder("View Election Results"));
//        setBackground(new Color(240, 255, 255)); // Azure
//
//        // Top panel for election selection
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
//        topPanel.setBackground(getBackground());
//
//        topPanel.add(new JLabel("Select Election:"));
//        electionComboBox = new JComboBox<>();
//        electionComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
//        topPanel.add(electionComboBox);
//
//        viewResultsButton = new JButton("View Results");
//        styleButton(viewResultsButton, new Color(70, 130, 180)); // SteelBlue
//        viewResultsButton.addActionListener(e -> viewResults());
//        topPanel.add(viewResultsButton);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        // Results table
//        resultsTableModel = new DefaultTableModel(new String[]{"Candidate Name", "Party Affiliation", "Vote Count"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        resultsTable = new JTable(resultsTableModel);
//        resultsTable.getTableHeader().setReorderingAllowed(false);
//        JScrollPane scrollPane = new JScrollPane(resultsTable);
//        add(scrollPane, BorderLayout.CENTER);
//
//        loadElections(); // Load elections on panel initialization
//    }
//
//    private void loadElections() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listElections");
//                params.put("status", "COMPLETED"); // Only show completed elections for results
//                return ApiClient.get("/admin/election", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    electionComboBox.removeAllItems();
//                    electionMap = new HashMap<>();
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray elections = response.getJSONArray("elections");
//                        if (elections.length() == 0) {
//                            electionComboBox.addItem("No completed elections available");
//                            viewResultsButton.setEnabled(false);
//                        } else {
//                             viewResultsButton.setEnabled(true);
//                            for (int i = 0; i < elections.length(); i++) {
//                                JSONObject election = elections.getJSONObject(i);
//                                String electionName = election.getString("electionName");
//                                int electionId = election.getInt("electionId");
//                                electionComboBox.addItem(electionName);
//                                electionMap.put(electionName, electionId);
//                            }
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ResultsPanel.this, "Failed to load elections: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                        electionComboBox.addItem("Error loading elections");
//                        viewResultsButton.setEnabled(false);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ResultsPanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                    electionComboBox.addItem("Error loading elections");
//                    viewResultsButton.setEnabled(false);
//                }
//            }
//        }.execute();
//    }
//
//    private void viewResults() {
//        String selectedElectionName = (String) electionComboBox.getSelectedItem();
//        if (selectedElectionName == null || selectedElectionName.isEmpty() || selectedElectionName.equals("No completed elections available") || selectedElectionName.equals("Error loading elections")) {
//            JOptionPane.showMessageDialog(this, "Please select a valid election.", "Selection Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int electionId = electionMap.getOrDefault(selectedElectionName, -1);
//        if (electionId == -1) {
//            JOptionPane.showMessageDialog(this, "Selected election ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("electionId", String.valueOf(electionId));
//                return ApiClient.get("/results", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    resultsTableModel.setRowCount(0); // Clear previous results
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray results = response.getJSONArray("results");
//                        if (results.length() == 0) {
//                            JOptionPane.showMessageDialog(ResultsPanel.this, "No results available for this election yet.", "Results", JOptionPane.INFORMATION_MESSAGE);
//                        }
//                        for (int i = 0; i < results.length(); i++) {
//                            JSONObject candidateResult = results.getJSONObject(i);
//                            Vector<Object> row = new Vector<>();
//                            row.add(candidateResult.getString("fullName"));
//                            row.add(candidateResult.optString("partyAffiliation", "N/A"));
//                            row.add(candidateResult.getInt("voteCount"));
//                            resultsTableModel.addRow(row);
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(ResultsPanel.this, "Failed to fetch results: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(ResultsPanel.this, "An error occurred fetching results: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

// src/com/votingsystem/client/ui/ResultsPanel.java
package com.votingsystem.client.ui;

import com.votingsystem.client.util.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ResultsPanel extends JPanel {
    private DashboardPanel parentDashboard;
    private JComboBox<String> electionComboBox;
    private JButton viewResultsButton;
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private Map<String, Integer> electionMap; // To store election name -> ID mapping

    public ResultsPanel(DashboardPanel parentDashboard) {
        this.parentDashboard = parentDashboard;
        setLayout(new BorderLayout(20, 20)); // Increased gaps for better spacing
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel
        setBackground(new Color(245, 248, 250)); // Light grey-blue, softer than Azure

        // Header Panel for Title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(getBackground());
        JLabel titleLabel = new JLabel("Election Results Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Larger, more prominent title
        titleLabel.setForeground(new Color(44, 62, 80)); // Darker, professional color
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Top panel for election selection
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Centered flow layout
        selectionPanel.setBackground(new Color(236, 240, 241)); // Lighter grey for distinction
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel selectLabel = new JLabel("Select Election:");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        selectionPanel.add(selectLabel);

        electionComboBox = new JComboBox<>();
        electionComboBox.setFont(new Font("Arial", Font.PLAIN, 15));
        electionComboBox.setPreferredSize(new Dimension(250, 35)); // Fixed size for consistency
        selectionPanel.add(electionComboBox);

        viewResultsButton = new JButton("View Results");
        styleButton(viewResultsButton, new Color(52, 152, 219)); // Brighter blue
        viewResultsButton.addActionListener(e -> viewResults());
        selectionPanel.add(viewResultsButton);

        add(selectionPanel, BorderLayout.NORTH); // Changed to NORTH to place above the table

        // Results table
        resultsTableModel = new DefaultTableModel(new String[]{"Candidate Name", "Party Affiliation", "Vote Count"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultsTable.setRowHeight(25); // Increased row height for readability
        resultsTable.setGridColor(new Color(220, 220, 220)); // Lighter grid lines
        resultsTable.setSelectionBackground(new Color(174, 214, 241)); // Light blue selection

        // Table Header Styling
        JTableHeader tableHeader = resultsTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));
        tableHeader.setBackground(new Color(70, 130, 180)); // SteelBlue for header
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(true); // Allow resizing columns

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1)); // Subtle border
        add(scrollPane, BorderLayout.CENTER);

        loadElections(); // Load elections on panel initialization
    }

    private void loadElections() {
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listElections");
                params.put("status", "COMPLETED"); // Only show completed elections for results
                return ApiClient.get("/admin/election", params);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    electionComboBox.removeAllItems();
                    electionMap = new HashMap<>();
                    if (response != null && response.getBoolean("success")) {
                        JSONArray elections = response.getJSONArray("elections");
                        if (elections.length() == 0) {
                            electionComboBox.addItem("No completed elections available");
                            viewResultsButton.setEnabled(false);
                        } else {
                            viewResultsButton.setEnabled(true);
                            for (int i = 0; i < elections.length(); i++) {
                                JSONObject election = elections.getJSONObject(i);
                                String electionName = election.getString("electionName");
                                int electionId = election.getInt("electionId");
                                electionComboBox.addItem(electionName);
                                electionMap.put(electionName, electionId);
                            }
                        }
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(ResultsPanel.this, "Failed to load elections: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                        electionComboBox.addItem("Error loading elections");
                        viewResultsButton.setEnabled(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ResultsPanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    electionComboBox.addItem("Error loading elections");
                    viewResultsButton.setEnabled(false);
                }
            }
        }.execute();
    }

    private void viewResults() {
        String selectedElectionName = (String) electionComboBox.getSelectedItem();
        if (selectedElectionName == null || selectedElectionName.isEmpty() || selectedElectionName.equals("No completed elections available") || selectedElectionName.equals("Error loading elections")) {
            JOptionPane.showMessageDialog(this, "Please select a valid election.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int electionId = electionMap.getOrDefault(selectedElectionName, -1);
        if (electionId == -1) {
            JOptionPane.showMessageDialog(this, "Selected election ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("electionId", String.valueOf(electionId));
                return ApiClient.get("/results", params);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    resultsTableModel.setRowCount(0); // Clear previous results
                    if (response != null && response.getBoolean("success")) {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() == 0) {
                            JOptionPane.showMessageDialog(ResultsPanel.this, "No results available for this election yet.", "Results", JOptionPane.INFORMATION_MESSAGE);
                        }
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject candidateResult = results.getJSONObject(i);
                            Vector<Object> row = new Vector<>();
                            row.add(candidateResult.getString("fullName"));
                            row.add(candidateResult.optString("partyAffiliation", "N/A"));
                            row.add(candidateResult.getInt("voteCount"));
                            resultsTableModel.addRow(row);
                        }
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(ResultsPanel.this, "Failed to fetch results: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ResultsPanel.this, "An error occurred fetching results: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 15)); // Slightly larger font for buttons
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1), // Darker border for depth
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // More padding
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add a subtle hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
}