#!/bin/bash

git pull

cd Lab.Kafka
mvn clean package spring-boot:repackage

cd ..

#docker-compose -f docker-compose-kafka-spark.yml up --build -d
docker-compose -f docker-compose-kafka-spark.yml up --build
