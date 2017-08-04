#!/bin/bash

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

rmdir keycloak-3.2.1.Final;scripts/start-keycloak.sh