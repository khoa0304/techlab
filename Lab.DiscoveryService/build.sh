#!/bin/bash
git pull
mvn clean package spring-boot:repackage
docker-compose up --build -d
