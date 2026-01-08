package za.ac.ui;

import za.ac.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardUser extends JFrame {

    private User user;
    private JLabel lblWelcome;
    private JButton btnViewEvents, btnLogout;

    public DashboardUser(User user) {
        this.user = user;
        setTitle("User Dashboard");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome message
        lblWelcome = new JLabel("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblWelcome, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        btnViewEvents = new JButton("View Upcoming Events");
        btnLogout = new JButton("Logout");
        
        buttonPanel.add(btnViewEvents);
        buttonPanel.add(btnLogout);
        
        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);

        // View Events action
        btnViewEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ✅ Open EventListView for regular users
                new EventListView(user).setVisible(true);
                dispose();
            }
        });

        // Logout action
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        // Test with a dummy user
        User testUser = new User();
        testUser.setFullName("Bongiwe Magagule");
        testUser.setRole("STUDENT");

        SwingUtilities.invokeLater(() -> new DashboardUser(testUser).setVisible(true));
    }
}