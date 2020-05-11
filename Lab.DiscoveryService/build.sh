#!/bin/bash
git pull

cd ../Lab.ConfigService
pwd
mvn clean package spring-boot:repackage

cd ../Lab.DiscoveryService
pwd
mvn clean package spring-boot:repackage
docker-compose up --build -d
