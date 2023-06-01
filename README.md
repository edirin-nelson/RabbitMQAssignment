# RabbitMQAssignment

# Spring Boot Work Item Processing Application

This is a Spring Boot application that processes and reports work items asynchronously using a producer-consumer architecture with RabbitMQ messaging platform. The work items and their processing results are stored in a MongoDB database. The application provides a web page to display the work item report and allows generating a PDF report.

## Features

- Asynchronously processes work items
- Utilizes RabbitMQ messaging platform for communication between components
- Stores work items and their processing results in a MongoDB database
- Provides a web page to display the work item report
- Generates a PDF report with JasperReports
- Supports unit testing and integration testing

## Prerequisites

Before running the application, ensure that you have the following:

- Java Development Kit (JDK) installed (version 17 or higher)
- Apache Maven installed
- RabbitMQ server running
- MongoDB server running

## Build and Run Instructions
## Building from GitHub
Follow these steps to build and run the application from the source code on GitHub:

1. Clone the repository: git clone <repository_url>

2. Navigate to the project directory: cd spring-boot-work-item-processing
3. Build the application using Maven: mvn clean package
4. Configure the application:

- Update the RabbitMQ configuration in `src/main/resources/application.properties` if necessary.
- Configure the MongoDB connection settings in `src/main/resources/application.properties` if needed.

5. Run the application: java -jar target/spring-boot-work-item-processing.jar
6. Access the application:

Open a web browser and go to `http://localhost:8080/work-item/` to access the work item report page.

### Running from DockerHub

To run the application using a Docker image from DockerHub, follow these steps:

1. Pull the Docker image: docker pull neledirin/rabbitmq-assignment:<tag>
2. Run the Docker container: docker run -p 8080:8080 neledirin/rabbitmq-assignment:<tag>
3. Access the application:

Open a web browser and go to `http://localhost:8080/work-item/` to access the work item report page.

Note: Make sure you have Docker installed and running on your system before executing the above steps.

That's it! You can now build and run the application either from the source code on GitHub or using the Docker image from DockerHub. Choose the method that suits your requirements and environment.


## How to Use the Postman Tool to Queue the Items

1. Ensure that your Spring Boot application, with the implemented endpoints for creating work items, is running and accessible.

2. Open Postman and make sure you have the request collection set up with the "Create Work Item" request.

3. Click on the "Runner" button in the top navigation bar of Postman.

4. In the Collection Runner window, select the desired collection from the left sidebar.

5. Configure the collection runner settings:
   - Set "Iterations" to the desired number of items you want to queue. For example, if you want to queue 1000 items, set it to 1000.
   - Set "Delay" to an appropriate value to introduce a delay between each request, if needed. This can be useful to prevent overwhelming the server with simultaneous requests. For example, you can set a delay of 100 milliseconds to introduce a 100 ms delay between each request.
   - Make sure "Data" is set to "No data" since you don't need to provide specific data for each request.

6. Click on the "Start Run" button to start the collection run.

7. Postman will send the requests one by one, according to the configured settings in the collection runner. Each request will create a work item with a random value within the range of 1 to 10.

8. After the collection run is complete, you can check the responses and any associated data in the collection runner window to verify that the items have been successfully queued.

By using Postman's Collection Runner, you can automate the process of queuing multiple items by specifying the number of iterations and setting an optional delay between each request. This allows you to efficiently create a large number of work items via the API without overwhelming the server.

Please note that the above instructions assume you have set up the Spring Boot application and Postman correctly and have the necessary environment to execute the requests.
