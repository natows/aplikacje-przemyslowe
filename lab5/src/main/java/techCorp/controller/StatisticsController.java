package techCorp.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import techCorp.dto.CompanyStatisticsDTO;
import techCorp.model.*;
import techCorp.service.EmployeeService;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final EmployeeService employeeService;

    @Autowired
    public StatisticsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }



    @GetMapping("/salary/average")
    public ResponseEntity<Map<String,Double>> getAverageSalary(
            @RequestParam(required = false) String company) {
        
        double average;
        
        if (company != null) {
            average = employeeService.getCompanyWorkers(company)
                    .stream()
                    .mapToDouble(Worker::getSalary)
                    .average()
                    .orElse(0.0);
        } else {
            average = employeeService.averageSalary();
        }
        
        return ResponseEntity.ok(Map.of("averageSalary", average));
    }


    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStatisticsDTO> getCompanyStatistics(@PathVariable String companyName){
        Map<String, CompanyStatistics> companiesMap = employeeService.getCompanyStatistics();
        CompanyStatistics companyStats = companiesMap.get(companyName);
        if (companyStats == null ){
            return ResponseEntity.notFound().build();
        }

        CompanyStatisticsDTO dto = new CompanyStatisticsDTO(
                companyStats.getCompanyName(),
                companyStats.getWorkerCount(),
                companyStats.getAvgSalary(),
                companyStats.getBestEarningWorker()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/positions")
    public ResponseEntity<Map<String, Integer>> getPositionDistribution() {
        Map<Position, Long> positionCounts = employeeService.countByPosition();
        
        Map<String, Integer> result = positionCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(), 
                        entry -> entry.getValue().intValue()           
                ));
        
        return ResponseEntity.ok(result);
    }  

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getStatusDistribution() {
        Map<EmploymentStatus, Long> statusCounts = employeeService.countByStatus();

        Map<String,Integer> result = statusCounts.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().name(),
                    entry -> entry.getValue().intValue()
                ));
        return ResponseEntity.ok(result);

    }
}
