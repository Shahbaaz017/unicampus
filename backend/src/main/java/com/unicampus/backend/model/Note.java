// backend/src/main/java/com/unicampus/backend/model/Note.java
package com.unicampus.backend.model; // Package name includes 'model'

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation: Generates getters, setters, equals, hashCode, toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class Note {
    private String id;
    private String title;
    private String subject;
    private String uploadedBy; // e.g., Professor's name or ID
    private String downloadUrl; // Link to the actual note file (will be placeholder now)
}