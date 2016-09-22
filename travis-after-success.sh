#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ -n "${GITHUB_TOKEN}" ] && [ -n "${SONAR_TOKEN}" ]; then
  # Pull request
  
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "develop" ]; then
  # Develop build
  
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "master" ]; then
  # Master build
  
  # Change git -repository to  writeable

  eval `ssh-agent -s`
  ssh-add .travis_rsa
  git config user.name "Travis CI"
  git config user.email "travis@travis-ci.org"
  git remote set-url origin git@github.com:Metatavu/edelphi.git
  
  # Prepare Maven credentials for Sonatype 

  python travis-prepare-sonatype.py
  
  # Perform release
  
  mvn -B release:prepare release:perform --settings ~/.m2/mySettings.xml
  
  # Merge changed back to develop
  
  git checkout -B develop
  git merge master
  git push --set-upstream origin develop

else
  # Feature or hotfix build
  
fi

