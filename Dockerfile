# Use OpenJDK as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/*.jar quickdrop.jar

# Set an environment variable for uploads
ENV UPLOAD_PATH=/app/uploads

# Create an uploads directory inside the container
RUN mkdir -p $UPLOAD_PATH

# Expose the application port (must match server.port in application.properties)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "quickdrop.jar"]
