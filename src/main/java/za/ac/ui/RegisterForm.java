package za.ac.ui;

import za.ac.model.User;
import za.ac.service.AuthService;
import za.ac.service.FirebaseConfig;
import com.google.firebase.database.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterForm extends JFrame {

    private JTextField txtFullName, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;
    private JButton btnRegister, btnBack;

    public RegisterForm() {
        setTitle("Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10)); // Changed to 6 rows for test button
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Full Name:"));
        txtFullName = new JTextField();
        panel.add(txtFullName);

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        panel.add(new JLabel("Role:"));
        comboRole = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});
        panel.add(comboRole);

        btnRegister = new JButton("Register");
        btnBack = new JButton("Back to Login");
        JButton btnTestDB = new JButton("Test DB Connection"); // Added test button
        
        panel.add(btnRegister);
        panel.add(btnBack);
        panel.add(btnTestDB);
        panel.add(new JLabel()); // Empty cell

        add(panel);

        // Test DB Connection button
        btnTestDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testDatabaseConnection();
            }
        });

        // Register button action
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = txtFullName.getText().trim();
                String email = txtEmail.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String role = (String) comboRole.getSelectedItem();

                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterForm.this, 
                        "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Email validation
                if (!email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(RegisterForm.this,
                        "Please enter a valid email address!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Password validation
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(RegisterForm.this,
                        "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create User object
                User user = new User();
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPassword(password);
                user.setRole(role);

                System.out.println("=== REGISTRATION ATTEMPT ===");
                System.out.println("Name: " + fullName);
                System.out.println("Email: " + email);
                System.out.println("Role: " + role);

                try {
                    boolean success = AuthService.registerUser(user);
                    System.out.println("Registration result: " + success);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(RegisterForm.this, 
                            "✅ Registration successful!\nYou can now login with your credentials.");
                        new LoginForm().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterForm.this, 
                            "❌ Registration failed!\nCheck console for details.", 
                            "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    System.err.println("Exception during registration:");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterForm.this,
                        "Registration error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Back button action
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }
    
    private void testDatabaseConnection() {
        try {
            System.out.println("=== TESTING DATABASE CONNECTION ===");
            
            // Test write
            DatabaseReference testRef = FirebaseDatabase.getInstance().getReference("connectionTest");
            testRef.setValue("Test from NetBeans at " + System.currentTimeMillis(), 
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if (error == null) {
                            System.out.println("✅ Database write test PASSED");
                            JOptionPane.showMessageDialog(RegisterForm.this,
                                "✅ Database connection successful!\nData written to: " + ref.getPath(),
                                "Connection Test", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            System.out.println("❌ Database write test FAILED: " + error.getMessage());
                            JOptionPane.showMessageDialog(RegisterForm.this,
                                "❌ Database connection failed:\n" + error.getMessage(),
                                "Connection Test", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            
            Thread.sleep(1000); // Wait for write
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(RegisterForm.this,
                "Test error: " + e.getMessage(),
                "Connection Test", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Initialize Firebase BEFORE showing the form
        try {
            System.out.println("=== STARTING APPLICATION ===");
            System.out.println("Initializing Firebase...");
            FirebaseConfig.init();
            System.out.println("Firebase initialized successfully!");
        } catch (Exception e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null,
                "Cannot connect to database.\nError: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            RegisterForm form = new RegisterForm();
            form.setVisible(true);
            System.out.println("Register form displayed");
        });
    }
}