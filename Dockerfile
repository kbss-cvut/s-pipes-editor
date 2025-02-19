FROM maven:3-eclipse-temurin-17 as build

WORKDIR /s-pipes-editor

COPY pom.xml pom.xml

RUN mvn -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY src src

RUN mvn package -B -DskipTests=true

FROM eclipse-temurin:17-jdk-alpine as runtime
COPY --from=build  /s-pipes-editor/target/og-spipes-*.jar s-pipes-editor.jar

EXPOSE 18115

ENTRYPOINT ["java","-jar","/s-pipes-editor.jar"]