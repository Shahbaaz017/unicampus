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
    // Default to the absolute path where the key will be inside the Docker container
    // For local runs (like Codespaces), this needs to be overridden by the
    // FIREBASE_SERVICE_ACCOUNT_KEY_PATH environment variable if the devcontainer
    // places the key file elsewhere (e.g., backend/firebase-service-account.json)
    @Value("${firebase.service-account.key-path:file:/app/firebase-key.json}")
    private String keyPath;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initializeFirebase() {
        // Use a temporary variable to hold the effective path
        String effectiveKeyPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_KEY_PATH");
        if (effectiveKeyPath == null || effectiveKeyPath.trim().isEmpty()) {
            effectiveKeyPath = this.keyPath; // Use default from @Value if env var not set
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase Admin SDK using effective key path: {}", effectiveKeyPath);

                Resource resource = resourceLoader.getResource(effectiveKeyPath); // Use effective path
                if (!resource.exists()) {
                     logger.error("Firebase service account key file NOT FOUND at specified path: {}", effectiveKeyPath);
                     throw new RuntimeException("Firebase key file not found at: " + effectiveKeyPath + ". Check path and environment variable setup.");
                }
                logger.info("Found Firebase key file resource: {}", resource);

                try (InputStream serviceAccount = resource.getInputStream()) {
                    if (serviceAccount.available() <= 0) {
                        logger.error("Firebase service account key file appears to be empty at: {}", effectiveKeyPath);
                        throw new RuntimeException("Firebase key file is empty at: " + effectiveKeyPath);
                    }

                    FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                    FirebaseApp.initializeApp(options);
                    logger.info("Firebase Admin SDK initialized successfully.");
                }
            } else {
                logger.info("Firebase Admin SDK already initialized.");
            }
        } catch (Exception e) {
            logger.error("FATAL: Error initializing Firebase Admin SDK. Path: {}, Error: {}", effectiveKeyPath, e.getMessage(), e);
             throw new RuntimeException("Failed to initialize Firebase Admin SDK. See logs for details.", e);
        }
    }

    @Bean
    public Firestore firestore() {
         if (FirebaseApp.getApps().isEmpty()) {
             logger.error("CRITICAL: Attempted to get Firestore bean, but FirebaseApp is not initialized!");
             throw new IllegalStateException("FirebaseApp not initialized. Cannot provide Firestore bean.");
         }
         logger.debug("Providing Firestore bean instance.");
         return FirestoreClient.getFirestore();
    }
}