package org.oneworldaccuracy.service;

import jakarta.xml.bind.JAXBException;
import net.sf.jasperreports.engine.JasperPrint;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.dto.WorkItemResponse;
import org.oneworldaccuracy.dto.WorkItemResult;
import org.oneworldaccuracy.model.WorkItem;
import org.springframework.scheduling.annotation.Async;

public interface WorkItemService {
    @Async
    void processWorkItem(WorkItem workItem);

    WorkItemResult CreateWorkItem(int value);

    WorkItemResponse getWorkItemById(String id);

    String deleteWorkItem(String id);

    ReportResponse generateReport();

    String generateReportXml() throws JAXBException;

    JasperPrint generateJasperPrint(ReportResponse report) throws Exception;
}

