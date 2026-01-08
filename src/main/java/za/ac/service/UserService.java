package za.ac.service;

/*import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import za.ac.model.User;


import java.security.MessageDigest;
import java.util.List;

public class UserService {

    private final Firestore db;

    public UserService() {
        this.db = FirebaseConfig.getFirestore();
    }

    // Hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Register a new user
    public boolean registerUser(User user) {
        try {
            db.collection("users").document().set(new User(
                    user.getFullName(),
                    user.getEmail(),
                    hashPassword(user.getPassword()),
                     user.getRole()
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login verification
    public boolean login(String email, String password) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection("users")
                    .whereEqualTo("email", email)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.isEmpty()) {
                return false; // No user with that email
            }

            String hashedInput = hashPassword(password);

            for (DocumentSnapshot doc : documents) {
                String storedHash = doc.getString("password");
                if (storedHash != null && storedHash.equals(hashedInput)) {
                    return true; // Password matches
                }
            }

            return false; // Password did not match
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}*/







