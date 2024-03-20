FROM maven:3.8.3-openjdk-21 as mvn
WORKDIR /DDI-Access-Services
COPY ./ /DDI-Access-Services/
RUN mvn -B -f /DDI-Access-Services/pom.xml package

MAINTAINER hugobouttes

FROM openjdk:21-alpine
COPY --from=mvn DDI-Access-Services/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]