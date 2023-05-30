package org.oneworldaccuracy.service;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.producer.WorkItemProducer;
import org.oneworldaccuracy.repository.WorkItemRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkItemService {

    private WorkItemRepository workItemRepository;
    private WorkItemProducer workItemProducer;

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



    public String createWorkItem(int value) {
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("Work item value must be between 1 and 10.");
        }
        WorkItem workItem = new WorkItem(value);
        workItemRepository.save(workItem);
        workItemProducer.sendWorkItem(workItem);
        return workItem.getId();
    }

    public WorkItem getWorkItem(String id) {
        Optional<WorkItem> optional = workItemRepository.findById(id);
        return optional.orElse(null);
    }

    public boolean deleteWorkItem(String id) {
        Optional<WorkItem> optionalWorkItem = workItemRepository.findById(id);
        if (optionalWorkItem.isPresent()) {
            WorkItem workItem = optionalWorkItem.get();
            if (!workItem.isProcessed()) {
                workItemRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

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


    public JasperPrint generateJasperPrint(ReportResponse report) throws Exception {
        // Create a map to hold the parameters required for the JasperReport
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("itemCounts", report.getItemCounts());
        parameters.put("processedCounts", report.getProcessedCounts());

        // Load the report template file
        InputStream reportTemplateStream = getClass().getClassLoader().getResourceAsStream("reports/work-item-report-template.jrxml");

        // Compile the report template and generate a JasperReport object
        JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplateStream);

        // Create an empty JRDataSource (JasperReports data source)
        JRDataSource dataSource = new JREmptyDataSource();

        // Fill the JasperReport with data and return the generated JasperPrint object
        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }


    public byte[] exportToPdf(JasperPrint jasperPrint) throws Exception {
        // Export the JasperPrint object to a PDF byte array
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}

