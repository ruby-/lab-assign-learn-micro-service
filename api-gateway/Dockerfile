FROM openjdk:11-jre-slim
MAINTAINER r5by dllizhongwei@gmail.com

EXPOSE 8080
VOLUME /tmp

ENV ROUTE_USER_HOST user-service
ENV ROUTE_USER_PORT 8082

ARG JAR_FILE="target/api-gateway-1.0-SNAPSHOT.jar"
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
