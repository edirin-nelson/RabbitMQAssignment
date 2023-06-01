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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> saveWorkItem(@RequestBody WorkItemRequest request){
        return new ResponseEntity<>(workItemService.createWorkItem(
                request.getValue()), HttpStatus.CREATED);
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

    //Returns the report page
    @GetMapping("/report")
    @ApiOperation(value = "Get work item report", notes = "Retrieves a report containing item values, item counts, and processed counts.")
    public ModelAndView getReport(ModelAndView modelAndView) {
        return new ModelAndView("index", "report", workItemService.generateReport());
    }

    //Rest controller to get report
    @GetMapping("/api-report")
    @ApiOperation(value = "Get work item report", notes = "Retrieves a report containing item values, item counts, and processed counts.")
    public ResponseEntity<ReportResponse> getReport() {
        ReportResponse response = workItemService.generateReport();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/pdf")
    @ApiOperation(value = "Generate PDF report", notes = "Generates a PDF report based on the work item report.")
    public ResponseEntity<Resource> generatePdfReport() throws Exception {
        // Generate the report data
        ReportResponse report = workItemService.generateReport();

        // Generate the JasperPrint object using the report data
        JasperPrint jasperPrint = workItemService.generateJasperPrint(report);

        // Export the JasperPrint object to a byte array representing the PDF content
        byte[] pdfBytes = exportToPdf(jasperPrint);

        // Create a Resource object from the PDF byte array
        Resource resource = (Resource) new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        // Set the headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=work-item-report.pdf");

        // Return the ResponseEntity with the PDF content in the body and appropriate headers
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

}