FROM adoptopenjdk/openjdk11:jdk-11.0.3_7-slim

ARG JAR_FILE
ARG VERSION

ENV VERSION=$VERSION

EXPOSE 8080

COPY "${JAR_FILE}" /microservice/app.jar

ENTRYPOINT java -jar /microservice/app.jar -Dapp.version=${VERSION}