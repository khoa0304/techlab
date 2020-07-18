#!/bin/bash

git pull

cd Lab.Spark
mvn clean package spring-boot:repackage

cd ..
cd Lab.Kafka
mvn clean package spring-boot:repackage

cd ..

docker-compose -f docker-compose-kafka-spark.yml up -d
