# Basic_ATM
Basic ATM Functionality App with Java and Spring Boot

In order to run this application you will need to have Java and Maven configured in the system environment variables.
Also install Lombok plugin in IntelliJ

As described above this application was created to simulate a simple withdrawal transaction on an ATM.

Steps:

1. Open terminal in the root of the project and run the following commands: 
   mvn clean install ; 
   mvn spring-boot:run

2. Use Postman in order to test the request with the postman collection attached in the project
   You will find there an example of the request URL and request body needed.

3. Open h2 database console with the url: localhost:8080/h2-console
   Use the credentials and url from the "application.properties" file in the resources folder to access it and see the data.
   If you want to modify the data in the database go the "data.sql" file in the resources folder. Here you can find examples of insert statements to use.
