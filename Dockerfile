FROM amazoncorretto:17
WORKDIR /app
COPY target/*.jar rabbitmqtask.jar
EXPOSE 8080
CMD ["java", "-jar", "rabbitmqtask.jar"]