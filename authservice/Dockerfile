FROM maven:3.8.5-openjdk-17
WORKDIR /authservice
COPY target/authservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8001
CMD ["java","-jar","app.jar","--spring.profiles.active=docker"]
