# RabbitMQAssignment



To queue the items using the tool, you can follow these steps:

Ensure that your Spring Boot application, with the implemented endpoints for creating work items, is running and accessible.

Open Postman and make sure you have the request collection set up with the "Create Work Item" request.

Click on the "Runner" button in the top navigation bar of Postman.

In the Collection Runner window, select the desired collection from the left sidebar.

Configure the collection runner settings:

Set "Iterations" to the desired number of items you want to queue. For example, if you want to queue 1000 items, set it to 1000.
Set "Delay" to an appropriate value to introduce a delay between each request, if needed. This can be useful to prevent overwhelming the server with simultaneous requests. For example, you can set a delay of 100 milliseconds to introduce a 100 ms delay between each request.
Make sure "Data" is set to "No data" since you don't need to provide specific data for each request.
Click on the "Start Run" button to start the collection run.

Postman will send the requests one by one, according to the configured settings in the collection runner. Each request will create a work item with a random value within the range of 1 to 10.

After the collection run is complete, you can check the responses and any associated data in the collection runner window to verify that the items have been successfully queued.

By using Postman's Collection Runner, you can automate the process of queuing multiple items by specifying the number of iterations and setting an optional delay between each request. This allows you to efficiently create a large number of work items via the API without overwhelming the server.
