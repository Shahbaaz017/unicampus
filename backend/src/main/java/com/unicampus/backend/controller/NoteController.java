// backend/src/main/java/com/unicampus/backend/controller/NoteController.java
package com.unicampus.backend.controller;

import com.unicampus.backend.model.Note;       // Import the Note model
import com.unicampus.backend.service.NoteService; // Import the Note service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Import PostMapping
import org.springframework.web.bind.annotation.RequestBody; // Import RequestBody
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus; // Import ResponseStatus
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

    // GET /api/notes - Retrieve all notes
    @GetMapping
    public List<Note> getAllNotes() {
        // Delegate the call to the service layer
        return noteService.getAllNotes();
        // Spring Boot automatically converts the returned List<Note> to JSON
    }

    // --- NEW METHOD ---
    // POST /api/notes - Create a new note
    @PostMapping // Maps HTTP POST requests to this method
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP status code to 201 Created on success
    public Note createNote(@RequestBody Note note) {
        // @RequestBody tells Spring to deserialize the JSON request body into a Note object
        // Delegate the creation logic to the service layer
        return noteService.addNote(note);
    }
    // --- END NEW METHOD ---

}