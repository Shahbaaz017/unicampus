// backend/src/main/java/com/unicampus/backend/controller/NoteController.java
package com.unicampus.backend.controller;

import com.unicampus.backend.model.Note;
import com.unicampus.backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import PathVariable
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
// Removed import java.util.Optional; as we handle it differently here

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // GET /api/notes - Retrieve all notes
    @GetMapping
    public List<Note> getAllNotes() throws ExecutionException, InterruptedException {
        return noteService.getAllNotes();
    }

    // POST /api/notes - Create a new note
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody Note note) throws ExecutionException, InterruptedException {
        return noteService.addNote(note);
    }

    // --- NEW METHOD ---
    // GET /api/notes/{id} - Retrieve a single note by its ID
    @GetMapping("/{id}") // Maps GET requests like /api/notes/some-firestore-id
    public ResponseEntity<Note> getNoteById(@PathVariable String id) throws ExecutionException, InterruptedException {
        // @PathVariable extracts the 'id' part from the URL path
        // Call the service method which returns an Optional<Note>
        return noteService.getNoteById(id)
                // If Optional contains a Note, wrap it in ResponseEntity with 200 OK status
                .map(note -> ResponseEntity.ok(note))
                // If Optional is empty (note not found), return ResponseEntity with 404 Not Found status
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // --- END NEW METHOD ---

}