FROM maven:3.6.2 as mvn
WORKDIR /DDI-Access-Services
COPY ./ /DDI-Access-Services/
RUN mvn -B -f /DDI-Access-Services/pom.xml package

FROM tomcat:9.0.71-jre17

MAINTAINER hugobouttes

RUN rm -rf $CATALINA_HOME/webapps/*
ADD application.properties $CATALINA_HOME/webapps/application.properties
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war
