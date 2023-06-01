package org.oneworldaccuracy.service;

import net.sf.jasperreports.engine.JasperPrint;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.dto.WorkItemResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.springframework.scheduling.annotation.Async;

public interface WorkItemService {
    @Async
    void processWorkItem(WorkItem workItem);

    String createWorkItem(int value);

    WorkItemResponse getWorkItemById(String id);

    String deleteWorkItem(String id);

    ReportResponse generateReport();

    JasperPrint generateJasperPrint(ReportResponse report) throws Exception;
}

