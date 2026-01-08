package za.ac.app;

import za.ac.service.FirebaseConfig; // Use your FirebaseConfig
import za.ac.ui.LoginForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Use your existing FirebaseConfig class
            FirebaseConfig.init();
            System.out.println("✅ Firebase initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null,
                "Cannot connect to database.\nError: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Launch login form
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}