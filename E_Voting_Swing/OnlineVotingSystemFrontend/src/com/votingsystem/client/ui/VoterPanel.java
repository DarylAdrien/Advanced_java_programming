//// src/com/votingsystem/client/ui/VoterPanel.java
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
//public class VoterPanel extends JPanel {
//    private DashboardPanel parentDashboard;
//    private JTable electionsTable;
//    private DefaultTableModel electionsTableModel;
//    private JTable candidatesTable;
//    private DefaultTableModel candidatesTableModel;
//
//    private int selectedElectionId = -1;
//
//    public VoterPanel(DashboardPanel parentDashboard) {
//        this.parentDashboard = parentDashboard;
//        setLayout(new GridBagLayout());
//        setBorder(BorderFactory.createTitledBorder("Voter Dashboard - Cast Your Vote"));
//        setBackground(new Color(245, 255, 250)); // MintCream
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10);
//        gbc.fill = GridBagConstraints.BOTH;
//
//        // --- Active Elections Panel ---
//        JPanel electionsPanel = new JPanel(new BorderLayout());
//        electionsPanel.setBorder(BorderFactory.createTitledBorder("Available Elections (Status: ACTIVE)"));
//        electionsTableModel = new DefaultTableModel(new String[]{"ID", "Election Name", "Description", "Starts", "Ends"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        electionsTable = new JTable(electionsTableModel);
//        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        electionsTable.getTableHeader().setReorderingAllowed(false);
//        electionsTable.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting() && electionsTable.getSelectedRow() != -1) {
//                selectedElectionId = (int) electionsTableModel.getValueAt(electionsTable.getSelectedRow(), 0);
//                loadCandidatesForSelectedElection(selectedElectionId);
//            } else {
//                selectedElectionId = -1;
//                candidatesTableModel.setRowCount(0); // Clear candidates if no election selected
//            }
//        });
//        electionsPanel.add(new JScrollPane(electionsTable), BorderLayout.CENTER);
//
//        JButton refreshElectionsButton = new JButton("Refresh Elections");
//        styleButton(refreshElectionsButton, new Color(70, 130, 180)); // SteelBlue
//        refreshElectionsButton.addActionListener(e -> loadElections());
//        JPanel electionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        electionButtonPanel.add(refreshElectionsButton);
//        electionsPanel.add(electionButtonPanel, BorderLayout.SOUTH);
//
//
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.weightx = 1.0;
//        gbc.weighty = 0.5;
//        add(electionsPanel, gbc);
//
//        // --- Candidates Panel ---
//        JPanel candidatesPanel = new JPanel(new BorderLayout());
//        candidatesPanel.setBorder(BorderFactory.createTitledBorder("Candidates for Selected Election (Select an election above)"));
//        candidatesTableModel = new DefaultTableModel(new String[]{"ID", "Candidate Name", "Party", "Manifesto"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        candidatesTable = new JTable(candidatesTableModel);
//        candidatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        candidatesTable.getTableHeader().setReorderingAllowed(false);
//        candidatesPanel.add(new JScrollPane(candidatesTable), BorderLayout.CENTER);
//
//        JButton castVoteButton = new JButton("Cast My Vote for Selected Candidate");
//        styleButton(castVoteButton, new Color(34, 139, 34)); // ForestGreen
//        castVoteButton.addActionListener(e -> castVote());
//        JPanel candidateButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        candidateButtonPanel.add(castVoteButton);
//        candidatesPanel.add(candidateButtonPanel, BorderLayout.SOUTH);
//
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.weighty = 0.5;
//        add(candidatesPanel, gbc);
//
//        loadElections(); // Initial load
//    }
//
//    private void loadElections() {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listElections");
//                params.put("status", "ACTIVE"); // Only show active elections for voters
//                return ApiClient.get("/admin/election", params);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    electionsTableModel.setRowCount(0); // Clear existing data
//                    candidatesTableModel.setRowCount(0); // Also clear candidates
//                    selectedElectionId = -1; // Reset selected election
//
//                    if (response != null && response.getBoolean("success")) {
//                        JSONArray elections = response.getJSONArray("elections");
//                        for (int i = 0; i < elections.length(); i++) {
//                            JSONObject election = elections.getJSONObject(i);
//                            Vector<Object> row = new Vector<>();
//                            row.add(election.getInt("electionId"));
//                            row.add(election.getString("electionName"));
//                            row.add(election.optString("description", ""));
//                            row.add(election.getString("startDateTime"));
//                            row.add(election.getString("endDateTime"));
//                            electionsTableModel.addRow(row);
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(VoterPanel.this, "Failed to load active elections: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(VoterPanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void loadCandidatesForSelectedElection(int electionId) {
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                Map<String, String> params = new HashMap<>();
//                params.put("action", "listByElection");
//                params.put("electionId", String.valueOf(electionId));
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
//                            // Only show approved candidates to voters
//                            if ("APPROVED".equalsIgnoreCase(candidate.getString("approvalStatus"))) {
//                                Vector<Object> row = new Vector<>();
//                                row.add(candidate.getInt("candidateId"));
//                                row.add(candidate.getString("userFullName"));
//                                row.add(candidate.optString("partyAffiliation", "N/A"));
//                                row.add(candidate.optString("manifesto", "N/A"));
//                                candidatesTableModel.addRow(row);
//                            }
//                        }
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(VoterPanel.this, "Failed to load candidates: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(VoterPanel.this, "An error occurred loading candidates: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }.execute();
//    }
//
//    private void castVote() {
//        if (selectedElectionId == -1) {
//            JOptionPane.showMessageDialog(this, "Please select an election first.", "Voting Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int selectedCandidateRow = candidatesTable.getSelectedRow();
//        if (selectedCandidateRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select a candidate to vote for.", "Voting Error", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int candidateId = (int) candidatesTableModel.getValueAt(selectedCandidateRow, 0);
//
//        // Confirmation dialog
//        int confirm = JOptionPane.showConfirmDialog(this,
//                "Are you sure you want to cast your vote for " + candidatesTableModel.getValueAt(selectedCandidateRow, 1) + "?\n" +
//                "You cannot change your vote once cast.", "Confirm Vote", JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.NO_OPTION) {
//            return;
//        }
//
//        Map<String, String> formData = new HashMap<>();
//        formData.put("electionId", String.valueOf(selectedElectionId));
//        formData.put("candidateId", String.valueOf(candidateId));
//
//        new SwingWorker<JSONObject, Void>() {
//            @Override
//            protected JSONObject doInBackground() {
//                return ApiClient.post("/vote", formData);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    JSONObject response = get();
//                    if (response != null && response.getBoolean("success")) {
//                        JOptionPane.showMessageDialog(VoterPanel.this, response.getString("message"), "Vote Success", JOptionPane.INFORMATION_MESSAGE);
//                        loadElections(); // Refresh elections to show voted status or hide the election
//                    } else {
//                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
//                        JOptionPane.showMessageDialog(VoterPanel.this, "Vote Failed: " + msg, "Vote Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(VoterPanel.this, "An error occurred during voting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

// src/com/votingsystem/client/ui/VoterPanel.java
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

public class VoterPanel extends JPanel {
    private DashboardPanel parentDashboard;
    private JTable electionsTable;
    private DefaultTableModel electionsTableModel;
    private JTable candidatesTable;
    private DefaultTableModel candidatesTableModel;
    // Declare candidatesPanel as a class member variable here
    private JPanel candidatesPanel; 

    private int selectedElectionId = -1;

    public VoterPanel(DashboardPanel parentDashboard) {
        this.parentDashboard = parentDashboard;
        // Overall panel styling and layout
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Generous padding around the panel
        setBackground(new Color(245, 248, 250)); // Soft, light grey-blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased insets for more spacing between components
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; // Panels will expand horizontally

        // --- Header Panel for Title ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(getBackground()); // Match panel background
        JLabel titleLabel = new JLabel("Cast Your Vote");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Larger, prominent title
        titleLabel.setForeground(new Color(44, 62, 80)); // Dark, professional text color
        headerPanel.add(titleLabel);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.05; // Give very little vertical space to the title
        add(headerPanel, gbc);

        // --- Active Elections Panel ---
        JPanel electionsPanel = new JPanel(new BorderLayout(10, 10)); // Added internal gaps
        electionsPanel.setBackground(new Color(250, 250, 250)); // Slightly lighter background for the sub-panel
        // Custom TitledBorder for a modern look
        electionsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Subtle line border
            "Available Elections", // Title text
            javax.swing.border.TitledBorder.LEADING, // Title position
            javax.swing.border.TitledBorder.TOP, // Title justification
            new Font("Arial", Font.BOLD, 16), // Font for the title
            new Color(66, 135, 245) // Blue color for the title text
        ));

        electionsTableModel = new DefaultTableModel(new String[]{"ID", "Election Name", "Description", "Starts", "Ends"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        electionsTable = new JTable(electionsTableModel);
        styleTable(electionsTable); // Apply common table styling
        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // --- IMPORTANT FIX HERE: Initialize candidatesPanel before referencing it in the listener ---
        // Initialize candidatesPanel here so it's ready for the listener
        candidatesPanel = new JPanel(new BorderLayout(10, 10)); // Initialize the member variable
        candidatesPanel.setBackground(new Color(250, 250, 250)); // Match elections panel background
        candidatesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            "Candidates for Selected Election (Select an election above)", // Initial title
            javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16), new Color(66, 135, 245)
        ));

        // Listener to load candidates when an election is selected
        electionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && electionsTable.getSelectedRow() != -1) {
                selectedElectionId = (int) electionsTableModel.getValueAt(electionsTable.getSelectedRow(), 0);
                loadCandidatesForSelectedElection(selectedElectionId);
                // Update candidates panel title to show selected election
                if (electionsTable.getSelectedRow() != -1) {
                    // Access the class member variable candidatesPanel
                    candidatesPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                        "Candidates for: " + electionsTableModel.getValueAt(electionsTable.getSelectedRow(), 1),
                        javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16), new Color(66, 135, 245)
                    ));
                }
            } else {
                selectedElectionId = -1;
                candidatesTableModel.setRowCount(0); // Clear candidates if no election selected
                // Access the class member variable candidatesPanel
                candidatesPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                    "Candidates for Selected Election (Select an election above)",
                    javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 16), new Color(66, 135, 245)
                ));
            }
        });
        electionsPanel.add(new JScrollPane(electionsTable), BorderLayout.CENTER);

        JPanel electionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Align right
        electionButtonPanel.setBackground(electionsPanel.getBackground());
        JButton refreshElectionsButton = new JButton("Refresh Elections");
        styleButton(refreshElectionsButton, new Color(52, 152, 219)); // Bright, inviting blue
        refreshElectionsButton.addActionListener(e -> loadElections());
        electionButtonPanel.add(refreshElectionsButton);
        electionsPanel.add(electionButtonPanel, BorderLayout.SOUTH);

        gbc.gridy = 1;
        gbc.weighty = 0.5; // Give half of the remaining vertical space
        add(electionsPanel, gbc);

        // --- Candidates Panel ---
        // candidatesPanel is now initialized above, before the listener.
        candidatesTableModel = new DefaultTableModel(new String[]{"ID", "Candidate Name", "Party", "Manifesto"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        candidatesTable = new JTable(candidatesTableModel);
        styleTable(candidatesTable); // Apply common table styling
        candidatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidatesPanel.add(new JScrollPane(candidatesTable), BorderLayout.CENTER);

        JPanel candidateButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Align right
        candidateButtonPanel.setBackground(candidatesPanel.getBackground());
        JButton castVoteButton = new JButton("Cast My Vote for Selected Candidate");
        styleButton(castVoteButton, new Color(46, 204, 113)); // Vibrant emerald green for voting
        castVoteButton.addActionListener(e -> castVote());
        candidateButtonPanel.add(castVoteButton);
        candidatesPanel.add(candidateButtonPanel, BorderLayout.SOUTH);

        gbc.gridy = 2;
        gbc.weighty = 0.45; // Give the other half, leaving a small gap for title if needed
        add(candidatesPanel, gbc);

        loadElections(); // Initial load of active elections
    }

    /**
     * Applies common styling to JTables.
     * @param table The JTable to style.
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(28); // Increased row height for better touch/click targets and readability
        table.setGridColor(new Color(220, 220, 220)); // Lighter grid lines
        table.setSelectionBackground(new Color(174, 214, 241)); // Light blue selection
        table.setSelectionForeground(Color.BLACK); // Keep text black on selection

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));
        tableHeader.setBackground(new Color(70, 130, 180)); // SteelBlue for header background
        tableHeader.setForeground(Color.WHITE); // White text for header
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(true); // Allow column resizing
    }

    /**
     * Applies common styling to JButtons, including a hover effect.
     * @param button The JButton to style.
     * @param bgColor The background color for the button.
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Remove focus border
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker().darker(), 1), // Darker, more defined border
                BorderFactory.createEmptyBorder(10, 25, 10, 25) // More padding inside button
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        // Add a subtle hover effect for better user feedback
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter()); // Slightly brighter on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor); // Restore original color
            }
        });
    }

    private void loadElections() {
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listElections");
                params.put("status", "ACTIVE"); // Only show active elections for voters
                return ApiClient.get("/admin/election", params);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    electionsTableModel.setRowCount(0); // Clear existing data
                    candidatesTableModel.setRowCount(0); // Also clear candidates
                    selectedElectionId = -1; // Reset selected election ID

                    if (response != null && response.getBoolean("success")) {
                        JSONArray elections = response.getJSONArray("elections");
                        if (elections.length() == 0) {
                            // Display a message if no active elections are found
                            Vector<Object> noElectionsRow = new Vector<>();
                            noElectionsRow.add("");
                            noElectionsRow.add("No active elections available.");
                            noElectionsRow.add("");
                            noElectionsRow.add("");
                            noElectionsRow.add("");
                            electionsTableModel.addRow(noElectionsRow);
                            electionsTable.setEnabled(false); // Disable selection if no elections
                        } else {
                            electionsTable.setEnabled(true); // Enable if there are elections
                            for (int i = 0; i < elections.length(); i++) {
                                JSONObject election = elections.getJSONObject(i);
                                Vector<Object> row = new Vector<>();
                                row.add(election.getInt("electionId"));
                                row.add(election.getString("electionName"));
                                row.add(election.optString("description", ""));
                                row.add(election.getString("startDateTime"));
                                row.add(election.getString("endDateTime"));
                                electionsTableModel.addRow(row);
                            }
                        }
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(VoterPanel.this, "Failed to load active elections: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                        // Add error row to table
                        Vector<Object> errorRow = new Vector<>();
                        errorRow.add("");
                        errorRow.add("Error loading elections.");
                        errorRow.add("");
                        errorRow.add("");
                        errorRow.add("");
                        electionsTableModel.addRow(errorRow);
                        electionsTable.setEnabled(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(VoterPanel.this, "An error occurred loading elections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    // Add error row to table
                    Vector<Object> errorRow = new Vector<>();
                    errorRow.add("");
                    errorRow.add("An error occurred.");
                    errorRow.add("");
                    errorRow.add("");
                    errorRow.add("");
                    electionsTableModel.addRow(errorRow);
                    electionsTable.setEnabled(false);
                }
            }
        }.execute();
    }

    private void loadCandidatesForSelectedElection(int electionId) {
        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "listByElection");
                params.put("electionId", String.valueOf(electionId));
                
                return ApiClient.get("/candidate", params);
            }

            @Override
            protected void done() {
                try {
                    JSONObject response = get();
                    candidatesTableModel.setRowCount(0); // Clear existing data
                    if (response != null && response.getBoolean("success")) {
                        JSONArray candidates = response.getJSONArray("candidates");
                        if (candidates.length() == 0) {
                            // Display a message if no candidates are found
                            Vector<Object> noCandidatesRow = new Vector<>();
                            noCandidatesRow.add("");
                            noCandidatesRow.add("No approved candidates for this election.");
                            noCandidatesRow.add("");
                            noCandidatesRow.add("");
                            candidatesTableModel.addRow(noCandidatesRow);
                        } else {
                            for (int i = 0; i < candidates.length(); i++) {
                                JSONObject candidate = candidates.getJSONObject(i);
                                // Only show approved candidates to voters
                                if ("APPROVED".equalsIgnoreCase(candidate.getString("approvalStatus"))) {
                                    Vector<Object> row = new Vector<>();
                                    row.add(candidate.getInt("candidateId"));
                                    row.add(candidate.getString("userFullName"));
                                    row.add(candidate.optString("partyAffiliation", "N/A"));
                                    row.add(candidate.optString("manifesto", "N/A"));
                                    candidatesTableModel.addRow(row);
                                }
                            }
                        }
                    } else {
                        String msg = (response != null) ? response.getString("message") : "Unknown error.";
                        JOptionPane.showMessageDialog(VoterPanel.this, "Failed to load candidates: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                        // Add error row to table
                        Vector<Object> errorRow = new Vector<>();
                        errorRow.add("");
                        errorRow.add("Error loading candidates.");
                        errorRow.add("");
                        errorRow.add("");
                        candidatesTableModel.addRow(errorRow);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(VoterPanel.this, "An error occurred loading candidates: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    // Add error row to table
                    Vector<Object> errorRow = new Vector<>();
                    errorRow.add("");
                    errorRow.add("An error occurred.");
                    errorRow.add("");
                    errorRow.add("");
                    candidatesTableModel.addRow(errorRow);
                }
            }
        }.execute();
    }

    private void castVote() {
        if (selectedElectionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an election first.", "Voting Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedCandidateRow = candidatesTable.getSelectedRow();
        if (selectedCandidateRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate to vote for.", "Voting Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ensure a valid candidate row is selected before attempting to get ID
        // This check prevents ClassCastException if the "No candidates" message row is selected
        if (candidatesTable.getValueAt(selectedCandidateRow, 0) instanceof Integer) {
            int candidateId = (int) candidatesTableModel.getValueAt(selectedCandidateRow, 0);

            // Confirmation dialog for vote
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cast your vote for " + candidatesTableModel.getValueAt(selectedCandidateRow, 1) + "?\n" +
                    "You cannot change your vote once cast. Your vote is final.",
                    "Confirm Your Vote", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); // QUESTION_MESSAGE icon

            if (confirm == JOptionPane.NO_OPTION) {
                return;
            }

            Map<String, String> formData = new HashMap<>();
            formData.put("electionId", String.valueOf(selectedElectionId));
            formData.put("candidateId", String.valueOf(candidateId));

            new SwingWorker<JSONObject, Void>() {
                @Override
                protected JSONObject doInBackground() {
                    return ApiClient.post("/vote", formData);
                }

                @Override
                protected void done() {
                    try {
                        JSONObject response = get();
                        if (response != null && response.getBoolean("success")) {
                            JOptionPane.showMessageDialog(VoterPanel.this, response.getString("message"), "Vote Success", JOptionPane.INFORMATION_MESSAGE);
                            loadElections(); // Refresh elections to show voted status or hide the election
                        } else {
                            String msg = (response != null) ? response.getString("message") : "Unknown error.";
                            JOptionPane.showMessageDialog(VoterPanel.this, "Vote Failed: " + msg, "Vote Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(VoterPanel.this, "An error occurred during voting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } else {
             JOptionPane.showMessageDialog(this, "Please select a valid candidate. (ID not found)", "Voting Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}