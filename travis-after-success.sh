#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ -n "${GITHUB_TOKEN}" ] && [ -n "${SONAR_TOKEN}" ]; then
  echo "Pull request"
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "develop" ]; then
  echo "Develop build"
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "master" ]; then
  commitmessage=`git log --pretty=format:"%s" -1`;
  if [[ $commitmessage == *"[maven-release-plugin]"* ]]; then
    echo "Release build"
  else
    echo "Master build"
  
    set -e
  
    # Change git -repository to  writeable

    eval `ssh-agent -s`
    ssh-add .travis_rsa
    git remote set-url origin git@github.com:Metatavu/edelphi.git
    git config user.name "Travis CI"
    git config user.email "travis@travis-ci.org"
    git config --global push.default simple
    git checkout master
    git reset --hard
    git pull

    # Prepare Maven credentials for Sonatype 

    python travis-prepare-sonatype.py
  
    # Perform release
  
    mvn -q -Psonatype-oss-release -B release:prepare release:perform --settings ~/.m2/mySettings.xml
  
    # Merge changed back to develop
  
    git checkout -B develop
    git fetch
    git branch -u develop origin/develop
    git pull
    git merge master
    git push -u develop origin/develop
    
    set +e    
  fi

else
  echo "Feature or hotfix build"
fi
