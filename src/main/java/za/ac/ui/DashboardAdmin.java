package za.ac.ui;

import za.ac.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardAdmin extends JFrame {

    private User adminUser;
    private JLabel lblWelcome;
    private JButton btnViewUsers, btnManageEvents, btnLogout;

    public DashboardAdmin(User adminUser) {
        this.adminUser = adminUser;
        setTitle("Admin Dashboard");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome label
        lblWelcome = new JLabel("Welcome, " + adminUser.getFullName() + " (ADMIN)");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblWelcome, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1, 10, 10));

        btnViewUsers = new JButton("View All Users");
        btnManageEvents = new JButton("Manage Events");
        btnLogout = new JButton("Logout");

        centerPanel.add(btnViewUsers);
        centerPanel.add(btnManageEvents);
        centerPanel.add(btnLogout);

        panel.add(centerPanel, BorderLayout.CENTER);

        add(panel);

        // Button actions
        btnViewUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Open User Management form
                JOptionPane.showMessageDialog(DashboardAdmin.this, 
                    "User Management feature coming soon!", 
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnManageEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ✅ FIXED: Open EventForm for admin
                new EventForm(adminUser).setVisible(true);
                dispose();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        // Test with dummy admin
        User admin = new User();
        admin.setFullName("Bongiwe Magagule");
        admin.setRole("ADMIN");

        SwingUtilities.invokeLater(() -> new DashboardAdmin(admin).setVisible(true));
    }
}