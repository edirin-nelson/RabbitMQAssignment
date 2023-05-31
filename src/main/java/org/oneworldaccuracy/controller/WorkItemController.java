package org.oneworldaccuracy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperPrint;
import org.oneworldaccuracy.dto.*;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

import static org.oneworldaccuracy.service.serviceImpl.WorkItemServiceImpl.exportToPdf;

@AllArgsConstructor
@RestController
@RequestMapping("/work-items")
@Api(tags = "Work Items")
public class WorkItemController {

    private WorkItemService workItemService;

    @PostMapping
    @ApiOperation(value = "Create a work item", notes = "Creates a new work item and returns its ID.")
    public ResponseEntity<WorkItemUniqueID> createWorkItem(@RequestBody WorkItemRequest request) {
        WorkItemResult workItemResult = workItemService.CreateWorkItem(request.getValue());
        return ResponseEntity.created(workItemResult.getLocation()).body(new WorkItemUniqueID(workItemResult.getResponse().getId()));
    }

//    @GetMapping("/{id}")
//    @ApiOperation(value = "Get a work item", notes = "Retrieves a work item by its ID.")
//    public ResponseEntity<WorkItemResponse> getWorkItem(@PathVariable String id) {
//        WorkItem workItem = workItemService.getWorkItem(id);
//        if (workItem == null) {
//            return ResponseEntity.notFound().build();
//        }
//        WorkItemResponse response = new WorkItemResponse(workItem.getId(), workItem.getValue(),
//                workItem.isProcessed(), workItem.getResult());
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get a work item", notes = "Retrieves a work item by its ID.")
    public ResponseEntity<WorkItemResponse> getWorkItem(@PathVariable String id) {
        WorkItemResponse response = workItemService.getWorkItemById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a work item", notes = "Deletes a work item by its ID if it has not been processed.")
    public String deleteWorkItem(@PathVariable String id) {
        return workItemService.deleteWorkItem(id);
    }

    @GetMapping("/report")
    @ApiOperation(value = "Get work item report", notes = "Retrieves a report containing item counts and processed counts.")
    public ResponseEntity<ReportResponse> getReport() {
        ReportResponse response = workItemService.generateReport();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-report")
    @ApiOperation(value = "Get work item report", notes = "Retrieves a report containing item counts and processed counts.")
    public String getReport(Model model) {
        ReportResponse response = workItemService.generateReport();
        model.addAttribute("report", response);
        return "redirect: /index.html";
    }

    @GetMapping("/report/pdf")
    @ApiOperation(value = "Generate PDF report", notes = "Generates a PDF report based on the work item report.")
    public ResponseEntity<Resource> generatePdfReport() throws Exception {
        ReportResponse report = workItemService.generateReport();
        JasperPrint jasperPrint = workItemService.generateJasperPrint(report);

        byte[] pdfBytes = exportToPdf(jasperPrint);

        Resource resource = (Resource) new InputStreamResource(new ByteArrayInputStream(pdfBytes));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=work-item-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}

