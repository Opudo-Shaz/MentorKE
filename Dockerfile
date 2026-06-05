FROM quay.io/wildfly/wildfly:latest-jdk21

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main
COPY docker/postgresql-42.7.3.jar $JBOSS_HOME/modules/org/postgresql/main/postgresql-42.7.3.jar
COPY docker/module.xml $JBOSS_HOME/modules/org/postgresql/main/module.xml
COPY docker/configure-ds.cli /tmp/configure-ds.cli

RUN $JBOSS_HOME/bin/standalone.sh --admin-only -c standalone-full.xml & \
    sleep 20 && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --file=/tmp/configure-ds.cli && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=":shutdown" && \
    rm -rf $JBOSS_HOME/standalone/log/*

COPY target/MentorKE.war $JBOSS_HOME/standalone/deployments/

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-c", "standalone-full.xml"]
