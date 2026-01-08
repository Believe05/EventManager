package za.ac.service;

import com.google.firebase.database.*;
import za.ac.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AuthService {
    
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private static String hashPassword(String password) {
        try {
            if (password == null || password.isEmpty()) {
                return null;
            }
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }

    public static boolean registerUser(User user) {
        try {
            // 🔥 CRITICAL: Ensure Firebase is initialized
            if (!FirebaseConfig.isInitialized()) {
                System.out.println("Firebase not initialized. Initializing now...");
                FirebaseConfig.init();
            }
            
            System.out.println("Starting registration for: " + user.getEmail());
            System.out.println("Firebase initialized: " + FirebaseConfig.isInitialized());
            
            // 1. Create new user ID
            String userId = dbRef.child("users").push().getKey();
            System.out.println("Generated user ID: " + userId);
            
            // 2. Hash the password
            String hashedPassword = hashPassword(user.getPassword());
            if (hashedPassword == null) {
                System.err.println("Failed to hash password");
                return false;
            }
            
            // 3. Create user data map
            Map<String, Object> userValues = new HashMap<>();
            userValues.put("fullName", user.getFullName());
            userValues.put("email", user.getEmail());
            userValues.put("password", hashedPassword);
            userValues.put("role", user.getRole());
            userValues.put("createdAt", System.currentTimeMillis());
            
            System.out.println("Saving user to database at path: /users/" + userId);
            
            // 4. Save to database with detailed error handling
            dbRef.child("users").child(userId).setValue(userValues, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error != null) {
                        System.err.println("❌ Database error code: " + error.getCode());
                        System.err.println("❌ Database error message: " + error.getMessage());
                        System.err.println("❌ Database error details: " + error.getDetails());
                    } else {
                        System.out.println("✅ User saved to database with ID: " + userId);
                        System.out.println("📁 Path: " + ref.getPath());
                    }
                }
            });
            
            // Small delay to ensure write completes
            Thread.sleep(1000);
            System.out.println("✅ Registration method completed successfully");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Exception in registerUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ CORRECTED LOGIN METHOD (only one method!)
    public static User login(String email, String password) {
        try {
            // Ensure Firebase is initialized
            if (!FirebaseConfig.isInitialized()) {
                System.out.println("Firebase not initialized. Initializing now...");
                FirebaseConfig.init();
            }
            
            System.out.println("Attempting login for: " + email);
            
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                System.err.println("Failed to hash password");
                return null;
            }
            
            System.out.println("Searching for user in database...");
            
            // Create a CompletableFuture to handle async database query
            CompletableFuture<User> future = new CompletableFuture<>();
            
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            
            // Query for user with matching email
            usersRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                // Get user data
                                String dbPassword = userSnapshot.child("password").getValue(String.class);
                                String fullName = userSnapshot.child("fullName").getValue(String.class);
                                String dbEmail = userSnapshot.child("email").getValue(String.class);
                                String role = userSnapshot.child("role").getValue(String.class);
                                
                                // Check password
                                if (dbPassword != null && dbPassword.equals(hashedPassword)) {
                                    User user = new User();
                                    user.setId(userSnapshot.getKey());
                                    user.setFullName(fullName);
                                    user.setEmail(dbEmail);
                                    user.setRole(role);
                                    user.setPassword(dbPassword); // Store hashed password
                                    
                                    System.out.println("✅ Login successful for: " + email);
                                    future.complete(user);
                                    return;
                                } else {
                                    System.out.println("❌ Password mismatch for: " + email);
                                }
                            }
                        }
                        System.out.println("❌ No user found with email: " + email);
                        future.complete(null);
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("❌ Database error during login: " + databaseError.getMessage());
                        future.complete(null);
                    }
                });
            
            // Wait for the async operation to complete (with timeout)
            try {
                return future.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.err.println("Login timeout or error: " + e.getMessage());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Exception in login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

