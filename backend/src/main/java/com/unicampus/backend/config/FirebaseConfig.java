// backend/src/main/java/com/unicampus/backend/config/FirebaseConfig.java
package com.unicampus.backend.config;

// ... (Imports: GoogleCredentials, FirebaseApp, FirebaseOptions, FirestoreClient, Firestore, PostConstruct, Logger, LoggerFactory, Value, Bean, Configuration, Resource, ResourceLoader, InputStream) ...
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
    private final ResourceLoader resourceLoader; // Use ResourceLoader

    // Default path relative to 'backend' directory (where CI writes it)
    @Value("${firebase.service-account.key-path:file:./firebase-service-account.json}")
    private String keyPath;

    // Inject ResourceLoader
    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase Admin SDK using key path: {}", keyPath);

                Resource resource = resourceLoader.getResource(keyPath); // Use ResourceLoader
                if (!resource.exists()) {
                     logger.error("Firebase service account key file NOT FOUND at specified path: {}", keyPath);
                     // Fail fast if key isn't found where expected
                     throw new RuntimeException("Firebase key file not found at: " + keyPath + ". Check path and CI/devcontainer setup.");
                }
                logger.info("Found Firebase key file resource: {}", resource); // Log resource info

                try (InputStream serviceAccount = resource.getInputStream()) { // Use try-with-resources
                    if (serviceAccount.available() <= 0) {
                        logger.error("Firebase service account key file appears to be empty at: {}", keyPath);
                        throw new RuntimeException("Firebase key file is empty at: " + keyPath);
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
            // Log the detailed exception
            logger.error("FATAL: Error initializing Firebase Admin SDK. Path: {}, Error: {}", keyPath, e.getMessage(), e);
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