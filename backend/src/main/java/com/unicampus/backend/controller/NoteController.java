// backend/src/main/java/com/unicampus/backend/controller/NoteController.java
package com.unicampus.backend.controller;

import com.unicampus.backend.model.Note;
import com.unicampus.backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
// --- Ensure these imports exist ---
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
// --- End Ensure imports ---

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // --- Verify/Add throws clause here ---
    @GetMapping
    public List<Note> getAllNotes() throws ExecutionException, InterruptedException {
        return noteService.getAllNotes();
    }
    // --- End Verify/Add ---


    // --- Verify/Add throws clause here ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody Note note) throws ExecutionException, InterruptedException {
        return noteService.addNote(note);
    }
    // --- End Verify/Add ---
}