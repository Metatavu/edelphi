#!/bin/bash

MANAGEMENT_HTTP_PORT=9790

. scripts/keycloak-version.sh

${KEYCLOAK}/bin/jboss-cli.sh --connect command=:shutdown --controller=localhost:${MANAGEMENT_HTTP_PORT} 
