FROM m.daocloud.io/docker.io/library/eclipse-temurin:21-jre

WORKDIR /app

COPY sxupr-device-simulator/target/sxupr-device-simulator-1.0-SNAPSHOT.jar app.jar

ENV TZ=Asia/Shanghai

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]