package org.oneworldaccuracy.service.serviceImpl;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.dto.WorkItemResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.producer.WorkItemProducer;
import org.oneworldaccuracy.repository.WorkItemRepository;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class WorkItemServiceImpl implements WorkItemService {

    private WorkItemRepository workItemRepository;
    private WorkItemProducer workItemProducer;

    @Override
    @Async
    public WorkItem processWorkItem(WorkItem workItem) {
        // Calculate the square of the work item value
        int value = workItem.getValue();
        int result = value * value;

        // Introduce a simulated delay
        try {
            Thread.sleep(value * 10L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update the work item with the processing result
        workItem.setResult(result);
        workItem.setProcessed(true);
        return workItemRepository.save(workItem);
    }

    @Override
    public String createWorkItem(int value) {
        // Check if the value is within the valid range
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("Invalid request");
        }
        // Create a new WorkItem instance
        WorkItem workItem = new WorkItem();
        workItem.setValue(value);
        workItem.setProcessed(false);
        // Save the WorkItem to the repository
        WorkItem savedWorkItem = workItemRepository.save(workItem);
        // Send the WorkItem to a WorkItemProducer
        workItemProducer.sendWorkItem(savedWorkItem);
        // Return the ID of the saved WorkItem
        return savedWorkItem.getId();
    }

    @Override
    public WorkItemResponse getWorkItemById(String id) {
        // Retrieve the WorkItem from the repository based on the provided ID
        WorkItem workItem = workItemRepository.findById(id).orElse(null);
        // If the WorkItem is not found, return null
        if (workItem == null) {
            return null;
        }
        // Create and return a WorkItemResponse object with the relevant information from the retrieved WorkItem
        return new WorkItemResponse(workItem.getId(), workItem.getValue(),
                workItem.isProcessed(), workItem.getResult());
    }

    @Override
    public String deleteWorkItem(String id) {
        // Retrieve the optional WorkItem from the repository based on the provided ID
        Optional<WorkItem> optionalWorkItem = workItemRepository.findById(id);
        // If the optional WorkItem is present
        if (optionalWorkItem.isPresent()) {
            // Get the WorkItem from the optional
            WorkItem workItem = optionalWorkItem.get();
            // Check if the WorkItem is not yet processed
            if (!workItem.isProcessed()) {
                // Delete the WorkItem from the repository
                workItemRepository.deleteById(id);
                return "Work item with id: " + id + " is deleted successfully.";
            } else {
                return "Work item with id: " + id + " can't be deleted because it's already processed.";
            }
        }
        // Return a message indicating that the WorkItem was not found
        return "Work item with id: " + id + " not found.";
    }

    public ReportResponse generateReport() {
        // Retrieve all work items from the database
        List<WorkItem> workItems = workItemRepository.findAll();

        // Create a map to store the item values, item counts, and processed counts
        Map<Integer, Integer> itemCountsMap = new HashMap<>();
        Map<Integer, Integer> processedCountsMap = new HashMap<>();

        // Iterate through each work item
        for (WorkItem workItem : workItems) {
            int value = workItem.getValue();

            // Update the item counts map
            itemCountsMap.put(value, itemCountsMap.getOrDefault(value, 0) + 1);

            // Update the processed counts map if the work item is processed
            if (workItem.isProcessed()) {
                processedCountsMap.put(value, processedCountsMap.getOrDefault(value, 0) + 1);
            }
        }

        // Create lists to store the sorted item values, item counts, and processed counts
        List<Integer> workItemValues = new ArrayList<>(itemCountsMap.keySet());
        List<Integer> itemCounts = new ArrayList<>();
        List<Integer> processedCounts = new ArrayList<>();

        // Sort the item values in ascending order
        Collections.sort(workItemValues);

        // Populate the item counts and processed counts lists based on the sorted item values
        for (int value : workItemValues) {
            itemCounts.add(itemCountsMap.getOrDefault(value, 0));
            processedCounts.add(processedCountsMap.getOrDefault(value, 0));
        }

        // Create and return a new ReportResponse with the sorted item values, item counts, and processed counts
        return new ReportResponse(workItemValues, itemCounts, processedCounts);
    }


//    @Override
//    public ReportResponse generateReport() {
//        // Retrieve all work items from the database
//        List<WorkItem> workItems = workItemRepository.findAll();
//
//        // Initialize lists to store item values, item counts, and processed counts
//        List<Integer> workItemValues = new ArrayList<>();
//        List<Integer> itemCounts = new ArrayList<>();
//        List<Integer> processedCounts = new ArrayList<>();
//
//        // Iterate through each work item
//        for (WorkItem workItem : workItems) {
//            int value = workItem.getValue();
//
//            // Update the item values, item counts, and processed counts lists
//            if (!workItemValues.contains(value)) {
//                workItemValues.add(value);
//                itemCounts.add(0);
//                processedCounts.add(0);
//            }
//
//            int index = workItemValues.indexOf(value);
//            itemCounts.set(index, itemCounts.get(index) + 1);
//
//            if (workItem.isProcessed()) {
//                processedCounts.set(index, processedCounts.get(index) + 1);
//            }
//        }
//
//        // Create and return a new ReportResponse with the item values, item counts, and processed counts
//        return new ReportResponse(workItemValues, itemCounts, processedCounts);
//    }

    @Override
    public JasperPrint generateJasperPrint(ReportResponse report) throws Exception {
        // Create a map to hold the parameters required for the JasperReport
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("itemCounts", report.getItemCounts());
        parameters.put("processedCounts", report.getProcessedCounts());

        // Load the JasperReport template file
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass()
                .getResourceAsStream("/static/Blank-report.jrxml"));

        // Create an empty JRDataSource (JasperReports data source)
        JRDataSource dataSource = new JREmptyDataSource();

        // Fill the JasperReport with data and return the generated JasperPrint object
        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }

    public static byte[] exportToPdf(JasperPrint jasperPrint) throws Exception {
        // Export the JasperPrint object to a PDF byte array
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

}

