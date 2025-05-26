# Dockerfile
FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw com.vaadin:vaadin-maven-plugin:prepare-frontend
RUN ./mvnw clean package -DskipTests

# Runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]