#!/bin/bash
git pull

cd Lab.Common
pwd
mvn clean install

cd ..

cd Lab.Spark
mvn clean package

cd ..
pwd
mvn clean package spring-boot:repackage
docker-compose up --build 
