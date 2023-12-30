#!/bin/bash
echo Preparing project build execution.
path=$(pwd)

gradlewbuild() {
  cd $path/$1
  echo $1
  ./gradlew build
}

yarnbuild() {
  cd $path/tasks-app
  yarn build
}

gradlewbuild tasks-api
gradlewbuild tasks-notification
gradlewbuild tasks-discovery
gradlewbuild tasks-gateway-server
yarnbuild

echo Build of completed projects.