# backend/Dockerfile

# Use an official OpenJDK runtime as a parent image (Java 17 JRE)
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Argument to specify the JAR file path (Maven default)
# ARG JAR_FILE=target/*.jar # We won't use this directly anymore

# --- MODIFIED LINE ---
# Copy the JAR file using a path relative to the build context (repo root)
COPY backend/target/*.jar app.jar

# Make port 8080 available (Default Spring Boot port)
EXPOSE 8080

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]