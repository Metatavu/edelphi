#!/bin/bash

echo "Starting docker container for MySQL..."
docker-compose up -d mysql

sleep 5

CONTAINER_ID=$(docker ps -q --filter name=edelphi[_-]mysql[_-]1)
echo "MySQL container started with ID: $CONTAINER_ID"

docker cp ../db_dumps/edelphi.sql $CONTAINER_ID:/tmp/ed.sql
echo "Copied database dump for eDelphi"

docker cp ../db_dumps/local.sql $CONTAINER_ID:/tmp/lo.sql
echo "Copied local script for eDelphi"

echo "Creating databases with dump data..."
docker exec $CONTAINER_ID  mysql -uroot -proot -e 'DROP DATABASE IF EXISTS `edelphi`; CREATE DATABASE `edelphi` /*!40100 DEFAULT CHARACTER SET utf8 */; USE edelphi; source /tmp/ed.sql; source /tmp/lo.sql; commit;'

docker-compose down