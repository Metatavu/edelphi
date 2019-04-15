echo "Starting server..." &&
rm -fR /opt/jboss/wildfly/standalone/configuration/standalone_xml_history &&
exec /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 --server-config=standalone-full-ha.xml -Djboss.node.name=$(hostname)