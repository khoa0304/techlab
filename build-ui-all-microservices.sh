#!/bin/bash

git pull

cd Lab.Common
pwd
mvn clean install

cd ..
pwd
mvn clean package spring-boot:repackage

docker-compose -f docker-compose-ui-all-microservices.yml up --build -d
