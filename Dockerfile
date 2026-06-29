# =============================================
# ETAPA 1: Build con Maven
# =============================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar primero el pom para aprovechar cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# =============================================
# ETAPA 2: Imagen final liviana
# =============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el JAR generado en la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto (debe coincidir con server.port en application.properties)
EXPOSE 9090

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
