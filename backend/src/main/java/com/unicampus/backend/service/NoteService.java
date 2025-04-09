// backend/src/main/java/com/unicampus/backend/service/NoteService.java
package com.unicampus.backend.service; // Service package

import com.unicampus.backend.model.Note; // Import the Note model
import org.springframework.stereotype.Service; // Mark this as a Spring Service bean

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service // Tells Spring to manage this class as a singleton bean
public class NoteService {

    // In-memory list to store notes (TEMPORARY - Replace with Firebase later)
    private final List<Note> notes = new ArrayList<>();

    // Constructor to add some initial mock data (TEMPORARY)
    public NoteService() {
        notes.add(new Note(UUID.randomUUID().toString(), "Chapter 1: Introduction", "CS101", "Prof. Smith", "/downloads/cs101_ch1.pdf"));
        notes.add(new Note(UUID.randomUUID().toString(), "Lecture 5 Slides", "MA201", "Prof. Jones", "/downloads/ma201_lec5.ppt"));
        notes.add(new Note(UUID.randomUUID().toString(), "Lab Manual", "PH102", "Lab Assistant", "/downloads/ph102_lab.pdf"));
    }

    // Method to retrieve all notes
    public List<Note> getAllNotes() {
        // In a real app, this would fetch from Firebase/Database
        return new ArrayList<>(notes); // Return a copy to prevent external modification
    }

    // Method to add a new note (We'll use this later for the POST endpoint)
    public Note addNote(Note note) {
        // Assign a unique ID if one isn't provided (simple approach)
        if (note.getId() == null || note.getId().isEmpty()) {
            note.setId(UUID.randomUUID().toString());
        }
        // In a real app, this would save to Firebase/Database
        notes.add(note);
        return note;
    }

    // Add methods for getNoteById, updateNote, deleteNote later...
}