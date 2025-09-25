# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Change ownership of the app directory
RUN chown -R spring:spring /app
USER spring:spring

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/url-shortener-0.0.1-SNAPSHOT.jar"]