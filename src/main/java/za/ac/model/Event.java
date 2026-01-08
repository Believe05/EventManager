package za.ac.model;

import java.util.Date;




public class Event {
  private String id;
    private String title;
    private String description;
    private String date;
    private String location;

    // Default constructor (required by Firebase)
    public Event(String title1, String description1, String location1, Date date1) {
    }

    // Constructor with all fields
    public Event(String title, String description,String location, String date) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
    }

    // Constructor with fewer fields (optional)
    public Event(String title, String date) {
        this(title, "", date, ""); // default description and location as empty
    }

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
     
    
    
    
    
    
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
