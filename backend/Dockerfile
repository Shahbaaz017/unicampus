# backend/Dockerfile

# Use an official OpenJDK runtime as a parent image (Java 17 JRE)
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Argument to specify the JAR file path (Maven default)
ARG JAR_FILE=target/*.jar

# Copy the packaged application JAR file
# This relies on the build context being set correctly in the CI workflow
COPY ${JAR_FILE} app.jar

# Make port 8080 available (Default Spring Boot port)
EXPOSE 8080

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]