package org.oneworldaccuracy.controller;

import lombok.AllArgsConstructor;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.dto.WorkItemRequest;
import org.oneworldaccuracy.dto.WorkItemResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.service.WorkItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
}

