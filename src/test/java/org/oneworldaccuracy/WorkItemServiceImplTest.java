package org.oneworldaccuracy;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.PrintPageFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneworldaccuracy.dto.ReportResponse;
import org.oneworldaccuracy.dto.WorkItemResponse;
import org.oneworldaccuracy.model.WorkItem;
import org.oneworldaccuracy.producer.WorkItemProducer;
import org.oneworldaccuracy.repository.WorkItemRepository;
import org.oneworldaccuracy.service.serviceImpl.WorkItemServiceImpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkItemServiceImplTest {

    private WorkItemServiceImpl workItemService;

    @Mock
    private WorkItemRepository workItemRepository;

    @Mock
    private WorkItemProducer workItemProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workItemService = new WorkItemServiceImpl(workItemRepository, workItemProducer);
    }

    @Test
    void processWorkItem_ShouldCalculateSquareAndSaveWorkItem() {
        // Arrange
        WorkItem workItem = new WorkItem(5);
        int expectedResult = 25;

        // Act
        workItemService.processWorkItem(workItem);

        // Assert
        assertEquals(expectedResult, workItem.getResult());
        assertTrue(workItem.isProcessed());
        verify(workItemRepository, times(1)).save(workItem);
    }

    @Test
    void CreateWorkItem_WithValidValue_ShouldSaveWorkItemAndSendToProducer() {
        // Arrange
        int value = 5;
        String expectedId = "1";
        URI expectedLocation = URI.create("/workItems/1");

        // Act
        WorkItemResponse response = workItemService.CreateWorkItem(value).getResponse();

        // Assert
        assertNotNull(response);
        assertEquals(expectedId, response.getId());
        assertEquals(expectedLocation, workItemService.CreateWorkItem(value).getLocation());
        verify(workItemRepository, times(1)).save(any(WorkItem.class));
        verify(workItemProducer, times(1)).sendWorkItem(any(WorkItem.class));
    }

    @Test
    void CreateWorkItem_WithInvalidValue_ShouldThrowException() {
        // Arrange
        int value = 0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> workItemService.CreateWorkItem(value));
        verify(workItemRepository, never()).save(any(WorkItem.class));
        verify(workItemProducer, never()).sendWorkItem(any(WorkItem.class));
    }

    @Test
    void getWorkItemById_WithExistingId_ShouldReturnWorkItemResponse() {
        // Arrange
        String id = "1";
        WorkItem workItem = new WorkItem(5);
        workItem.setId(id);
        when(workItemRepository.findById(id)).thenReturn(Optional.of(workItem));
        WorkItemResponse expectedResponse = new WorkItemResponse(id, workItem.getValue(), workItem.isProcessed(), workItem.getResult());

        // Act
        WorkItemResponse response = workItemService.getWorkItemById(id);

        // Assert
        assertEquals(expectedResponse.getValue(), response.getValue());
        verify(workItemRepository, times(1)).findById(id);
    }

    @Test
    void getWorkItemById_WithNonExistingId_ShouldReturnNull() {
        // Arrange
        String id = "1";
        when(workItemRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        WorkItemResponse response = workItemService.getWorkItemById(id);

        // Assert
        assertNull(response);
        verify(workItemRepository, times(1)).findById(id);
    }

    @Test
    void deleteWorkItem_WithExistingIdAndNotProcessed_ShouldDeleteWorkItem() {
        // Arrange
        String id = "1";
        WorkItem workItem = new WorkItem(5);
        workItem.setId(id);
        when(workItemRepository.findById(id)).thenReturn(Optional.of(workItem));

        // Act
        String result = workItemService.deleteWorkItem(id);

        // Assert
        assertEquals("Work item with id: " + id + " is deleted successfully.", result);
        verify(workItemRepository, times(1)).findById(id);
        verify(workItemRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteWorkItem_WithExistingIdAndProcessed_ShouldReturnErrorMessage() {
        // Arrange
        String id = "1";
        WorkItem workItem = new WorkItem(5);
        workItem.setId(id);
        workItem.setProcessed(true);
        when(workItemRepository.findById(id)).thenReturn(Optional.of(workItem));

        // Act
        String result = workItemService.deleteWorkItem(id);

        // Assert
        assertEquals("Work item with id: " + id + " can't be deleted because it's already processed.", result);
        verify(workItemRepository, times(1)).findById(id);
        verify(workItemRepository, never()).deleteById(id);
    }

    @Test
    void deleteWorkItem_WithNonExistingId_ShouldReturnNotFoundErrorMessage() {
        // Arrange
        String id = "1";
        when(workItemRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        String result = workItemService.deleteWorkItem(id);

        // Assert
        assertEquals("Work item with id: " + id + " not found.", result);
        verify(workItemRepository, times(1)).findById(id);
        verify(workItemRepository, never()).deleteById(id);
    }

    @Test
    void generateReport_ShouldReturnReportResponseWithItemValuesAndCounts() {
        // Arrange
        List<WorkItem> workItems = new ArrayList<>();
        workItems.add(new WorkItem(5));
        workItems.add(new WorkItem(5));
        workItems.add(new WorkItem(10));
        when(workItemRepository.findAll()).thenReturn(workItems);
        List<Integer> expectedItemValues = List.of(5, 10);
        List<Integer> expectedItemCounts = List.of(2, 1);
        List<Integer> expectedProcessedCounts = List.of(0, 0);

        // Act
        ReportResponse reportResponse = workItemService.generateReport();

        // Assert
        assertNotNull(reportResponse);
        assertEquals(expectedItemValues, reportResponse.getWorkItemValues());
        assertEquals(expectedItemCounts, reportResponse.getItemCounts());
        assertEquals(expectedProcessedCounts, reportResponse.getProcessedCounts());
        verify(workItemRepository, times(1)).findAll();
    }

    @Test
    void generateJasperPrint_ShouldReturnJasperPrint() throws Exception {
        // Arrange
        ReportResponse reportResponse = new ReportResponse(
                List.of(5, 10),
                List.of(2, 1),
                List.of(0, 0)
        );

        // Act
        JasperPrint jasperPrint = workItemService.generateJasperPrint(reportResponse);

        // Assert
        assertNotNull(jasperPrint);
    }
}
