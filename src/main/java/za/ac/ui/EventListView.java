package za.ac.ui;

import za.ac.model.User;
import com.google.firebase.database.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventListView extends JFrame {

    private JTable tblEvents;
    private DefaultTableModel tableModel;
    private JButton btnBack;
    private User loggedInUser; // Store the logged-in user
    
    // Realtime Database reference
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public EventListView() {
        setTitle("Upcoming Events");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadEvents();
    }

    // ✅ FIXED: Constructor that accepts User
    public EventListView(User loggedInUser) {
        this(); // Call default constructor
        this.loggedInUser = loggedInUser; // Store the logged-in user
        setTitle("Upcoming Events - " + loggedInUser.getFullName());
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table
        tableModel = new DefaultTableModel(new String[]{"Title", "Description", "Location", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        };
        tblEvents = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblEvents);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        btnBack = new JButton("Back to Dashboard");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnBack);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedInUser != null) {
                    if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
                        new DashboardAdmin(loggedInUser).setVisible(true);
                    } else {
                        new DashboardUser(loggedInUser).setVisible(true);
                    }
                } else {
                    // Fallback if no user is passed
                    new LoginForm().setVisible(true);
                }
                dispose();
            }
        });
    }

    private void loadEvents() {
        try {
            dbRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tableModel.setRowCount(0); // clear table
                    
                    if (!dataSnapshot.exists()) {
                        System.out.println("No events found in database");
                        return;
                    }
                    
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        String title = eventSnapshot.child("title").getValue(String.class);
                        String description = eventSnapshot.child("description").getValue(String.class);
                        String location = eventSnapshot.child("location").getValue(String.class);
                        String date = eventSnapshot.child("date").getValue(String.class);
                        
                        // Add to table
                        tableModel.addRow(new Object[]{
                                title != null ? title : "",
                                description != null ? description : "",
                                location != null ? location : "",
                                date != null ? date : ""
                        });
                    }
                    
                    System.out.println("Loaded " + tableModel.getRowCount() + " events");
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    JOptionPane.showMessageDialog(EventListView.this, 
                        "Error loading events: " + databaseError.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Database error: " + databaseError.getMessage());
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Initialize Firebase first
        try {
            za.ac.service.FirebaseConfig.init();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create test user
        User testUser = new User();
        testUser.setFullName("Test User");
        testUser.setRole("STUDENT");
        
        SwingUtilities.invokeLater(() -> new EventListView(testUser).setVisible(true));
    }
}