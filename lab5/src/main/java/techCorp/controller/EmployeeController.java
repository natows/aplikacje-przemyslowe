package techCorp.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import techCorp.dto.EmployeeDTO;
import techCorp.exception.DuplicateEmailException;
import techCorp.exception.EmployeeNotFoundException;
import techCorp.exception.InvalidDataException;
import techCorp.model.EmploymentStatus;
import techCorp.model.Worker;
import techCorp.service.EmployeeService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {


    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }



    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(
            @RequestParam(required = false) String company) {
        
        List<Worker> workers = (company != null) 
                ? employeeService.getCompanyWorkers(company)
                : employeeService.getAllWorkers();
        
        List<EmployeeDTO> employees = workers.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(employees);
    }


    private EmployeeDTO mapToDTO(Worker worker) {
        return new EmployeeDTO(
            worker.getName(),
            worker.getSurname(),
            worker.getEmail(),
            worker.getCorpName(),
            worker.getPosition(),
            worker.getSalary(),
            worker.getStatus() 
        );
    }


    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmail(@PathVariable String email) {
        Worker worker = employeeService.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(
                    "Pracownik z emailem '" + email + "' nie został znaleziony"));
        
        return ResponseEntity.ok(mapToDTO(worker));
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO){
        if (employeeDTO.getEmail() == null || employeeDTO.getEmail().trim().isEmpty()) {
            throw new InvalidDataException("Email nie może być pusty");
        }
        if (employeeDTO.getFirstName() == null || employeeDTO.getFirstName().trim().isEmpty()) {
            throw new InvalidDataException("Imię nie może być puste");
        }
        if (employeeDTO.getSalary() < 0) {
            throw new InvalidDataException("Wynagrodzenie nie może być ujemne");
        }

        Worker worker = mapToWorker(employeeDTO);
        if (!employeeService.addWorker(worker)) {
            throw new DuplicateEmailException(
                "Pracownik z emailem '" + employeeDTO.getEmail() + "' już istnieje");
            
        } 

        URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{email}")
                    .buildAndExpand(worker.getEmail())
                    .toUri();
        return ResponseEntity
                    .created(location)
                    .body(mapToDTO(worker));

    }

    private Worker mapToWorker(EmployeeDTO dto) {
        return new Worker(
            dto.getFirstName(),
            dto.getLastName(),
            dto.getEmail(),
            dto.getCompany(),
            dto.getPosition(),
            dto.getSalary(),
            dto.getStatus() != null ? dto.getStatus() : EmploymentStatus.ACTIVE
        );
    }

    @PutMapping("/{email}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String email, @RequestBody EmployeeDTO employeeDTO) {
        
        if (!employeeService.findByEmail(email).isPresent()) {
            throw new EmployeeNotFoundException(
            "Pracownik z emailem '" + email + "' nie został znaleziony");
        }

        if (employeeDTO.getSalary() < 0) {
            throw new InvalidDataException("Wynagrodzenie nie może być ujemne");
        }

        Worker updatedWorker = mapToWorker(employeeDTO);
        updatedWorker.setEmail(email); 
        
        if (employeeService.updateWorker(email, updatedWorker)) {
            return ResponseEntity.ok(mapToDTO(updatedWorker));
        }
        
       throw new RuntimeException("Nie udało się zaktualizować pracownika");
    }


    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        if (!employeeService.deleteWorker(email)) {
            throw new EmployeeNotFoundException(
                "Pracownik z emailem '" + email + "' nie został znaleziony"); 
        }
        return ResponseEntity.noContent().build(); 
    }

    // @PatchMapping("/{email}/status")
    // public ResponseEntity<EmployeeDTO> changeEmployeeStatus(
    //         @PathVariable String email, 
    //         @RequestBody String statusUpdate) {
        
    //     Worker worker = employeeService.findByEmail(email)
    //             .orElseThrow(() -> new EmployeeNotFoundException(
    //                 "Pracownik z emailem '" + email + "' nie został znaleziony"));
    //     if (statusUpdate)
    //     worker.setStatus(statusUpdate);
    //     return ResponseEntity.ok(mapToDTO(worker));
    // }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByStatus(@PathVariable EmploymentStatus status) {
        List<EmployeeDTO> employees = employeeService.getWorkersByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(employees);
    }
    
}
