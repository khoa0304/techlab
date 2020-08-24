FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN mkdir -p target
ADD target/lab-service-spark-0.0.1-SNAPSHOT.jar app.jar
ADD target/lab-service-spark-0.0.1-SNAPSHOT.jar.original target/lab-service-spark-0.0.1-SNAPSHOT.jar
#ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]