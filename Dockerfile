# backend/Dockerfile
# Imagen base con Java
FROM eclipse-temurin:17-jdk-alpine

# Metadata opcional
LABEL authors="alumno2DAM"

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo .jar generado por Maven/Gradle
COPY target/BackendTFGGeneral-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de la app
EXPOSE 8080

# Comando que arranca la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
