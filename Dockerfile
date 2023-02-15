FROM tomcat:9.0.71-jre17

MAINTAINER hugobouttes

RUN rm -rf $CATALINA_HOME/webapps/*
ADD application.properties $CATALINA_HOME/webapps/application.properties
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war
