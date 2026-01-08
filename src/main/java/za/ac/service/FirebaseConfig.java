package za.ac.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import java.io.InputStream;
import java.io.FileInputStream;

public class FirebaseConfig {
    private static boolean initialized = false;
    
    public static void init() throws Exception {
        if (initialized) return;
        
        try {
            System.out.println("Looking for serviceAccountKey.json...");
            
            InputStream serviceAccount = FirebaseConfig.class
                .getClassLoader()
                .getResourceAsStream("serviceAccountKey.json");
            
            if (serviceAccount == null) {
                String filePath = "C:/Users/belie/Documents/NetBeansProjects/EventManager/src/main/resources/serviceAccountKey.json";
                System.out.println("Trying file path: " + filePath);
                serviceAccount = new FileInputStream(filePath);
            }
            
            System.out.println("Found credentials file, initializing Firebase...");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            
            // ✅ CORRECTED DATABASE URL WITH REGION
            String databaseUrl = "https://eventmanagementsystem-b99fe-default-rtdb.europe-west1.firebasedatabase.app/";
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setDatabaseUrl(databaseUrl)
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            
            initialized = true;
            System.out.println("✅ Firebase Realtime Database initialized successfully!");
            System.out.println("📊 Database URL: " + databaseUrl);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: Could not initialize Firebase: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public static FirebaseDatabase getDatabase() {
        if (!initialized) {
            throw new IllegalStateException("Firebase not initialized. Call FirebaseConfig.init() first.");
        }
        return FirebaseDatabase.getInstance();
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}