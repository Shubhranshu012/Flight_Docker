start /B "Eureka" java -jar eurekaserver\target\eurekaserver-0.0.1-SNAPSHOT.jar
start /B "Config" java -jar configserver\target\configserver-0.0.1-SNAPSHOT.jar
start /B "Gateway" java -jar apigateway\target\apigateway-0.0.1-SNAPSHOT.jar
start /B "Auth" java -jar authservice\target\authservice-0.0.1-SNAPSHOT.jar
start /B "Flight" java -jar flightservice\target\flightservice-0.0.1-SNAPSHOT.jar
start /B "Booking" java -jar bookingservice\target\bookingservive-0.0.1-SNAPSHOT.jar
