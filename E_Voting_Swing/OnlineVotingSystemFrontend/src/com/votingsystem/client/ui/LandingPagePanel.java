//// src/com/votingsystem/client/ui/LandingPagePanel.java
//package com.votingsystem.client.ui;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.net.URL; // For loading image from resources
//
//public class LandingPagePanel extends JPanel {
//    private OnlineVotingApp parentFrame;
//
//    public LandingPagePanel(OnlineVotingApp parentFrame) {
//        this.parentFrame = parentFrame;
//        setLayout(new BorderLayout()); // Use BorderLayout for main structure
//        setBackground(new Color(240, 248, 255)); // AliceBlue
//
//        // --- 1. Navbar ---
//        add(createNavbar(), BorderLayout.NORTH);
//
//        // --- Main Content Area (Scrollable) ---
//        JPanel contentPanel = new JPanel();
//        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Stack elements vertically
//        contentPanel.setBackground(new Color(240, 248, 255));
//        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding
//
//        // Portal Title (moved from old landingPanel, now more prominent)
//        JLabel mainTitleLabel = new JLabel("Welcome to the Secure Online Voting Portal", SwingConstants.CENTER);
//        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 36));
//        mainTitleLabel.setForeground(new Color(25, 25, 112)); // MidnightBlue
//        mainTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        contentPanel.add(mainTitleLabel);
//        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer
//
//        // Election Image
//        contentPanel.add(createElectionImagePanel());
//        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer
//
//        // Previous Results and Upcoming Election Details (Grid Layout)
//        contentPanel.add(createElectionDetailsPanel());
//        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer
//
//        // Key Features (Card-like Layout)
//        contentPanel.add(createKeyFeaturesPanel());
//        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer
//
//        // Support and Contact Section
//        contentPanel.add(createSupportAndContactPanel());
//        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer
//
//        // Make the content scrollable if it exceeds frame height
//        JScrollPane scrollPane = new JScrollPane(contentPanel);
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
//        add(scrollPane, BorderLayout.CENTER);
//
//
//        // --- 2. Footer ---
//        add(createFooter(), BorderLayout.SOUTH);
//    }
//
//    private JPanel createNavbar() {
//        JPanel navbar = new JPanel();
//        navbar.setBackground(new Color(70, 130, 180)); // SteelBlue
//        navbar.setLayout(new BorderLayout());
//        navbar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
//
//        // Panel for Logo and Portal Name
//        JPanel logoAndNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // 10px gap between logo and text
//        logoAndNamePanel.setOpaque(false); // Make it transparent
//
//        // Logo Label
//        JLabel logoLabel = new JLabel();
//        URL logoUrl = getClass().getResource("/images/logo.png"); // Path to your logo image
//        if (logoUrl != null) {
//            ImageIcon originalIcon = new ImageIcon(logoUrl);
//            Image image = originalIcon.getImage();
//            Image scaledImage = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH); // Adjust size as needed
//            logoLabel.setIcon(new ImageIcon(scaledImage));
//        } else {
//            // Fallback text if logo not found
//            logoLabel.setText("[Logo]");
//            logoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//            logoLabel.setForeground(Color.RED);
//        }
//        logoAndNamePanel.add(logoLabel);
//
//
//        JLabel portalName = new JLabel("Voting Portal");
//        portalName.setFont(new Font("Arial", Font.BOLD, 24));
//        portalName.setForeground(Color.WHITE);
//        logoAndNamePanel.add(portalName);
//
//        navbar.add(logoAndNamePanel, BorderLayout.WEST); // Add the combined panel to the left
//
//
//        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
//        navButtons.setOpaque(false); // Make it transparent to show navbar background
//
//        JButton loginButton = new JButton("Login");
//        styleNavButton(loginButton);
//        loginButton.addActionListener(e -> parentFrame.showPanel("Login"));
//        navButtons.add(loginButton);
//
//        JButton registerButton = new JButton("Register");
//        styleNavButton(registerButton);
//        registerButton.addActionListener(e -> parentFrame.showPanel("Register"));
//        navButtons.add(registerButton);
//
//        navbar.add(navButtons, BorderLayout.EAST);
//        return navbar;
//    }
//
//    private void styleNavButton(JButton button) {
//        button.setFont(new Font("Arial", Font.BOLD, 16));
//        button.setBackground(new Color(88, 145, 200)); // Slightly lighter blue for buttons
//        button.setForeground(Color.WHITE);
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
//        button.setPreferredSize(new Dimension(100, 35));
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//    }
//
//    private JPanel createElectionImagePanel() {
//        JPanel imagePanel = new JPanel(new BorderLayout());
//        imagePanel.setBackground(new Color(240, 248, 255));
//        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2)); // LightBlue border
//
//        JLabel imageLabel = new JLabel();
//        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
//
//        // Load image from resources (ensure 'election.png' is in your classpath, e.g., in a 'resources' folder)
//        URL imageUrl = getClass().getResource("/images/election_hero.png"); // Adjust path if needed
//        if (imageUrl != null) {
//            ImageIcon originalIcon = new ImageIcon(imageUrl);
//            // Scale image to fit within the panel (adjust dimensions as needed)
//            Image image = originalIcon.getImage();
//            Image scaledImage = image.getScaledInstance(600, 300, Image.SCALE_SMOOTH); // Fixed size for demo
//            imageLabel.setIcon(new ImageIcon(scaledImage));
//        } else {
//            imageLabel.setText("Election Image Not Found");
//            imageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
//            imageLabel.setForeground(Color.RED);
//        }
//        imagePanel.add(imageLabel, BorderLayout.CENTER);
//        return imagePanel;
//    }
//
//    private JPanel createElectionDetailsPanel() {
//        JPanel detailsPanel = new JPanel(new BorderLayout());
//        detailsPanel.setBackground(new Color(240, 248, 255));
//        detailsPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
//            "Election Information",
//            javax.swing.border.TitledBorder.CENTER,
//            javax.swing.border.TitledBorder.TOP,
//            new Font("Arial", Font.BOLD, 20),
//            new Color(25, 25, 112)
//        ));
//
//        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 row, 2 columns, 20px horizontal gap
//        gridPanel.setOpaque(false); // Inherit background from parent
//
//        // Previous Results Panel
//        JPanel prevResultsPanel = new JPanel(new BorderLayout());
//        prevResultsPanel.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(100, 149, 237), 1), // CornflowerBlue
//            BorderFactory.createEmptyBorder(10, 10, 10, 10)
//        ));
//        prevResultsPanel.setBackground(Color.WHITE);
//        JLabel prevResultsTitle = new JLabel("Previous Election Results", SwingConstants.CENTER);
//        prevResultsTitle.setFont(new Font("Arial", Font.BOLD, 18));
//        prevResultsTitle.setForeground(new Color(25, 25, 112));
//        prevResultsPanel.add(prevResultsTitle, BorderLayout.NORTH);
//
//        JLabel prevResultsLabel = new JLabel(
//            "<html>" +
//            "<b>2024 National Elections:</b><br>" +
//            "- Total Seats: 543<br>" +
//            "- Party A: 300 seats (55.2%)<br>" +
//            "- Party B: 200 seats (36.8%)<br>" +
//            "- Others: 43 seats (8.0%)<br><br>" +
//            "<b>2023 State Assembly Elections (Puducherry):</b><br>" +
//            "- Total Constituencies: 30<br>" +
//            "- Party X: 18 seats (60%)<br>" +
//            "- Party Y: 10 seats (33.3%)<br>" +
//            "- Independents: 2 seats (6.7%)<br><br>" +
//            "<b>2022 Municipal Corporation Elections (Puducherry):</b><br>" +
//            "- Total Wards: 42<br>" +
//            "- Turnout: 70.5%<br>" +
//            "- Major Issues: Local Development, Sanitation" +
//            "</html>"
//        );
//        prevResultsLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to the top
//        prevResultsLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
//        prevResultsPanel.add(new JScrollPane(prevResultsLabel), BorderLayout.CENTER); // Wrap in scroll pane for long content
//        gridPanel.add(prevResultsPanel);
//
//
//        // Upcoming Elections Panel
//        JPanel upcomingPanel = new JPanel(new BorderLayout());
//        upcomingPanel.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(100, 149, 237), 1), // CornflowerBlue
//            BorderFactory.createEmptyBorder(10, 10, 10, 10)
//        ));
//        upcomingPanel.setBackground(Color.WHITE);
//        JLabel upcomingTitle = new JLabel("Upcoming Elections", SwingConstants.CENTER);
//        upcomingTitle.setFont(new Font("Arial", Font.BOLD, 18));
//        upcomingTitle.setForeground(new Color(25, 25, 112));
//        upcomingPanel.add(upcomingTitle, BorderLayout.NORTH);
//        JTextArea upcomingDetailsArea = new JTextArea(
//            "National Elections 2029:\n" +
//            "- Dates: October 1 - October 15, 2029\n" +
//            "- Registration Deadline: September 1, 2029\n" +
//            "- Key Candidates: To be announced\n" +
//            "- Important Issues: Economic Growth, Healthcare, Education\n\n" +
//            "State By-Elections (Puducherry - Oulgaret Constituency):\n" +
//            "- Dates: November 5 - November 10, 2025\n" +
//            "- Registration Deadline: October 15, 2025\n" +
//            "- Polling Booths: Local community centers\n" +
//            "- Eligibility: Registered voters in Oulgaret Constituency\n\n" +
//            "Panchayat Elections (Local Areas):\n" +
//            "- Dates: March 1 - March 15, 2026\n" +
//            "- Purpose: Local Governance, Infrastructure Development"
//        );
//        upcomingDetailsArea.setEditable(false);
//        upcomingDetailsArea.setLineWrap(true);
//        upcomingDetailsArea.setWrapStyleWord(true);
//        upcomingDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
//        upcomingDetailsArea.setBorder(new EmptyBorder(5, 5, 5, 5));
//        upcomingPanel.add(new JScrollPane(upcomingDetailsArea), BorderLayout.CENTER);
//        gridPanel.add(upcomingPanel);
//
//        detailsPanel.add(gridPanel, BorderLayout.CENTER);
//        return detailsPanel;
//    }
//
//    private JPanel createKeyFeaturesPanel() {
//        JPanel featuresPanel = new JPanel();
//        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS)); // Stack title and grid
//        featuresPanel.setBackground(new Color(240, 248, 255));
//        featuresPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(60, 179, 113), 2), // MediumSeaGreen
//            "Key Features",
//            javax.swing.border.TitledBorder.CENTER,
//            javax.swing.border.TitledBorder.TOP,
//            new Font("Arial", Font.BOLD, 20),
//            new Color(25, 25, 112)
//        ));
//
//        // Changed to 4 rows for 8 cards. Adjust if you add more/fewer.
//        JPanel cardGrid = new JPanel(new GridLayout(4, 2, 20, 20));
//        cardGrid.setOpaque(false);
//        cardGrid.setBorder(new EmptyBorder(10, 0, 10, 0)); // Padding for the grid itself
//
//        // Existing features
//        cardGrid.add(createFeatureCard("Role-Based Dashboards", "<html>Access personalized dashboards tailored for Voters, Candidates, and Administrators.</html>", "role-based.png"));
//        cardGrid.add(createFeatureCard("Multi-Factor Authentication (MFA)", "<html>Enhance security with multiple layers of verification during login.</html>", "mfa.png"));
//        cardGrid.add(createFeatureCard("Comprehensive Logging", "<html>All critical actions and events are securely logged for audit and transparency.</html>", "log.png"));
//        cardGrid.add(createFeatureCard("Real-time Results", "<html>View election results as they are tallied in real-time.</html>", "results.png"));
//
//        // New or expanded features
//        cardGrid.add(createFeatureCard("Secure & Anonymous Voting", "<html>Your vote is encrypted and kept anonymous to ensure privacy and integrity.</html>", "secure.png"));
//        cardGrid.add(createFeatureCard("Candidate Profiles", "<html>Browse detailed profiles of candidates, including their manifestos and qualifications.</html>", "candidate.png"));
//        cardGrid.add(createFeatureCard("Voter Registration & Eligibility", "<html>Streamlined process for new voter registration and easy eligibility checks.</html>", "voter.png"));
//        cardGrid.add(createFeatureCard("Audit Trails", "<html>Transparent and unalterable records of all voting activities for verifiable elections.</html>", "audit.png"));
//
//        featuresPanel.add(cardGrid);
//        return featuresPanel;
//    }
//
//    private JPanel createFeatureCard(String title, String description, String iconFileName) {
//        JPanel card = new JPanel(new BorderLayout(10, 10)); // Layout with gaps
//        card.setBackground(Color.WHITE);
//        card.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(173, 216, 230), 1), // LightBlue
//            BorderFactory.createEmptyBorder(15, 15, 15, 15) // Inner padding
//        ));
//
//        JLabel iconLabel = new JLabel();
//        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        // Corrected path for icon
//        URL iconUrl = getClass().getResource("/images/" + iconFileName);
//        if (iconUrl != null) {
//            ImageIcon originalIcon = new ImageIcon(iconUrl);
//            Image image = originalIcon.getImage();
//            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Small icon size
//            iconLabel.setIcon(new ImageIcon(scaledImage));
//        } else {
//            iconLabel.setText("Icon");
//            iconLabel.setFont(new Font("Arial", Font.ITALIC, 10));
//        }
//        card.add(iconLabel, BorderLayout.WEST);
//
//        JPanel textPanel = new JPanel(new BorderLayout());
//        textPanel.setOpaque(false);
//
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        titleLabel.setForeground(new Color(25, 25, 112));
//        textPanel.add(titleLabel, BorderLayout.NORTH);
//
//        JLabel descLabel = new JLabel(description);
//        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//        descLabel.setForeground(new Color(70, 70, 70));
//        textPanel.add(descLabel, BorderLayout.CENTER);
//
//        card.add(textPanel, BorderLayout.CENTER);
//        return card;
//    }
//
//
//
//    private JPanel createSupportAndContactPanel() {
//        JPanel supportPanel = new JPanel();
//        supportPanel.setLayout(new BoxLayout(supportPanel, BoxLayout.Y_AXIS));
//        supportPanel.setBackground(new Color(240, 248, 255));
//        supportPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(255, 165, 0), 2), // Orange for contrast
//            "Support & Contact",
//            javax.swing.border.TitledBorder.CENTER,
//            javax.swing.border.TitledBorder.TOP,
//            new Font("Arial", Font.BOLD, 20),
//            new Color(25, 25, 112)
//        ));
//
//        supportPanel.add(Box.createRigidArea(new Dimension(0, 15)));
//
//        JLabel introLabel = new JLabel("<html>Need assistance? Our support team is here to help you.</html>", SwingConstants.CENTER);
//        introLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//        introLabel.setForeground(new Color(50, 50, 50));
//        introLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        supportPanel.add(introLabel);
//        supportPanel.add(Box.createRigidArea(new Dimension(0, 15)));
//
//        JPanel contactDetailsPanel = new JPanel(new GridLayout(3, 1, 0, 10)); // 3 rows, 1 column, 10px vertical gap
//        contactDetailsPanel.setOpaque(false);
//        contactDetailsPanel.setBorder(new EmptyBorder(0, 50, 0, 50)); // Inner padding
//
//        JLabel emailLabel = new JLabel("<html><b>Email:</b> support@votingportal.com</html>", SwingConstants.LEFT);
//        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//        emailLabel.setForeground(new Color(25, 25, 112));
//        contactDetailsPanel.add(emailLabel);
//
//        JLabel phoneLabel = new JLabel("<html><b>Phone:</b> +91 0413-123456 (Mon-Fri, 9 AM - 5 PM IST)</html>", SwingConstants.LEFT);
//        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//        phoneLabel.setForeground(new Color(25, 25, 112));
//        contactDetailsPanel.add(phoneLabel);
//
//        JLabel addressLabel = new JLabel("<html><b>Address:</b> Election Commission Office, 123 Main Street, Puducherry, India</html>", SwingConstants.LEFT);
//        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//        addressLabel.setForeground(new Color(25, 25, 112));
//        contactDetailsPanel.add(addressLabel);
//
//        supportPanel.add(contactDetailsPanel);
//        supportPanel.add(Box.createRigidArea(new Dimension(0, 15)));
//
//        return supportPanel;
//    }
//
//    private JPanel createFooter() {
//        JPanel footer = new JPanel();
//        footer.setBackground(new Color(70, 130, 180)); // SteelBlue, matching navbar
//        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
//        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//
//        JLabel copyrightLabel = new JLabel("© 2025 Voting Portal. All rights reserved.");
//        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//        copyrightLabel.setForeground(Color.WHITE);
//        footer.add(copyrightLabel);
//
//        return footer;
//    }
//}

