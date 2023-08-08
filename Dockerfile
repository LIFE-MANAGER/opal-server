FROM openjdk:11-jdk
LABEL maintainer="ldc991104@gmail.com"
ARG JAR_FILE=build/libs/opal-youth-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} docker-springboot.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-springboot.jar"]