// backend/src/test/java/com/unicampus/backend/service/NoteServiceTest.java
package com.unicampus.backend.service;

import com.unicampus.backend.model.Note;
import com.google.api.core.ApiFuture; // Import ApiFuture
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.BeforeEach; // Import JUnit 5 annotations
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; // Import Mockito annotations
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Import Mockito extension

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*; // Import JUnit assertions
import static org.mockito.ArgumentMatchers.anyString; // Import Mockito matchers
import static org.mockito.Mockito.*; // Import Mockito static methods

@ExtendWith(MockitoExtension.class) // Initialize Mockito annotations
class NoteServiceTest {

    @Mock // Creates a mock instance of Firestore
    private Firestore firestoreMock;

    @Mock // Mock dependent Firestore objects
    private CollectionReference collectionReferenceMock;
    @Mock
    private DocumentReference documentReferenceMock;
    @Mock
    private ApiFuture<DocumentSnapshot> apiFutureMock;
    @Mock
    private DocumentSnapshot documentSnapshotMock;

    @InjectMocks // Creates an instance of NoteService and injects mocks into it
    private NoteService noteService;

    private Note testNote;
    private String testNoteId = "test-id-123";

    @BeforeEach // Runs before each test method
    void setUp() {
        // Setup common test data
        testNote = new Note(testNoteId, "Test Title", "Test Subject", "Tester", "/test/url");
        // Note: We set the ID here for comparison, Firestore normally generates it on add
    }

    @Test
    void getNoteById_whenNoteExists_shouldReturnNote() throws ExecutionException, InterruptedException {
        // --- Arrange ---
        // Define behavior for mock interactions
        when(firestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(testNoteId)).thenReturn(documentReferenceMock);
        when(documentReferenceMock.get()).thenReturn(apiFutureMock);
        when(apiFutureMock.get()).thenReturn(documentSnapshotMock);
        when(documentSnapshotMock.exists()).thenReturn(true);
        // Crucially, define what the mock document converts to
        when(documentSnapshotMock.toObject(Note.class)).thenReturn(testNote);
        // Define what getId() returns on the mock snapshot
        when(documentSnapshotMock.getId()).thenReturn(testNoteId);


        // --- Act ---
        // Call the actual service method we want to test
        Optional<Note> result = noteService.getNoteById(testNoteId);

        // --- Assert ---
        // Verify the result
        assertTrue(result.isPresent(), "Result should not be empty");
        assertEquals(testNoteId, result.get().getId(), "IDs should match");
        assertEquals("Test Title", result.get().getTitle(), "Titles should match");

        // Verify that Firestore mock methods were called as expected
        verify(firestoreMock).collection("notes");
        verify(collectionReferenceMock).document(testNoteId);
        verify(documentReferenceMock).get();
        verify(apiFutureMock).get();
        verify(documentSnapshotMock).toObject(Note.class);
    }

    @Test
    void getNoteById_whenNoteDoesNotExist_shouldReturnEmpty() throws ExecutionException, InterruptedException {
        // --- Arrange ---
        // Define behavior for mock interactions when document doesn't exist
        when(firestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock); // Use anyString for ID
        when(documentReferenceMock.get()).thenReturn(apiFutureMock);
        when(apiFutureMock.get()).thenReturn(documentSnapshotMock);
        when(documentSnapshotMock.exists()).thenReturn(false); // Simulate document not found

        // --- Act ---
        Optional<Note> result = noteService.getNoteById("non-existent-id");

        // --- Assert ---
        assertFalse(result.isPresent(), "Result should be empty");

        // Verify mocks (toObject should NOT be called)
        verify(firestoreMock).collection("notes");
        verify(collectionReferenceMock).document("non-existent-id");
        verify(documentReferenceMock).get();
        verify(apiFutureMock).get();
        verify(documentSnapshotMock, never()).toObject(Note.class); // Ensure toObject wasn't called
    }
}