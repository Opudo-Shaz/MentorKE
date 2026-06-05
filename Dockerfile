FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17

# Add PostgreSQL driver
RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main

ADD https://jdbc.postgresql.org/download/postgresql-42.7.3.jar \
    $JBOSS_HOME/modules/org/postgresql/main/postgresql-42.7.3.jar

COPY docker/module.xml $JBOSS_HOME/modules/org/postgresql/main/module.xml

# Copy datasource config
COPY docker/configure-ds.cli /tmp/configure-ds.cli
RUN $JBOSS_HOME/bin/standalone.sh --admin-only & \
    sleep 10 && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --file=/tmp/configure-ds.cli && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=":shutdown" && \
    rm -rf $JBOSS_HOME/standalone/log/*

# Deploy the war
COPY target/MentorKE.war $JBOSS_HOME/standalone/deployments/

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
