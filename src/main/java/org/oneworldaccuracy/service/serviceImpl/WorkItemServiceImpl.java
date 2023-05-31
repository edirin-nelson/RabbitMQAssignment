package org.oneworldaccuracy.service.serviceImpl;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.dto.WorkItemResult;
import org.oneworldaccuracy.dto.WorkItemResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.producer.WorkItemProducer;
import org.oneworldaccuracy.repository.WorkItemRepository;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkItemServiceImpl implements WorkItemService {

    private WorkItemRepository workItemRepository;
    private WorkItemProducer workItemProducer;

    @Override
    @Async
    public void processWorkItem(WorkItem workItem) {
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
        workItemRepository.save(workItem);
    }

    @Override
    public WorkItemResult CreateWorkItem(int value){
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("Work item value must be between 1 and 10.");
        }
        WorkItem workItem = new WorkItem(value);
        workItemRepository.save(workItem);
        workItemProducer.sendWorkItem(workItem);
        String id = workItem.getId();

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        WorkItemResponse response = new WorkItemResponse(id);
        return WorkItemResult.builder()
                .location(location)
                .response(response)
                .build();
    }

    @Override
    public WorkItemResponse getWorkItemById(String id) {
        WorkItem workItem = workItemRepository.findById(id).orElse(null);
        if (workItem == null) {
            return null;
        }
        return new WorkItemResponse(workItem.getId(), workItem.getValue(),
                workItem.isProcessed(), workItem.getResult());
    }

    @Override
    public String deleteWorkItem(String id) {
        Optional<WorkItem> optionalWorkItem = workItemRepository.findById(id);
        if (optionalWorkItem.isPresent()) {
            WorkItem workItem = optionalWorkItem.get();
            if (!workItem.isProcessed()) {
                workItemRepository.deleteById(id);
                return "Work item with id: " + id + " is deleted successfully.";
            } else {
                return "Work item with id: " + id + " can't be deleted because it's already processed.";
            }
        }
        return "Work item with id: " + id + " not found.";
    }


    @Override
    public ReportResponse generateReport() {
        // Retrieve all work items from the database
        List<WorkItem> workItems = workItemRepository.findAll();

        // Initialize maps to store item counts and processed counts
        Map<Integer, Integer> itemCounts = new HashMap<>();
        Map<Integer, Integer> processedCounts = new HashMap<>();

        // Iterate through each work item
        for (WorkItem workItem : workItems) {
            int value = workItem.getValue();

            // Update the item count in the itemCounts map
            itemCounts.put(value, itemCounts.getOrDefault(value, 0) + 1);

            // If the work item is processed, update the processed count in the processedCounts map
            if (workItem.isProcessed()) {
                processedCounts.put(value, processedCounts.getOrDefault(value, 0) + 1);
            }
        }

        // Create and return a new ReportResponse with the item counts and processed counts
        return new ReportResponse(itemCounts, processedCounts);
    }

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
