# Start with a base image that includes the JDK
FROM eclipse-temurin:21-jdk

# Set the working directory in the Docker image
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy the pom.xml file to the working directory
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code to the working directory
COPY . .

# Expose the port the application runs on
EXPOSE 8080

# Build the application using Maven
CMD ["mvn", "spring-boot:run"]