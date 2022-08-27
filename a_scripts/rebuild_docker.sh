#!/bin/sh
docker stop nebula-server
docker rm nebula-server
docker rmi maple.io/nebula-server
sh buildImages.sh
docker-compose up -d
