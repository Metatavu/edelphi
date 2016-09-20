#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ -n "${GITHUB_TOKEN}" ] && [ -n "${SONAR_TOKEN}" ]; then

  # It's a pull-request, run SonarQube analysis in the pull-request and execute tests

  sh sonar-scanner/bin/sonar-runner -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.analysis.mode=issues \
    -Dsonar.login=$SONAR_TOKEN \
    -Dsonar.projectKey=$SONAR_PROJECT_KEY \
    -Dsonar.github.oauth=$GITHUB_TOKEN \
    -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
    -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST
  set -e
  mvn clean verify -Pui -Dit.browser=phantomjs
  mvn jacoco:report coveralls:report -Pitests -DrepoToken=$COVERALLS_TOKEN
  set +e
fi

if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "develop" ]; then

  # Merge to develop, publish to SonarQube

  sh sonar-scanner/bin/sonar-runner -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.analysis.mode=publish \
    -Dsonar.login=$SONAR_TOKEN \
    -Dsonar.projectKey=$SONAR_PROJECT_KEY
  set -e
  mvn jacoco:report coveralls:report -Pitests -DrepoToken=$COVERALLS_TOKEN
  set +e
fi