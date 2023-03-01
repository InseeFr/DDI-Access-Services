FROM maven:3.6.2 as mvn
WORKDIR /magma
COPY ./ /magma/
RUN mvn -B -f /magma/pom.xml package

:
FROM tomcat:9.0.71-jre17

MAINTAINER hugobouttes

RUN rm -rf $CATALINA_HOME/webapps/*
ADD application.properties $CATALINA_HOME/webapps/application.properties
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war
