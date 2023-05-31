package org.oneworldaccuracy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperPrint;
import org.oneworldaccuracy.dto.*;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    @ApiOperation(value = "Get work item report", notes = "Retrieves a report containing item values, item counts, and processed counts.")
    public ModelAndView getReport(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        modelAndView.addObject("report", workItemService.generateReport());
        return modelAndView;
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