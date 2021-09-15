#!/bin/bash

echo "Starting docker container for MySQL..."
docker-compose up -d mysql

sleep 5

CONTAINER_ID=$(docker ps -q --filter name=edelphi_mysql_1)
echo "MySQL container started with ID: $CONTAINER_ID"

docker cp ../db_dumps/keycloak.sql $CONTAINER_ID:/tmp/kc.sql
echo "Copied database dump for Keycloak"

echo "Creating databases with dump data..."
docker exec $CONTAINER_ID  mysql -uroot -proot -e 'DROP DATABASE IF EXISTS `keycloak`; CREATE DATABASE `keycloak` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */; USE keycloak; source /tmp/kc.sql; commit;'

docker-compose down