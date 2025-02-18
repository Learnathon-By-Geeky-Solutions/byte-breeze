# Use OpenJDK as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/quickdrop-0.0.1-SNAPSHOT.jar quickdrop.jar

# Expose the application port (must match server.port in application.properties)
EXPOSE 8080

# Define environment variables (can be overridden in docker-compose or run command)
#ENV PORT=8080
#ENV DB_URL=jdbc:postgresql://localhost:5432/quickdrop
#ENV DB_USERNAME=your_db_user
#ENV DB_PASSWORD=your_db_password

# Run the application
CMD ["java", "-jar", "quickdrop.jar"]
