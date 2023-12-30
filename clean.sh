#!/bin/bash
echo Preparing project clean execution.
path=$(pwd)

gradlewclean() {
  cd $path/$1
  echo $1
  ./gradlew clean
}

yarnbuild() {
  cd $path/tasks-app
  yarn build
}

gradlewclean tasks-api
gradlewclean tasks-notification
gradlewclean tasks-discovery
gradlewclean tasks-gateway-server
#yarnbuild

echo Build of completed projects.