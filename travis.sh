#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ -n "${GITHUB_TOKEN}" ] && [ -n "${SONAR_TOKEN}" ]; then
  echo "Pull request"
  
  PROJECT_VERSION=`mvn -f pom.xml -q -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec`
  
  sh sonar-scanner/bin/sonar-scanner -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.analysis.mode=issues \
    -Dsonar.login=$SONAR_TOKEN \
    -Dsonar.projectKey=$SONAR_PROJECT_KEY \
    -Dsonar.projectName=eDelphi \
    -Dsonar.projectVersion=$PROJECT_VERSION \
    -Dsonar.sources=edelphi/src,edelphi-persistence/src,itests/src,smvcj/src \
    -Dsonar.java.binaries=edelphi/target/classes,edelphi-persistence/target/classes,smvcj/target/classes \
    -Dsonar.java.source=1.8 \
    -Dsonar.github.oauth=$GITHUB_TOKEN \
    -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
    -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST

  mvn clean verify -Pui -Dit.skipGoogleTests=true -Dit.browser=chrome-headless -DrepoToken=$COVERALLS_TOKEN -DskipCoverage=false -Dwebdriver.chrome.driver=../chromedriver
  TEST_STATUS=$?
  
  if [ "$TEST_STATUS" != "0" ]; then
    pip install --user awscli
    export PATH=$PATH:$HOME/.local/bin
    export S3_PATH=s3://$AWS_BUCKET/$TRAVIS_REPO_SLUG/$TRAVIS_BUILD_NUMBER
    aws s3 cp itests/target/cargo/configurations/wildfly10x/log $S3_PATH --recursive
    aws s3 cp itests/target $S3_PATH --recursive --exclude "*" --include "*.png" --include "*.html" 
  fi
  
  exit $TEST_STATUS
  
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "develop" ]; then

  echo "Develop build"
  
  PROJECT_VERSION=`mvn -f pom.xml -q -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec`
  
  sh sonar-scanner/bin/sonar-scanner -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.analysis.mode=publish \
    -Dsonar.login=$SONAR_TOKEN \
    -Dsonar.projectKey=$SONAR_PROJECT_KEY \
    -Dsonar.projectName=eDelphi \
    -Dsonar.projectVersion=$PROJECT_VERSION \
    -Dsonar.sources=edelphi/src,edelphi-persistence/src,itests/src,smvcj/src \
    -Dsonar.java.binaries=edelphi/target/classes,edelphi-persistence/target/classes,smvcj/target/classes \
    -Dsonar.java.source=1.8

elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "master" ]; then
  echo "Master build"
else
  echo "Push to branch" 	  
fi

