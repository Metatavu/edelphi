mvn clean package
docker build -t edelphi .
docker tag $(docker images -q edelphi) metatavu/edelphi:develop
docker push metatavu/edelphi
