# 1단계: 빌드
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
# Windows에서 체크아웃한 gradlew가 CRLF로 들어와도(.gitattributes로 막아뒀지만 안전장치로 한 번 더)
# 셔뱅이 깨지지 않도록 여기서 CR을 제거함.
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# 2단계: 실행
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
