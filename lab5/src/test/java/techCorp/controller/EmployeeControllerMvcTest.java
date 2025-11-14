package techCorp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import techCorp.dto.EmployeeDTO;
import techCorp.model.EmploymentStatus;
import techCorp.model.Position;
import techCorp.model.Worker;
import techCorp.service.EmployeeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Worker testWorker1;
    private Worker testWorker2;
    private Worker testWorker3;
    private EmployeeDTO testEmployeeDTO;

    @BeforeEach
    void setUp() {
        testWorker1 = new Worker("Jan", "Kowalski", "jan.kowalski@techcorp.com", 
                                 "TechCorp", Position.MANAGER, 12500);
        testWorker1.setStatus(EmploymentStatus.ACTIVE);
        
        testWorker2 = new Worker("Anna", "Nowak", "anna.nowak@techcorp.com", 
                                 "TechCorp", Position.PROGRAMISTA, 8500);
        testWorker2.setStatus(EmploymentStatus.ACTIVE);
        
        testWorker3 = new Worker("Piotr", "Wiśniewski", "piotr.wisniewski@innovate.com", 
                                 "InnovateLab", Position.PROGRAMISTA, 9000);
        testWorker3.setStatus(EmploymentStatus.ACTIVE);

        testEmployeeDTO = new EmployeeDTO(
            "Marek",
            "Nowak",
            "marek.nowak@example.com",
            "TestCorp",
            Position.PROGRAMISTA,
            8000.0,
            EmploymentStatus.ACTIVE
        );
    }

    @Test
    void testGetAllEmployees_ShouldReturn200AndJsonArray() throws Exception {
        // Given
        List<Worker> workers = Arrays.asList(testWorker1, testWorker2, testWorker3);
        when(employeeService.getAllWorkers()).thenReturn(workers);

        // When & Then
        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].firstName", is("Jan")))
                .andExpect(jsonPath("$[0].lastName", is("Kowalski")))
                .andExpect(jsonPath("$[0].email", is("jan.kowalski@techcorp.com")))
                .andExpect(jsonPath("$[0].company", is("TechCorp")))
                .andExpect(jsonPath("$[0].position", is("MANAGER")))
                .andExpect(jsonPath("$[0].salary", is(12500.0)))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")))
                .andExpect(jsonPath("$[1].firstName", is("Anna")))
                .andExpect(jsonPath("$[2].firstName", is("Piotr")));

        verify(employeeService, times(1)).getAllWorkers();
    }

    @Test
    void testGetEmployeeByEmail_ShouldReturn200AndWorkerData() throws Exception {
        // Given
        String email = "jan.kowalski@techcorp.com";
        when(employeeService.findByEmail(email)).thenReturn(Optional.of(testWorker1));

        // When & Then
        mockMvc.perform(get("/api/employees/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.lastName", is("Kowalski")))
                .andExpect(jsonPath("$.email", is("jan.kowalski@techcorp.com")))
                .andExpect(jsonPath("$.company", is("TechCorp")))
                .andExpect(jsonPath("$.position", is("MANAGER")))
                .andExpect(jsonPath("$.salary", is(12500.0)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(employeeService, times(1)).findByEmail(email);
    }

    @Test
    void testGetEmployeeByEmail_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        String email = "nonexistent@example.com";
        when(employeeService.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/employees/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).findByEmail(email);
    }

    @Test
    void testCreateEmployee_ShouldReturn201AndLocationHeader() throws Exception {
        // Given
        when(employeeService.addWorker(any(Worker.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/employees/marek.nowak@example.com")))
                .andExpect(jsonPath("$.firstName", is("Marek")))
                .andExpect(jsonPath("$.lastName", is("Nowak")))
                .andExpect(jsonPath("$.email", is("marek.nowak@example.com")));

        verify(employeeService, times(1)).addWorker(any(Worker.class));
    }

    @Test
    void testCreateEmployee_WhenDuplicate_ShouldReturn409() throws Exception {
        // Given
        when(employeeService.addWorker(any(Worker.class))).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDTO)))
                .andExpect(status().isConflict());

        verify(employeeService, times(1)).addWorker(any(Worker.class));
    }

    @Test
    void testCreateEmployee_WhenInvalidData_ShouldReturn400() throws Exception {
        // Given - DTO bez emaila
        EmployeeDTO invalidDTO = new EmployeeDTO(
            "Test",
            "Test",
            null, // brak emaila
            "TestCorp",
            Position.PROGRAMISTA,
            8000.0,
            EmploymentStatus.ACTIVE
        );

        // When & Then
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).addWorker(any(Worker.class));
    }

    @Test
    void testDeleteEmployee_ShouldReturn204() throws Exception {
        // Given
        String email = "jan.kowalski@techcorp.com";
        when(employeeService.deleteWorker(email)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/employees/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteWorker(email);
    }

    @Test
    void testDeleteEmployee_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        String email = "nonexistent@example.com";
        when(employeeService.deleteWorker(email)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/employees/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).deleteWorker(email);
    }

    @Test
    void testFilterByCompany_ShouldReturn200AndFilteredWorkers() throws Exception {
        // Given
        String companyName = "TechCorp";
        List<Worker> techCorpWorkers = Arrays.asList(testWorker1, testWorker2);
        when(employeeService.getCompanyWorkers(companyName)).thenReturn(techCorpWorkers);

        // When & Then
        mockMvc.perform(get("/api/employees")
                .param("company", companyName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].company", is("TechCorp")))
                .andExpect(jsonPath("$[1].company", is("TechCorp")))
                .andExpect(jsonPath("$[0].firstName", is("Jan")))
                .andExpect(jsonPath("$[1].firstName", is("Anna")));

        verify(employeeService, times(1)).getCompanyWorkers(companyName);
        verify(employeeService, never()).getAllWorkers();
    }

    @Test
    void testFilterByCompany_WhenNoWorkers_ShouldReturnEmptyArray() throws Exception {
        // Given
        String companyName = "NonExistentCorp";
        when(employeeService.getCompanyWorkers(companyName)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/employees")
                .param("company", companyName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(employeeService, times(1)).getCompanyWorkers(companyName);
    }

    @Test
    void testUpdateEmployee_ShouldReturn200() throws Exception {
        // Given
        String email = "jan.kowalski@techcorp.com";
        EmployeeDTO updatedDTO = new EmployeeDTO(
            "Jan",
            "Kowalski",
            email,
            "TechCorp",
            Position.MANAGER,
            15000.0,
            EmploymentStatus.ACTIVE
        );
        
        when(employeeService.findByEmail(email)).thenReturn(Optional.of(testWorker1));
        when(employeeService.updateWorker(eq(email), any(Worker.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/employees/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.salary", is(15000.0)));

        verify(employeeService, times(1)).findByEmail(email);
        verify(employeeService, times(1)).updateWorker(eq(email), any(Worker.class));
    }

    @Test
    void testUpdateEmployee_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        String email = "nonexistent@example.com";
        when(employeeService.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/employees/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployeeDTO)))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).findByEmail(email);
        verify(employeeService, never()).updateWorker(any(), any());
    }

    @Test
    void testGetEmployeesByStatus_ShouldReturn200() throws Exception {
        // Given
        EmploymentStatus status = EmploymentStatus.ACTIVE;
        List<Worker> activeWorkers = Arrays.asList(testWorker1, testWorker2, testWorker3);
        when(employeeService.getWorkersByStatus(status)).thenReturn(activeWorkers);

        // When & Then
        mockMvc.perform(get("/api/employees/status/{status}", status)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")))
                .andExpect(jsonPath("$[1].status", is("ACTIVE")))
                .andExpect(jsonPath("$[2].status", is("ACTIVE")));

        verify(employeeService, times(1)).getWorkersByStatus(status);
    }

    @Test
    void testGetEmployeesByStatus_WhenNoWorkers_ShouldReturnEmptyArray() throws Exception {
        // Given
        EmploymentStatus status = EmploymentStatus.ACTIVE;
        when(employeeService.getWorkersByStatus(status)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/employees/status/{status}", status)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(employeeService, times(1)).getWorkersByStatus(status);
    }
}