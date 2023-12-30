echo Running docker containers
docker-compose up -d

echo Running front-end server
cd ./tasks-app
yarn preview