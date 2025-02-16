# Stage 1: Build the application
FROM eclipse-temurin:17-jdk AS builder
# Set the working directory
WORKDIR /app
# Copy the application code
COPY . .
# Given permissions to mvnw
RUN chmod +x mvnw
# Ensure Maven is available and build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
# Set the working directory
WORKDIR /app
# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar
# Expose the port the app will run on
EXPOSE 8080

ENV DATABASE_URL jdbc:mysql://mysqldb:3306/pokemon_db
ENV DATABASE_URSERNAME root
ENV DATABASE_PASSWORD ""
ENV DATABASE_DRIVER com.mysql.cj.jdbc.Driver

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
