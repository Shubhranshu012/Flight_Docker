FROM maven:3.8.5-openjdk-17
WORKDIR /apigateway
COPY target/apigateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8088
CMD ["java","-jar","app.jar","--spring.profiles.active=docker"]
