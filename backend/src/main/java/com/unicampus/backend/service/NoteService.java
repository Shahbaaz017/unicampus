// backend/src/main/java/com/unicampus/backend/service/NoteService.java
package com.unicampus.backend.service;

import com.unicampus.backend.model.Note;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot; // Ensure this import exists
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Ensure this import exists
import java.util.concurrent.ExecutionException;

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
            if (note != null) { // Check conversion result
                note.setId(document.getId());
                noteList.add(note);
            } else {
                 logger.warn("Failed to convert document {} to Note object.", document.getId());
            }
        }
        logger.info("Retrieved {} notes from Firestore.", noteList.size());
        return noteList;
    }

    // Method to add a new note to Firestore
    public Note addNote(Note note) throws ExecutionException, InterruptedException {
        logger.debug("Adding new note to Firestore collection '{}': {}", NOTES_COLLECTION, note); // Log note data
        CollectionReference notesCollection = firestore.collection(NOTES_COLLECTION);

        // Let Firestore generate the document ID - ensure note object passed doesn't have ID yet
        ApiFuture<DocumentReference> future = notesCollection.add(note);

        // Get the reference to the newly created document (blocks until complete)
        DocumentReference addedDocRef = future.get();
        logger.info("Note added to Firestore with ID: {}", addedDocRef.getId());

        // Set the generated ID back onto the Note object before returning
        note.setId(addedDocRef.getId());
        return note;
    }

    // --- UNCOMMENTED METHOD ---
    // Method to retrieve a single note by its ID from Firestore
    public Optional<Note> getNoteById(String id) throws ExecutionException, InterruptedException {
        logger.debug("Fetching note with ID '{}' from Firestore collection '{}'", id, NOTES_COLLECTION);
        if (id == null || id.trim().isEmpty()) {
             logger.warn("Attempted to fetch note with null or empty ID.");
             return Optional.empty(); // Return empty if ID is invalid
        }
        DocumentReference docRef = firestore.collection(NOTES_COLLECTION).document(id);
        // Asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = docRef.get();
        // future.get() blocks until the operation completes
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            // Convert Firestore document to Note object
            Note note = document.toObject(Note.class);
            // Set the ID from the document ID
            if (note != null) { // Ensure conversion was successful
               note.setId(document.getId());
               logger.info("Retrieved note with ID: {}", id);
               // Wrap the found note in an Optional
               return Optional.of(note); // Use Optional.of since we know note is not null here
            } else {
                logger.error("Document {} exists but failed to convert to Note object.", id);
                return Optional.empty(); // Conversion failed
            }
        } else {
            logger.warn("Note with ID '{}' not found in Firestore.", id);
            // Return an empty Optional if not found
            return Optional.empty();
        }
    }
    // --- END UNCOMMENTED METHOD ---


    // --- Methods still commented out for later ---
    /*
    public Note updateNote(String id, Note note) throws ExecutionException, InterruptedException { ... }
    public void deleteNote(String id) throws ExecutionException, InterruptedException { ... }
    */
} // End of class