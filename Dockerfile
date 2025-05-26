FROM openjdk:21-jdk-slim

RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN chmod +x ./mvnw

RUN ["./mvnw", "clean", "package", "-Pproduction", "-DskipTests"]

RUN mv /app/target/aditu-test-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 9000

CMD ["java", "-jar", "/app/app.jar"]