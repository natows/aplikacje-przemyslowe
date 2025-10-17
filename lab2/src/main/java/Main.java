import service.EmployeeService;
import service.ImportService;
import service.APIService;
import model.Worker;
import model.ImportSummary;
import model.CompanyStatistics;
import exception.ApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== System Zarządzania Pracownikami ===\n");
        
        EmployeeService employeeService = new EmployeeService();
        
        System.out.println("--- 1. Import z pliku CSV ---");
        try {
            ImportService importService = new ImportService(employeeService);
            ImportSummary summary = importService.importFromCsv("src/test/resources/correct_employee.csv");
            
            System.out.println("Zaimportowano pracowników: " + summary.getImportedCount());
            System.out.println("Liczba błędów: " + summary.getErrorList().size());
            
            if (!summary.getErrorList().isEmpty()) {
                System.out.println("Błędy:");
                summary.getErrorList().forEach(error -> System.out.println("  - " + error));
            }
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku CSV: " + e.getMessage());
        }
        
        System.out.println("\n--- 2. Import z REST API ---");
        try {
            APIService apiService = new APIService();
            List<Worker> apiWorkers = apiService.fetchEmployeesFromAPI();
            
            System.out.println("Pobrano użytkowników z API: " + apiWorkers.size());
            
            int addedCount = 0;
            for (Worker worker : apiWorkers) {
                if (employeeService.addWorker(worker)) {
                    addedCount++;
                }
            }
            System.out.println(" Dodano nowych pracowników z API: " + addedCount);
            
            if (!apiWorkers.isEmpty()) {
                Worker firstApi = apiWorkers.get(0);
                System.out.println("\nPrzykładowy pracownik z API:");
                System.out.println("  - Imię: " + firstApi.getName());
                System.out.println("  - Nazwisko: " + firstApi.getSurname());
                System.out.println("  - Email: " + firstApi.getEmail());
                System.out.println("  - Firma: " + firstApi.getCorpName());
                System.out.println("  - Stanowisko: " + firstApi.getPosition());
                System.out.println("  - Pensja: " + firstApi.getSalary() + " PLN");
            }
            
        } catch (ApiException e) {
            System.err.println("Błąd API: " + e.getMessage());
        }
        
        System.out.println("\n--- 3. Statystyki pracowników ---");
        System.out.println("Łączna liczba pracowników: " + employeeService.getAllWorkers().size());
        System.out.println("Średnia pensja: " + String.format("%.2f", employeeService.averageSalary()) + " PLN");
        
        employeeService.highestSalary().ifPresent(worker -> 
            System.out.println("Najwyższa pensja: " + worker.getName() + " " + worker.getSurname() + 
                             " (" + worker.getSalary() + " PLN)")
        );
        
        System.out.println("\n--- 4. Walidacja spójności pensji ---");
        List<Worker> inconsistent = employeeService.validateSalaryConsistency();
        if (inconsistent.isEmpty()) {
            System.out.println("Wszystkie pensje są zgodne z bazowymi stawkami");
        } else {
            System.out.println("Znaleziono " + inconsistent.size() + " pracowników z pensją poniżej bazowej:");
            inconsistent.forEach(worker -> 
                System.out.println("  - " + worker.getName() + " " + worker.getSurname() + 
                                 " (" + worker.getPosition() + "): " + worker.getSalary() + 
                                 " PLN < " + worker.getPosition().getSalary() + " PLN")
            );
        }
        
        System.out.println("\n--- 5. Statystyki firm ---");
        Map<String, CompanyStatistics> companyStats = employeeService.getCompanyStatistics();
        
        companyStats.forEach((company, stats) -> {
            System.out.println("\n" + company + ":");
            System.out.println("  - Liczba pracowników: " + stats.getWorkerCount());
            System.out.println("  - Średnia pensja: " + String.format("%.2f", stats.getAvgSalary()) + " PLN");
            System.out.println("  - Najlepiej zarabiający: " + stats.getBestEarningWorker());
        });
        
        System.out.println("\n--- 6. Liczba pracowników według stanowisk ---");
        employeeService.countByPosition().forEach((position, count) -> 
            System.out.println("  - " + position + ": " + count + " osób")
        );
        
        System.out.println("\n=== Koniec demonstracji ===");
    }
}