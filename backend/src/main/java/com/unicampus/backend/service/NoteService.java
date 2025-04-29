// backend/src/main/java/com/unicampus/backend/service/NoteService.java
package com.unicampus.backend.service;

import com.unicampus.backend.model.Note;
import com.google.api.core.ApiFuture; // Import ApiFuture
import com.google.cloud.firestore.CollectionReference; // Import CollectionReference
import com.google.cloud.firestore.DocumentReference; // Import DocumentReference
import com.google.cloud.firestore.Firestore;       // Import Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot; // Import QueryDocumentSnapshot
import com.google.cloud.firestore.QuerySnapshot;   // Import QuerySnapshot
import com.google.cloud.firestore.WriteResult;    // Import WriteResult
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException; // Import ExecutionException

@Service
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);
    private static final String NOTES_COLLECTION = "notes"; // Firestore collection name

    private final Firestore firestore; // Inject Firestore bean

    @Autowired // Constructor injection for Firestore
    public NoteService(Firestore firestore) {
        this.firestore = firestore;
        logger.info("NoteService initialized with Firestore instance.");
    }

    // --- REMOVE OLD IN-MEMORY LIST AND CONSTRUCTOR ---
    // private final List<Note> notes = new ArrayList<>();
    // public NoteService() { ... }

    // Method to retrieve all notes from Firestore
    public List<Note> getAllNotes() throws ExecutionException, InterruptedException {
        logger.debug("Fetching all notes from Firestore collection '{}'", NOTES_COLLECTION);
        List<Note> noteList = new ArrayList<>();
        CollectionReference notesCollection = firestore.collection(NOTES_COLLECTION);

        // Asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = notesCollection.get();
        // future.get() blocks until the operation completes
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            // Convert each Firestore document back into a Note object
            Note note = document.toObject(Note.class);
            // Firestore automatically maps fields if names match
            // Set the ID from the document ID
            note.setId(document.getId());
            noteList.add(note);
        }
        logger.info("Retrieved {} notes from Firestore.", noteList.size());
        return noteList;
    }

    // Method to add a new note to Firestore
    public Note addNote(Note note) throws ExecutionException, InterruptedException {
        logger.debug("Adding new note to Firestore collection '{}'", NOTES_COLLECTION);
        CollectionReference notesCollection = firestore.collection(NOTES_COLLECTION);

        // Let Firestore generate the document ID
        ApiFuture<DocumentReference> future = notesCollection.add(note);
        // Note: The 'note' object passed to add() should NOT have the ID set here

        // Get the reference to the newly created document (blocks until complete)
        DocumentReference addedDocRef = future.get();
        logger.info("Note added to Firestore with ID: {}", addedDocRef.getId());

        // Set the generated ID back onto the Note object before returning
        note.setId(addedDocRef.getId());
        return note;
    }

    // --- TODO LATER: Implement methods for getNoteById, updateNote, deleteNote ---
    /*
    public Note getNoteById(String id) throws ExecutionException, InterruptedException { ... }
    public Note updateNote(String id, Note note) throws ExecutionException, InterruptedException { ... }
    public void deleteNote(String id) throws ExecutionException, InterruptedException { ... }
    */
}