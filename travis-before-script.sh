#!/bin/bash

export DISPLAY=:99.0
/sbin/start-stop-daemon --start --quiet --pidfile /tmp/custom_xvfb_99.pid --make-pidfile --background --exec /usr/bin/Xvfb -- :99 -ac -screen 0 1280x1024x16

if [ ! -f itests/.phantomjs/bin/phantomjs ]; then
  rm -fR itests/.phantomjs
  curl -sSL "https://dl.dropboxusercontent.com/s/wz7o1jqclt8f4sy/phantomjs-2.1.1-linux-x86_64.tar.bz2"|tar -xvjC itests/
  mv itests/phantomjs-2.1.1-linux-x86_64 itests/.phantomjs
fi;

if [ ! -f sonar-scanner/bin/sonar-scanner ]; then
  rm -fR sonar-scanner
  wget "https://sonarsource.bintray.com/Distribution/sonar-scanner-cli/sonar-scanner-2.8.zip"
  unzip sonar-scanner-2.8.zip
  mv sonar-scanner-2.8 sonar-scanner
fi;

wget "https://chromedriver.storage.googleapis.com/2.31/chromedriver_linux64.zip"
unzip chromedriver_linux64.zip

rmdir keycloak-3.2.1.Final;scripts/start-keycloak.sh