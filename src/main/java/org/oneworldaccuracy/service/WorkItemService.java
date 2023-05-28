package org.oneworldaccuracy.service;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.producer.WorkItemProducer;
import org.oneworldaccuracy.repository.WorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
            Thread.sleep(value * 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update the work item with the processing result
        workItem.setResult(result);
        workItem.setProcessed(true);
        workItemRepository.save(workItem);
    }

//    public void createWorkItem(int value) {
//        WorkItem workItem = new WorkItem(value);
//        workItemRepository.save(workItem);
//
//        workItemProducer.sendWorkItem(workItem);
//    }

    public String createWorkItem(int value) {
        WorkItem workItem = new WorkItem(value);
        workItemRepository.save(workItem);
        return workItem.getId();
    }

    public WorkItem getWorkItem(String id) {
        Optional<WorkItem> optional = workItemRepository.findById(id);
        return optional.orElse(null);
    }

    public boolean deleteWorkItem(String id) {
        if (workItemRepository.existsById(id)) {
            workItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public ReportResponse generateReport() {
        List<WorkItem> workItems = workItemRepository.findAll();
        Map<Integer, Integer> itemCounts = new HashMap<>();
        Map<Integer, Integer> processedCounts = new HashMap<>();

        for (WorkItem workItem : workItems) {
            int value = workItem.getValue();
            itemCounts.put(value, itemCounts.getOrDefault(value, 0) + 1);
            if (workItem.isProcessed()) {
                processedCounts.put(value, processedCounts.getOrDefault(value, 0) + 1);
            }
        }

        return new ReportResponse(itemCounts, processedCounts);
    }

    public JasperPrint generateJasperPrint(ReportResponse report) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("itemCounts", report.getItemCounts());
        parameters.put("processedCounts", report.getProcessedCounts());

        ClassLoader classLoader = getClass().getClassLoader();
        String reportTemplatePath = classLoader.getResource("reports/work-item-report-template.jrxml").getFile();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplatePath);

        JRDataSource dataSource = new JREmptyDataSource();
        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }

    public byte[] exportToPdf(JasperPrint jasperPrint) throws Exception {
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}

