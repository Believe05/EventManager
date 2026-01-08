package za.ac.ui;

import za.ac.model.User;
import za.ac.service.AuthService;
import za.ac.service.FirebaseConfig;
import com.google.firebase.database.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister, btnTestDB;

    public LoginForm() {
        setTitle("Login - Event Management System");
        setSize(450, 300); // Increased size for test button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10)); // Added row for test button
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");
        btnTestDB = new JButton("Test DB"); // Test button
        
        // Set colors for better UX
        btnLogin.setBackground(new Color(76, 175, 80));
        btnLogin.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(33, 150, 243));
        btnRegister.setForeground(Color.WHITE);
        btnTestDB.setBackground(new Color(255, 152, 0));
        btnTestDB.setForeground(Color.WHITE);

        panel.add(btnLogin);
        panel.add(btnRegister);
        panel.add(btnTestDB);
        panel.add(new JLabel()); // Empty cell for layout

        add(panel);

        // Test DB Connection button
        btnTestDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testDatabaseConnection();
            }
        });

        // Login button action
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                        "Please enter email and password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Email validation
                if (!email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                        "Please enter a valid email address!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                System.out.println("=== LOGIN ATTEMPT ===");
                System.out.println("Email: " + email);
                
                // Show loading dialog
                JDialog loadingDialog = new JDialog(LoginForm.this, "Authenticating...", true);
                loadingDialog.setSize(200, 100);
                loadingDialog.setLocationRelativeTo(LoginForm.this);
                loadingDialog.add(new JLabel("Checking credentials...", SwingConstants.CENTER));
                loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                
                // Run login in background thread
                new Thread(() -> {
                    try {
                        User user = AuthService.login(email, password);
                        
                        SwingUtilities.invokeLater(() -> {
                            loadingDialog.dispose();
                            
                            if (user != null) {
                                System.out.println("✅ Login successful!");
                                System.out.println("User: " + user.getFullName());
                                System.out.println("Role: " + user.getRole());
                                
                                JOptionPane.showMessageDialog(LoginForm.this, 
                                    "Welcome, " + user.getFullName() + "!", 
                                    "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                                
                                // Navigate based on role
                                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                                    new DashboardAdmin(user).setVisible(true);
                                } else {
                                    new DashboardUser(user).setVisible(true);
                                }
                                dispose();
                            } else {
                                System.out.println("❌ Login failed");
                                JOptionPane.showMessageDialog(LoginForm.this, 
                                    "Invalid email or password!\n\nPlease check:", 
                                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            loadingDialog.dispose();
                            System.err.println("Login error: " + ex.getMessage());
                            JOptionPane.showMessageDialog(LoginForm.this,
                                "Login error: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }).start();
                
                loadingDialog.setVisible(true);
            }
        });

        // Register button action
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterForm().setVisible(true);
                dispose();
            }
        });
    }
    
    private void testDatabaseConnection() {
        try {
            System.out.println("=== TESTING DATABASE CONNECTION ===");
            
            // Test read from users
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long userCount = dataSnapshot.getChildrenCount();
                    System.out.println("✅ Database connected!");
                    System.out.println("📊 Total users in database: " + userCount);
                    
                    // Show user list
                    if (userCount > 0) {
                        StringBuilder userList = new StringBuilder("Registered users:\n");
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            String email = user.child("email").getValue(String.class);
                            String name = user.child("fullName").getValue(String.class);
                            userList.append("• ").append(name).append(" (").append(email).append(")\n");
                        }
                        System.out.println(userList.toString());
                    }
                    
                    JOptionPane.showMessageDialog(LoginForm.this,
                        "✅ Database connection successful!\n\n" +
                        "Total registered users: " + userCount,
                        "Connection Test", JOptionPane.INFORMATION_MESSAGE);
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("❌ Database error: " + databaseError.getMessage());
                    JOptionPane.showMessageDialog(LoginForm.this,
                        "❌ Database error:\n" + databaseError.getMessage(),
                        "Connection Test", JOptionPane.ERROR_MESSAGE);
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(LoginForm.this,
                "Test error: " + e.getMessage(),
                "Connection Test", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize Firebase BEFORE showing the form
        try {
            System.out.println("=== EVENT MANAGEMENT SYSTEM ===");
            System.out.println("Initializing Firebase...");
            FirebaseConfig.init();
            System.out.println("✅ Firebase initialized successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
            
            int choice = JOptionPane.showConfirmDialog(null,
                "Cannot connect to database.\n\nError: " + e.getMessage() + "\n\n" +
                "Would you like to continue in offline mode?",
                "Database Connection Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                System.exit(1);
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginForm form = new LoginForm();
            form.setVisible(true);
            System.out.println("Login form displayed");
        });
    }
}