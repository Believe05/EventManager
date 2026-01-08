package za.ac.model;

public class User {
     private String id; 
    private String fullName;
    private String email;
    private String password;
    private String role;

    // Default constructor (required by Firebase)
    public User() {
    }

    // Constructor with fields
    public User(String fullName, String email, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String fullName, String email, String password) {
    this.fullName = fullName;
    this.email = email;
    this.password = password;
    this.role = "STUDENT"; // Default role
}


    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
