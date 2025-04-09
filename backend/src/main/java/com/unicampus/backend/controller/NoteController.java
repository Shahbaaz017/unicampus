// backend/src/main/java/com/unicampus/backend/controller/NoteController.java
package com.unicampus.backend.controller; // Controller package

import com.unicampus.backend.model.Note;       // Import the Note model
import com.unicampus.backend.service.NoteService; // Import the Note service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Marks this class as a REST controller (combines @Controller and @ResponseBody)
@RequestMapping("/api/notes") // Base path for all methods in this controller
public class NoteController {

    private final NoteService noteService;

    // Constructor Injection: Spring injects the NoteService bean here
    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Handles HTTP GET requests to /api/notes
    @GetMapping
    public List<Note> getAllNotes() {
        // Delegate the call to the service layer
        return noteService.getAllNotes();
        // Spring Boot automatically converts the returned List<Note> to JSON
    }

    // Add methods for POST (@PostMapping), GET by ID (@GetMapping("/{id}")), etc. later...
    // Example POST endpoint structure (implement later):
    /*
    @PostMapping
    public Note createNote(@RequestBody Note note) {
        return noteService.addNote(note);
    }
    */
}