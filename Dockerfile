# Etapa 1: build
FROM openjdk:17-jdk-slim AS builder

# Instalar curl y Node.js (Vaadin front-end build)
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar código y dar permisos al wrapper
COPY . .
RUN chmod +x mvnw

# Compilar en modo production (ajusta el perfil si se llama distinto)
RUN ./mvnw clean package -Pproduction -DskipTests

# Etapa 2: runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Solo copiamos el JAR empaquetado
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto 8080 y fallback a $PORT (que Render define)
EXPOSE 8080

# Arranque de la aplicación; adapta el server.port con $PORT cuando esté definido
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]