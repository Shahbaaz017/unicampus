# backend/Dockerfile

# Use an official OpenJDK runtime as a parent image (Java 17 JRE)
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# --- NEW LINE: Copy the decoded key file into the image ---
# This assumes the key file exists at backend/firebase-service-account.json
# in the build context BEFORE docker build runs (created by CI Decode step)
COPY backend/firebase-service-account.json /app/firebase-key.json

# Argument for the application JAR (still useful)
ARG JAR_FILE=target/*.jar
# Copy the application JAR relative to the WORKDIR
COPY ${JAR_FILE} app.jar

# Make port 8080 available (Default Spring Boot port)
EXPOSE 8080

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]