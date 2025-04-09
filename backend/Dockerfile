# Start with a base image that includes the JDK
FROM maven:3.9-eclipse-temurin-21

# Set the working directory in the Docker image
WORKDIR /app

# Copy the pom.xml file to the working directory
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the application code to the working directory
COPY src ./src

# Expose the port the application runs on
EXPOSE 8080

# Build the application using Maven
CMD ["mvn", "spring-boot:run"]