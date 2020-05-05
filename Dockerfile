FROM tomcat:8.5.16-jre8

MAINTAINER bwerquin

RUN rm -rf $CATALINA_HOME/webapps/*
ADD ddi-access-services.properties $CATALINA_HOME/webapps/ddi-access-services.properties
ADD /*.war $CATALINA_HOME/webapps/ROOT.war
