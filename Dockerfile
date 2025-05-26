# Etapa 1: “builder” – compila el JAR y genera recursos de Vaadin
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copia sólo lo necesario para acelerar el cacheo de Maven
COPY pom.xml mvnw .mvn/ ./
# Asegura que mvnw sea ejecutable
RUN chmod +x mvnw

# Copia el código fuente
COPY src ./src

# Ejecuta los goals de Vaadin y empaqueta en modo producción
RUN ./mvnw clean verify -Pproduction

# Etapa 2: “runner” – ejecuta el JAR en un JRE ligero
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia el JAR generado en la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Expone el puerto que usa tu aplicación
EXPOSE 8080

# Variables de entorno para producción
ENV VAADIN_PRODUCTION_MODE=true

# Comando de arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
