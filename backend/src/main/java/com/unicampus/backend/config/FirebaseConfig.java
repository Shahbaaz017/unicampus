// backend/src/main/java/com/unicampus/backend/config/FirebaseConfig.java
package com.unicampus.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value; // Commented out for hardcoding
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.io.InputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    private final ResourceLoader resourceLoader; // Use ResourceLoader

    // --- @Value field commented out ---
    // @Value("${firebase.service-account.key-path:file:/app/firebase-key.json}")
    // private String keyPath;

    // Inject ResourceLoader
    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initializeFirebase() {
        // --- Using HARDCODED path for local Codespaces test ---
        String hardcodedKeyPath = "file:/workspaces/unicampus/backend/firebase-service-account.json";
        // --- End hardcoded path ---

        try {
            if (FirebaseApp.getApps().isEmpty()) { // Prevent re-initialization
                logger.info("Initializing Firebase Admin SDK using HARDCODED key path: {}", hardcodedKeyPath);

                Resource resource = resourceLoader.getResource(hardcodedKeyPath); // Use hardcoded path
                if (!resource.exists()) {
                     logger.error("Firebase service account key file NOT FOUND at specified path: {}", hardcodedKeyPath);
                     // Fail fast if key isn't found where expected
                     throw new RuntimeException("Firebase key file not found at: " + hardcodedKeyPath + ". Check path and devcontainer setup.");
                }
                logger.info("Found Firebase key file resource: {}", resource);

                try (InputStream serviceAccount = resource.getInputStream()) { // Use try-with-resources
                    // Check if stream has content AFTER getting it
                    if (serviceAccount.read() == -1) { // Read one byte, -1 means end of stream immediately
                        logger.error("Firebase service account key file appears to be empty at: {}", hardcodedKeyPath);
                        throw new RuntimeException("Firebase key file is empty at: " + hardcodedKeyPath);
                    }
                    // Reset stream or get a new one
                    try (InputStream serviceAccountForCredentials = resource.getInputStream()) {
                        FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccountForCredentials))
                            .build();
                        FirebaseApp.initializeApp(options);
                        logger.info("Firebase Admin SDK initialized successfully.");
                    }

                } catch (IOException ioException) {
                     logger.error("IOException while reading Firebase key file input stream: {}", hardcodedKeyPath, ioException);
                     throw new RuntimeException("Failed to read Firebase key file stream.", ioException);
                }
            } else {
                logger.info("Firebase Admin SDK already initialized.");
            }
        } catch (Exception e) {
            // Log the detailed exception
            logger.error("FATAL: Error initializing Firebase Admin SDK. Path: {}, Error: {}", hardcodedKeyPath, e.getMessage(), e);
            // Rethrow or handle as critical startup failure
             throw new RuntimeException("Failed to initialize Firebase Admin SDK. See logs for details.", e);
        }
    }

    @Bean
    public Firestore firestore() {
         if (FirebaseApp.getApps().isEmpty()) {
             // This indicates a serious initialization problem if reached
             logger.error("CRITICAL: Attempted to get Firestore bean, but FirebaseApp is not initialized!");
             throw new IllegalStateException("FirebaseApp not initialized. Cannot provide Firestore bean.");
         }
         logger.debug("Providing Firestore bean instance.");
         return FirestoreClient.getFirestore();
    }
}