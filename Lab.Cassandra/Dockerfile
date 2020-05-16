FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/*.jar app.jar
ENV JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=4444,server=y,suspend=n"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]