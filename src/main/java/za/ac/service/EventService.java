package za.ac.service;

import com.google.firebase.database.*;
import za.ac.model.Event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EventService {
    
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    // 1. CREATE EVENT
    public static boolean createEvent(Event event) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        try {
            String eventId = dbRef.child("events").push().getKey();
            Map<String, Object> eventValues = new HashMap<>();
            
            eventValues.put("title", event.getTitle());
            eventValues.put("description", event.getDescription());
            eventValues.put("location", event.getLocation());
            eventValues.put("date", event.getDate());
            
            dbRef.child("events").child(eventId).setValue(eventValues, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error != null) {
                        System.err.println("Error creating event: " + error.getMessage());
                        future.complete(false);
                    } else {
                        System.out.println("Event created successfully with ID: " + eventId);
                        event.setId(eventId);
                        future.complete(true);
                    }
                }
            });
            
            return future.get();
            
        } catch (Exception e) {
            System.err.println("Exception in createEvent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 2. UPDATE EVENT
    public static boolean updateEvent(String eventId, Event event) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        try {
            Map<String, Object> eventValues = new HashMap<>();
            eventValues.put("title", event.getTitle());
            eventValues.put("description", event.getDescription());
            eventValues.put("location", event.getLocation());
            eventValues.put("date", event.getDate());
            
            dbRef.child("events").child(eventId).updateChildren(eventValues, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error != null) {
                        System.err.println("Error updating event: " + error.getMessage());
                        future.complete(false);
                    } else {
                        System.out.println("Event updated successfully: " + eventId);
                        future.complete(true);
                    }
                }
            });
            
            return future.get();
            
        } catch (Exception e) {
            System.err.println("Exception in updateEvent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 3. DELETE EVENT
    public static boolean deleteEvent(String eventId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        try {
            dbRef.child("events").child(eventId).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error != null) {
                        System.err.println("Error deleting event: " + error.getMessage());
                        future.complete(false);
                    } else {
                        System.out.println("Event deleted successfully: " + eventId);
                        future.complete(true);
                    }
                }
            });
            
            return future.get();
            
        } catch (Exception e) {
            System.err.println("Exception in deleteEvent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 4. GET ALL EVENTS
    public static List<Event> getAllEvents() {
        CompletableFuture<List<Event>> future = new CompletableFuture<>();
        
        try {
            dbRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Event> events = new ArrayList<>();
                    
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            Event event = new Event(
                                eventSnapshot.child("title").getValue(String.class),
                                eventSnapshot.child("description").getValue(String.class),
                                eventSnapshot.child("location").getValue(String.class),
                                eventSnapshot.child("date").getValue(String.class)
                            );
                            event.setId(eventSnapshot.getKey());
                            events.add(event);
                        }
                    }
                    
                    future.complete(events);
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("Error getting events: " + databaseError.getMessage());
                    future.complete(new ArrayList<>());
                }
            });
            
            return future.get();
            
        } catch (Exception e) {
            System.err.println("Exception in getAllEvents: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // 5. GET EVENT BY ID
    public static Event getEventById(String eventId) {
        CompletableFuture<Event> future = new CompletableFuture<>();
        
        try {
            dbRef.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Event event = new Event(
                            dataSnapshot.child("title").getValue(String.class),
                            dataSnapshot.child("description").getValue(String.class),
                            dataSnapshot.child("location").getValue(String.class),
                            dataSnapshot.child("date").getValue(String.class)
                        );
                        event.setId(dataSnapshot.getKey());
                        future.complete(event);
                    } else {
                        future.complete(null);
                    }
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("Error getting event: " + databaseError.getMessage());
                    future.complete(null);
                }
            });
            
            return future.get();
            
        } catch (Exception e) {
            System.err.println("Exception in getEventById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // 6. SEARCH EVENTS BY TITLE
    public static List<Event> searchEventsByTitle(String searchText) {
        CompletableFuture<List<Event>> future = new CompletableFuture<>();
        
        try {
            dbRef.child("events").orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Event> events = new ArrayList<>();
                        
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                Event event = new Event(
                                    eventSnapshot.child("title").getValue(String.class),
                                    eventSnapshot.child("description").getValue(String.class),
                                    eventSnapshot.child("location").getValue(String.class),
                                    eventSnapshot.child("date").getValue(String.class)
                                );
                                event.setId(eventSnapshot.getKey());
                                events.add(event);
                            }
                        }
                        
                        future.complete(events);
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("Error searching events: " + databaseError.getMessage());
                        future.complete(new ArrayList<>());
                    }
                });
            
            return future.get();
            
        } catch (Exception e) {
            System.err.println("Exception in searchEventsByTitle: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}