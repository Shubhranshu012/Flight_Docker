FROM maven:3.8.5-openjdk-17
WORKDIR /bookingservive
COPY target/bookingservive-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
CMD ["java","-jar","app.jar","--spring.profiles.active=docker"]