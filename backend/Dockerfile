# backend/Dockerfile

# Use an official OpenJDK runtime as a parent image
# Choose a specific version matching your project and prefer slim/jre versions for smaller images
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Argument to specify the JAR file path (can be overridden during build)
ARG JAR_FILE=target/*.jar

# Copy the packaged application JAR file (using the argument)
# into the container at the root of the working directory.
COPY ${JAR_FILE} app.jar

# Make port 8080 available to the world outside this container
# (Default Spring Boot port - change if your app uses a different one)
EXPOSE 8080

# Define the command to run your application when the container launches
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Example: Add arguments for Spring profiles if needed later
# ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]