// src/com/votingsystem/client/ui/LandingPagePanel.java
package com.votingsystem.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL; // For loading image from resources

public class LandingPagePanel extends JPanel {
    private OnlineVotingApp parentFrame;

    public LandingPagePanel(OnlineVotingApp parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout()); // Use BorderLayout for main structure
        setBackground(new Color(240, 248, 255)); // AliceBlue - A soft, pleasant background

        // --- 1. Navbar ---
        add(createNavbar(), BorderLayout.NORTH);

        // --- Main Content Area (Scrollable) ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Stack elements vertically
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding

        // Portal Title (moved from old landingPanel, now more prominent)
        JLabel mainTitleLabel = new JLabel("Welcome to the Secure Online Voting Portal", SwingConstants.CENTER);
        mainTitleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        mainTitleLabel.setForeground(new Color(25, 25, 112)); // MidnightBlue - Deep blue for prominence
        mainTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(mainTitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // Election Image
        contentPanel.add(createElectionImagePanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // Previous Results and Upcoming Election Details (Grid Layout)
        contentPanel.add(createElectionDetailsPanel()); // This is where the main changes will be
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // Key Features (Card-like Layout)
        contentPanel.add(createKeyFeaturesPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // Support and Contact Section
        contentPanel.add(createSupportAndContactPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // Make the content scrollable if it exceeds frame height
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        add(scrollPane, BorderLayout.CENTER);


        // --- 2. Footer ---
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel();
        navbar.setBackground(new Color(70, 130, 180)); // SteelBlue
        navbar.setLayout(new BorderLayout());
        navbar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Panel for Logo and Portal Name
        JPanel logoAndNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // 10px gap between logo and text
        logoAndNamePanel.setOpaque(false); // Make it transparent

        // Logo Label
        JLabel logoLabel = new JLabel();
        URL logoUrl = getClass().getResource("/images/logo.png"); // Path to your logo image
        if (logoUrl != null) {
            ImageIcon originalIcon = new ImageIcon(logoUrl);
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH); // Adjust size as needed
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            // Fallback text if logo not found
            logoLabel.setText("[Logo]");
            logoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            logoLabel.setForeground(Color.RED);
        }
        logoAndNamePanel.add(logoLabel);


        JLabel portalName = new JLabel("Voting Portal");
        portalName.setFont(new Font("Arial", Font.BOLD, 24));
        portalName.setForeground(Color.WHITE);
        logoAndNamePanel.add(portalName);

        navbar.add(logoAndNamePanel, BorderLayout.WEST); // Add the combined panel to the left


        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        navButtons.setOpaque(false); // Make it transparent to show navbar background

        JButton loginButton = new JButton("Login");
        styleNavButton(loginButton);
        loginButton.addActionListener(e -> parentFrame.showPanel("Login"));
        navButtons.add(loginButton);

        JButton registerButton = new JButton("Register");
        styleNavButton(registerButton);
        registerButton.addActionListener(e -> parentFrame.showPanel("Register"));
        navButtons.add(registerButton);

        navbar.add(navButtons, BorderLayout.EAST);
        return navbar;
    }

    private void styleNavButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(88, 145, 200)); // Slightly lighter blue for buttons
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createElectionImagePanel() {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 248, 255));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2)); // LightBlue border

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Load image from resources (ensure 'election.png' is in your classpath, e.g., in a 'resources' folder)
        URL imageUrl = getClass().getResource("/images/election_hero.png"); // Adjust path if needed
        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            // Scale image to fit within the panel (adjust dimensions as needed)
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(600, 300, Image.SCALE_SMOOTH); // Fixed size for demo
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setText("Election Image Not Found");
            imageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            imageLabel.setForeground(Color.RED);
        }
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        return imagePanel;
    }

    private JPanel createElectionDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(240, 248, 255)); // Match content panel background
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2), // SteelBlue for the main border
            "Election Information",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 22), // Slightly larger and bold title
            new Color(25, 25, 112) // MidnightBlue for the main title
        ));
        detailsPanel.setPreferredSize(new Dimension(800, 500)); // Give it a preferred size

        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 30, 0)); // Increased horizontal gap to 30px
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding around the grid cells

        // --- Previous Results Panel ---
        JPanel prevResultsPanel = new JPanel(new BorderLayout(10, 10)); // Added internal padding
        prevResultsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 1), // Softer grey border
            BorderFactory.createEmptyBorder(15, 15, 15, 15) // Inner padding
        ));
        prevResultsPanel.setBackground(new Color(250, 250, 250)); // Off-white for distinct card
        
        JLabel prevResultsTitle = new JLabel("PREVIOUS ELECTION RESULTS", SwingConstants.CENTER);
        prevResultsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Modern font
        prevResultsTitle.setForeground(new Color(50, 120, 180)); // A calming blue
        prevResultsPanel.add(prevResultsTitle, BorderLayout.NORTH);

        // Styling for previous results content
        JTextPane prevResultsTextPane = new JTextPane();
        prevResultsTextPane.setContentType("text/html");
        prevResultsTextPane.setText(
            "<html><body style='font-family: Arial; font-size: 14px; color: #333333; line-height: 1.6;'>" +
            "<b><span style='font-size: 16px; color: #191970;'>2024 National Elections:</span></b><br>" +
            "&bull; Total Seats: 543<br>" +
            "&bull; <span style='color: #2E8B57;'>Party A: 300 seats (55.2%)</span><br>" + // SeaGreen
            "&bull; <span style='color: #DAA520;'>Party B: 200 seats (36.8%)</span><br>" + // Goldenrod
            "&bull; Others: 43 seats (8.0%)<br><br>" +
            "<b><span style='font-size: 16px; color: #191970;'>2023 State Assembly Elections (Puducherry):</span></b><br>" +
            "&bull; Total Constituencies: 30<br>" +
            "&bull; <span style='color: #2E8B57;'>Party X: 18 seats (60%)</span><br>" +
            "&bull; <span style='color: #DAA520;'>Party Y: 10 seats (33.3%)</span><br>" +
            "&bull; Independents: 2 seats (6.7%)<br><br>" +
            "<b><span style='font-size: 16px; color: #191970;'>2022 Municipal Corporation Elections (Puducherry):</span></b><br>" +
            "&bull; Total Wards: 42<br>" +
            "&bull; Turnout: 70.5%<br>" +
            "&bull; Major Issues: Local Development, Sanitation" +
            "</body></html>"
        );
        prevResultsTextPane.setEditable(false);
        prevResultsTextPane.setBackground(prevResultsPanel.getBackground()); // Match background
        // Make text pane transparent for smooth scrolling
        prevResultsTextPane.setOpaque(false); // Important for background color to show through
        prevResultsTextPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE); // Use setFont
        prevResultsTextPane.setFont(new Font("Arial", Font.PLAIN, 14)); // This might be overridden by HTML

        JScrollPane prevScrollPane = new JScrollPane(prevResultsTextPane);
        prevScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        prevScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        prevScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        prevScrollPane.setOpaque(false);
        prevScrollPane.getViewport().setOpaque(false); // Make viewport transparent too

        prevResultsPanel.add(prevScrollPane, BorderLayout.CENTER);
        gridPanel.add(prevResultsPanel);

        // --- Upcoming Elections Panel ---
        JPanel upcomingPanel = new JPanel(new BorderLayout(10, 10)); // Added internal padding
        upcomingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 1), // Softer grey border
            BorderFactory.createEmptyBorder(15, 15, 15, 15) // Inner padding
        ));
        upcomingPanel.setBackground(new Color(250, 250, 250)); // Off-white for distinct card

        JLabel upcomingTitle = new JLabel("UPCOMING ELECTIONS", SwingConstants.CENTER);
        upcomingTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Modern font
        upcomingTitle.setForeground(new Color(50, 120, 180)); // A calming blue
        upcomingPanel.add(upcomingTitle, BorderLayout.NORTH);

        // Styling for upcoming details content
        JTextPane upcomingTextPane = new JTextPane();
        upcomingTextPane.setContentType("text/html");
        upcomingTextPane.setText(
            "<html><body style='font-family: Arial; font-size: 14px; color: #333333; line-height: 1.6;'>" +
            "<b><span style='font-size: 16px; color: #4682B4;'>National Elections 2029:</span></b><br>" + // SteelBlue
            "&bull; Dates: October 1 - October 15, 2029<br>" +
            "&bull; Registration Deadline: September 1, 2029<br>" +
            "&bull; Key Candidates: <span style='font-style: italic; color: #6A5ACD;'>To be announced</span><br>" + // SlateBlue italic
            "&bull; Important Issues: Economic Growth, Healthcare, Education<br><br>" +
            "<b><span style='font-size: 16px; color: #4682B4;'>State By-Elections (Puducherry - Oulgaret Constituency):</span></b><br>" +
            "&bull; Dates: November 5 - November 10, 2025<br>" +
            "&bull; Registration Deadline: October 15, 2025<br>" +
            "&bull; Polling Booths: Local community centers<br>" +
            "&bull; Eligibility: Registered voters in Oulgaret Constituency<br><br>" +
            "<b><span style='font-size: 16px; color: #4682B4;'>Panchayat Elections (Local Areas):</span></b><br>" +
            "&bull; Dates: March 1 - March 15, 2026<br>" +
            "&bull; Purpose: Local Governance, Infrastructure Development" +
            "</body></html>"
        );
        upcomingTextPane.setEditable(false);
        upcomingTextPane.setBackground(upcomingPanel.getBackground()); // Match background
        upcomingTextPane.setOpaque(false); // Important for background color to show through
        upcomingTextPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        upcomingTextPane.setFont(new Font("Arial", Font.PLAIN, 14)); // This might be overridden by HTML

        JScrollPane upcomingScrollPane = new JScrollPane(upcomingTextPane);
        upcomingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        upcomingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        upcomingScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        upcomingScrollPane.setOpaque(false);
        upcomingScrollPane.getViewport().setOpaque(false); // Make viewport transparent too

        upcomingPanel.add(upcomingScrollPane, BorderLayout.CENTER);
        gridPanel.add(upcomingPanel);

        detailsPanel.add(gridPanel, BorderLayout.CENTER);
        return detailsPanel;
    }


    private JPanel createKeyFeaturesPanel() {
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS)); // Stack title and grid
        featuresPanel.setBackground(new Color(240, 248, 255));
        featuresPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 179, 113), 2), // MediumSeaGreen
            "Key Features",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 20),
            new Color(25, 25, 112)
        ));

        // Changed to 4 rows for 8 cards. Adjust if you add more/fewer.
        JPanel cardGrid = new JPanel(new GridLayout(4, 2, 20, 20));
        cardGrid.setOpaque(false);
        cardGrid.setBorder(new EmptyBorder(10, 0, 10, 0)); // Padding for the grid itself

        // Existing features
        cardGrid.add(createFeatureCard("Role-Based Dashboards", "<html>Access personalized dashboards tailored for Voters, Candidates, and Administrators.</html>", "role-based.png"));
        cardGrid.add(createFeatureCard("Multi-Factor Authentication (MFA)", "<html>Enhance security with multiple layers of verification during login.</html>", "mfa.png"));
        cardGrid.add(createFeatureCard("Comprehensive Logging", "<html>All critical actions and events are securely logged for audit and transparency.</html>", "log.png"));
        cardGrid.add(createFeatureCard("Real-time Results", "<html>View election results as they are tallied in real-time.</html>", "results.png"));

        // New or expanded features
        cardGrid.add(createFeatureCard("Secure & Anonymous Voting", "<html>Your vote is encrypted and kept anonymous to ensure privacy and integrity.</html>", "secure.png"));
        cardGrid.add(createFeatureCard("Candidate Profiles", "<html>Browse detailed profiles of candidates, including their manifestos and qualifications.</html>", "candidate.png"));
        cardGrid.add(createFeatureCard("Voter Registration & Eligibility", "<html>Streamlined process for new voter registration and easy eligibility checks.</html>", "voter.png"));
        cardGrid.add(createFeatureCard("Audit Trails", "<html>Transparent and unalterable records of all voting activities for verifiable elections.</html>", "audit.png"));

        featuresPanel.add(cardGrid);
        return featuresPanel;
    }

    private JPanel createFeatureCard(String title, String description, String iconFileName) {
        JPanel card = new JPanel(new BorderLayout(10, 10)); // Layout with gaps
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 1), // LightBlue
            BorderFactory.createEmptyBorder(15, 15, 15, 15) // Inner padding
        ));

        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Corrected path for icon
        URL iconUrl = getClass().getResource("/images/" + iconFileName);
        if (iconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(iconUrl);
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Small icon size
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            iconLabel.setText("Icon");
            iconLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        }
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(25, 25, 112));
        textPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(70, 70, 70));
        textPanel.add(descLabel, BorderLayout.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }



    private JPanel createSupportAndContactPanel() {
        JPanel supportPanel = new JPanel();
        supportPanel.setLayout(new BoxLayout(supportPanel, BoxLayout.Y_AXIS));
        supportPanel.setBackground(new Color(240, 248, 255));
        supportPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 2), // Orange for contrast
            "Support & Contact",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 20),
            new Color(25, 25, 112)
        ));

        supportPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel introLabel = new JLabel("<html>Need assistance? Our support team is here to help you.</html>", SwingConstants.CENTER);
        introLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        introLabel.setForeground(new Color(50, 50, 50));
        introLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        supportPanel.add(introLabel);
        supportPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel contactDetailsPanel = new JPanel(new GridLayout(3, 1, 0, 10)); // 3 rows, 1 column, 10px vertical gap
        contactDetailsPanel.setOpaque(false);
        contactDetailsPanel.setBorder(new EmptyBorder(0, 50, 0, 50)); // Inner padding

        JLabel emailLabel = new JLabel("<html><b>Email:</b> support@votingportal.com</html>", SwingConstants.LEFT);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(25, 25, 112));
        contactDetailsPanel.add(emailLabel);

        JLabel phoneLabel = new JLabel("<html><b>Phone:</b> +91 0413-123456 (Mon-Fri, 9 AM - 5 PM IST)</html>", SwingConstants.LEFT);
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneLabel.setForeground(new Color(25, 25, 112));
        contactDetailsPanel.add(phoneLabel);

        JLabel addressLabel = new JLabel("<html><b>Address:</b> Election Commission Office, 123 Main Street, Puducherry, India</html>", SwingConstants.LEFT);
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addressLabel.setForeground(new Color(25, 25, 112));
        contactDetailsPanel.add(addressLabel);

        supportPanel.add(contactDetailsPanel);
        supportPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        return supportPanel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(70, 130, 180)); // SteelBlue, matching navbar
        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel copyrightLabel = new JLabel("© 2025 Voting Portal. All rights reserved.");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightLabel.setForeground(Color.WHITE);
        footer.add(copyrightLabel);

        return footer;
    }
}