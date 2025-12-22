FROM maven:3.8.5-openjdk-17
WORKDIR /flightservice
COPY target/flightservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
CMD ["java","-jar","app.jar","--spring.profiles.active=docker"]