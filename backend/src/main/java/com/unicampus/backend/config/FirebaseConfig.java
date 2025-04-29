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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    private final ResourceLoader resourceLoader;

    // --- UPDATED VALUE ---
    // Reference the key file created by the devcontainer postCreateCommand
    // Use "file:" prefix and the exact filename relative to workspace root
    @Value("${firebase.service-account.key-path:file:./firebase-service-account.json}")
    private String keyPath;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase Admin SDK using key path: {}", keyPath);

                Resource resource = resourceLoader.getResource(keyPath);
                if (!resource.exists()) {
                     logger.error("Firebase service account key file NOT FOUND at specified path: {}", keyPath);
                     throw new RuntimeException("Firebase key file not found: " + keyPath);
                }
                logger.info("Found Firebase key file at: {}", resource.getURL()); // Log found path
                InputStream serviceAccount = resource.getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK initialized successfully.");
            } else {
                logger.info("Firebase Admin SDK already initialized.");
            }
        } catch (Exception e) {
            logger.error("Error initializing Firebase Admin SDK", e);
        }
    }

    @Bean
    public Firestore firestore() {
         if (FirebaseApp.getApps().isEmpty()) {
             logger.warn("Firebase not initialized yet when requesting Firestore bean!");
             // Optionally initialize here again, or rely on PostConstruct ordering
             // initializeFirebase(); // Be careful about potential race conditions/double init
             // Alternatively, throw an exception or return null, depending on desired behavior
             return null; // Or throw IllegalStateException
         }
         logger.info("Providing Firestore bean instance.");
         return FirestoreClient.getFirestore();
    }
}