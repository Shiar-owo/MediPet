# -- Build stage --
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# 1. Copy the wrapper tools first (these change very rarely)
COPY .mvn/ .mvn
COPY mvnw ./

# 2. Copy the project definition (changes only when adding libraries)
COPY pom.xml ./

# 3. Cache and download dependencies using a BuildKit cache mount
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

# 4. Copy source code
COPY src ./src

# 5. Build the application using the SAME cache mount so it reuses the jars
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests -B

# -- Runtime stage --
FROM eclipse-temurin:25-jre
WORKDIR /app

# Keep your runtime layer clean and minimal
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built jar from the build stage
COPY --from=build /app/target/medipet-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]