# the first stage of our build will use a maven 3.6.1 parent image
FROM maven:3.9.3-eclipse-temurin-11-alpine AS MAVEN_BUILD

# copy the pom and src code to the container
COPY ./ ./

EXPOSE 18115

RUN mvn clean package -q

CMD ["mvn", "spring-boot:run"]

## package our application code
#RUN mvn clean package -q
#
## the second stage of our build will use open jdk 8 on alpine 3.9
#FROM openjdk:8-jre-alpine3.9
#
## copy only the artifacts we need from the first stage and discard the rest
#COPY --from=MAVEN_BUILD /target/og-spipes-1.0-SNAPSHOT.jar /spipes-editor.jar
#
## set the startup command to execute the jar
#CMD ["java", "-jar", "/spipes-editor.jar"]