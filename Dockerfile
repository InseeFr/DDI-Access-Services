FROM maven:3.9.6-eclipse-temurin-21-alpine as mvn
WORKDIR /DDI-Access-Services
COPY ./ /DDI-Access-Services/
RUN mvn -B -f /DDI-Access-Services/pom.xml package

MAINTAINER hugobouttes

FROM eclipse-temurin:21-alpine
COPY --from=mvn DDI-Access-Services/target/*.jar app.jar
ENTRYPOINT ["java","-Xms256M","-Xmx1024M","-jar","/app.jar"]
