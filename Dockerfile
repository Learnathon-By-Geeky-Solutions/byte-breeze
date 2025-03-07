# Stage 1: Build the application using Gradle
FROM gradle:7.6.0-jdk17-alpine AS builder
WORKDIR /home/gradle/project
# Copy all project files into the builder container
COPY . .
# Run the Gradle build; adjust the command if necessary
RUN gradle build -x test --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the built JAR file from the builder stage into the runtime image
COPY --from=builder /home/gradle/project/build/libs/*.jar quickdrop.jar

# Set an environment variable for uploads and create the uploads directory
ENV UPLOAD_PATH=/app/uploads
RUN mkdir -p $UPLOAD_PATH

# Expose the application port (should match server.port in your Spring Boot configuration)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "quickdrop.jar"]
