package za.ac.ui;

import za.ac.model.Event;
import za.ac.model.User;
import za.ac.service.EventService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventForm extends JFrame {

    private JTable tblEvents;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnView, btnBack;
    private User loggedInUser;

    public EventForm(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        setTitle("Event Management - " + loggedInUser.getFullName());
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadEvents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table for events
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Description", "Location", "Date"}, 0);
        tblEvents = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblEvents);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        btnAdd = new JButton("Add Event");
        btnEdit = new JButton("Edit Event");
        btnDelete = new JButton("Delete Event");
        btnView = new JButton("View Events");
        btnBack = new JButton("Back to Dashboard");
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnView);
        btnPanel.add(btnBack);

        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel);

        // Button actions
        btnAdd.addActionListener(e -> addEvent());
        btnEdit.addActionListener(e -> editEvent());
        btnDelete.addActionListener(e -> deleteEvent());
        btnView.addActionListener(e -> {
            // ✅ New: View events in read-only mode
            new EventListView(loggedInUser).setVisible(true);
            dispose();
        });
        btnBack.addActionListener(e -> {
            if (loggedInUser != null && "ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
                new DashboardAdmin(loggedInUser).setVisible(true);
            } else {
                new DashboardUser(loggedInUser != null ? loggedInUser : new User()).setVisible(true);
            }
            dispose();
        });
    }

    private void loadEvents() {
        try {
            java.util.List<Event> events = EventService.getAllEvents();
            tableModel.setRowCount(0); // clear table
            
            for (Event event : events) {
                tableModel.addRow(new Object[]{
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getLocation(),
                    event.getDate()
                });
            }
            
            System.out.println("Loaded " + events.size() + " events");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addEvent() {
        // Same as before...
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField dateField = new JTextField();

        Object[] fields = {
                "Title:", titleField,
                "Description:", descField,
                "Location:", locationField,
                "Date (yyyy-MM-dd):", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Event", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String description = descField.getText().trim();
                String location = locationField.getText().trim();
                String date = dateField.getText().trim();
                
                if (title.isEmpty() || date.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and Date are required!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Event event = new Event(title, description, location, date);
                boolean success = EventService.createEvent(event);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event added successfully!");
                    loadEvents();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add event");
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding event: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void editEvent() {
        // Same as before...
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String eventId = (String) tableModel.getValueAt(selectedRow, 0);
        JTextField titleField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField descField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField locationField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JTextField dateField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));

        Object[] fields = {
                "Title:", titleField,
                "Description:", descField,
                "Location:", locationField,
                "Date (yyyy-MM-dd):", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Event", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String description = descField.getText().trim();
                String location = locationField.getText().trim();
                String date = dateField.getText().trim();
                
                if (title.isEmpty() || date.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and Date are required!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Event updatedEvent = new Event(title, description, location, date);
                boolean success = EventService.updateEvent(eventId, updatedEvent);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event updated successfully!");
                    loadEvents();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update event");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating event: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void deleteEvent() {
        // Same as before...
        int selectedRow = tblEvents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String eventId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = EventService.deleteEvent(eventId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event deleted successfully!");
                    loadEvents();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete event");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting event: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Initialize Firebase
        try {
            za.ac.service.FirebaseConfig.init();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create a dummy admin for testing
        User testAdmin = new User();
        testAdmin.setFullName("Admin User");
        testAdmin.setRole("ADMIN");
        
        SwingUtilities.invokeLater(() -> new EventForm(testAdmin).setVisible(true));
    }
}