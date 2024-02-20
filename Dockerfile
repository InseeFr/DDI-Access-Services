FROM maven:3.8.3-openjdk-17 as mvn
WORKDIR /DDI-Access-Services
COPY ./ /DDI-Access-Services/
RUN mvn -B -f /DDI-Access-Services/pom.xml package

MAINTAINER hugobouttes

FROM openjdk:17-alpine
COPY --from=mvn DDI-Access-Services/target/rmes-0.1.10-BetaElastic.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]