#!/bin/bash

MANAGEMENT_HTTP_PORT=9790
MANAGEMENT_HTTPS_PORT=9793
HTTP_PORT=8380
HTTPS_PORT=8643

. scripts/keycloak-version.sh

function waitForServer {
  # Give the server some time to start up. Look for a well-known
  # bit of text in the log file. Try at most 50 times before giving up.
  C=50
  while [ $C -gt 0 ]
  do
    grep "Keycloak ${VERSION} (WildFly Core 2.0.10.Final) started" keycloak.log
    if [ $? -eq 0 ]; then
      echo "Server started."
      C=0
    else
      echo -n "."
      C=$(( $C - 1 ))
    fi
    sleep 1
  done
}

ARCHIVE="${KEYCLOAK}.tar.gz"
DIST="keycloak-server-dist"
URL="https://repo1.maven.org/maven2/org/keycloak/$DIST/${VERSION}/$DIST-${VERSION}.tar.gz"
# Download keycloak server if we don't already have it
if [ ! -e $KEYCLOAK ]
then
  curl -o $ARCHIVE $URL
  tar xzf $ARCHIVE
  rm -f $ARCHIVE
fi

# Start the server
$KEYCLOAK/bin/standalone.sh -Djava.net.preferIPv4Stack=true -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=scripts/kc-setup-for-tests.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING -Djboss.management.http.port=$MANAGEMENT_HTTP_PORT -Djboss.management.https.port=$MANAGEMENT_HTTPS_PORT -Djboss.http.port=$HTTP_PORT -Djboss.https.port=$HTTPS_PORT > keycloak.log 2>&1 &

# Try to add an initial admin user, so we can test against
# the server and not get automatically redirected.
$KEYCLOAK/bin/add-user-keycloak.sh -r master -u admin -p admin
waitForServer

# We have to restart the server for the admin user to load?
$KEYCLOAK/bin/jboss-cli.sh --connect command=:reload --controller=localhost:${MANAGEMENT_HTTP_PORT} 

sleep 5

waitForServer

sleep 5