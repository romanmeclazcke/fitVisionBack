# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar solo lo necesario para descargar dependencias
COPY pom.xml .
COPY .mvn/ .mvn/
RUN mvn dependency:go-offline -B

# Ahora copiar el código fuente
COPY src/ src/

# Compilar sin limpiar todo (más rápido)
RUN mvn package -DskipTests -T 1C

# Stage 2: Final image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

ARG JAR_FILE=target/fitVisionBack-0.0.1-SNAPSHOT.jar
COPY --from=build /app/${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]