# ---------- Build Stage ----------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Development Stage ----------
FROM maven:3.9-eclipse-temurin-21 AS development

WORKDIR /app
COPY pom.xml .
COPY src ./src

EXPOSE 8080
CMD ["mvn", "spring-boot:run"]

# ---------- Production Stage ----------
FROM eclipse-temurin:21-jre-alpine AS production

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
