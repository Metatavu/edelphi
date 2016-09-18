#!/bin/bash

if [[ $it_browser = "phantomjs" ]]; then
  if [ ! -f itests/.phantomjs/bin/phantomjs ]; then
    rm -fR itests/.phantomjs
    curl -sSL "https://dl.dropboxusercontent.com/s/wz7o1jqclt8f4sy/phantomjs-2.1.1-linux-x86_64.tar.bz2"|tar -xvjC itests/
    mv itests/phantomjs-2.1.1-linux-x86_64 itests/.phantomjs
  fi;
fi;