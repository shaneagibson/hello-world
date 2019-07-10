FROM adoptopenjdk/openjdk11:jdk-11.0.3_7-slim

ARG JAR_FILE
ARG VERSION

ENV VERSION=$VERSION

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

COPY "${JAR_FILE}" /microservice/app.jar

ENTRYPOINT java ${JAVA_OPTS} $* -jar /microservice/app.jar -Dapp.version=${VERSION}