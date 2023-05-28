package org.oneworldaccuracy.controller;

import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperPrint;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.dto.WorkItemRequest;
import org.oneworldaccuracy.dto.WorkItemResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/work-items")
public class WorkItemController {

    private WorkItemService workItemService;

    @PostMapping
    public ResponseEntity<WorkItemResponse> createWorkItem(@RequestBody WorkItemRequest request) {
        int value = request.getValue();
        String id = workItemService.createWorkItem(value);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        WorkItemResponse response = new WorkItemResponse(id);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkItemResponse> getWorkItem(@PathVariable String id) {
        WorkItem workItem = workItemService.getWorkItem(id);
        if (workItem == null) {
            return ResponseEntity.notFound().build();
        }
        WorkItemResponse response = new WorkItemResponse(workItem.getId(), workItem.getValue(),
                workItem.isProcessed(), workItem.getResult());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkItem(@PathVariable String id) {
        boolean deleted = workItemService.deleteWorkItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/report")
    public ResponseEntity<ReportResponse> getReport() {
        ReportResponse response = workItemService.generateReport();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<Resource> generatePdfReport() throws Exception {
        ReportResponse report = workItemService.generateReport();
        JasperPrint jasperPrint = workItemService.generateJasperPrint(report);

        byte[] pdfBytes = workItemService.exportToPdf(jasperPrint);

        Resource resource = (Resource) new InputStreamResource(new ByteArrayInputStream(pdfBytes));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=work-item-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}

