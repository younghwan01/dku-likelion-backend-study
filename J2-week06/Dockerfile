# 예제: 이 Dockerfile을 Gradle 기반 Spring Boot 프로젝트 루트에서 사용
# J2-week06 폴더에는 Spring Boot 앱 소스가 없으므로 단독 빌드는 불가능
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle build.gradle ./
RUN chmod +x gradlew

COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
