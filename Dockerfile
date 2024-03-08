FROM jboss/wildfly:16.0.0.Final

ADD --chown=jboss edelphi/target/*.war /opt/jboss/wildfly/standalone/deployments/app.war
ADD --chown=jboss ./docker/entrypoint.sh /opt/docker/entrypoint.sh 
ADD --chown=jboss ./docker/host.cli /opt/docker/host.cli
ADD --chown=jboss ./docker/kubernets-jgroups.cli /opt/docker/kubernets-jgroups.cli
ADD --chown=jboss ./docker/jdbc.cli /opt/docker/jdbc.cli
ADD --chown=jboss ./docker/hibernate-search.cli /opt/docker/hibernate-search.cli
ADD --chown=jboss ./docker/interfaces.cli /opt/docker/interfaces.cli
ADD --chown=jboss ./docker/jboss-cli.properties /opt/docker/jboss-cli.properties
ADD --chown=jboss ./docker/keycloak.cli /opt/docker/keycloak.cli
ADD --chown=jboss ./docker/smtp.cli /opt/docker/smtp.cli
ADD --chown=jboss ./docker/infinispan.cli /opt/docker/infinispan.cli
ADD --chown=jboss ./docker/http-max-parameters.cli /opt/docker/http-max-parameters.cli
RUN chmod a+x /opt/docker/entrypoint.sh

ARG WILDFLY_VERSION=16.0.0.Final
ARG MARIADB_MODULE_VERSION=2.3.0
ARG MYSQL_MODULE_VERSION=8.0.15

RUN curl -L -o /tmp/mariadb-module.zip -L https://static.metatavu.io/wildfly/wildfly-${WILDFLY_VERSION}-mariadb-module-${MARIADB_MODULE_VERSION}.zip
RUN curl -L -o /tmp/mysql-module.zip -L https://static.metatavu.io/wildfly/wildfly-${WILDFLY_VERSION}-mysql-module-${MYSQL_MODULE_VERSION}.zip
RUN curl -L -o /tmp/keycloak-module.zip -L https://github.com/keycloak/keycloak/releases/download/18.0.2/keycloak-oidc-wildfly-adapter-18.0.2.zip

RUN unzip -o /tmp/mariadb-module.zip -d /opt/jboss/wildfly/
RUN unzip -o /tmp/mysql-module.zip -d /opt/jboss/wildfly/
RUN unzip -o /tmp/keycloak-module.zip -d /opt/jboss/wildfly/
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/host.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/jdbc.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/kubernets-jgroups.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/interfaces.cli
#RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/hibernate-search.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --properties=/opt/docker/jboss-cli.properties --file=/opt/jboss/wildfly/bin/adapter-elytron-install-offline.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/keycloak.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/smtp.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/infinispan.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/http-max-parameters.cli

EXPOSE 8080
EXPOSE 9990
EXPOSE 7600
EXPOSE 7601
EXPOSE 7800
EXPOSE 7801
EXPOSE 8888

CMD "/opt/docker/entrypoint.sh"
