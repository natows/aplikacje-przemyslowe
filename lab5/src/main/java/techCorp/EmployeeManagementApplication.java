package techCorp;


import techCorp.model.CompanyStatistics;
import techCorp.model.ImportSummary;
import techCorp.model.Worker;
import techCorp.service.APIService;
import techCorp.service.EmployeeService;
import techCorp.service.ImportService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication implements CommandLineRunner {

    private final EmployeeService employeeService;
    private final ImportService importService;
    private final APIService apiService;
    private final List<Worker> xmlEmployees;

    public EmployeeManagementApplication(
            EmployeeService employeeService,
            ImportService importService,
            APIService apiService,
            @Qualifier("xmlEmployees") List<Worker> xmlEmployees) {
        this.employeeService = employeeService;
        this.importService = importService;
        this.apiService = apiService;
        this.xmlEmployees = xmlEmployees;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("SYSTEM ZARZĄDZANIA PRACOWNIKAMI - START");
        System.out.println("========================================\n");

        System.out.println("1. IMPORT PRACOWNIKÓW Z PLIKU CSV");
        System.out.println("----------------------------------");
        try {
            ImportSummary csvSummary = importService.importFromCsv("employees.csv");
            System.out.println("✓ Zaimportowano " + csvSummary.getImportedCount() + " pracowników z CSV");
            if (!csvSummary.getErrorList().isEmpty()) {
                System.out.println("⚠ Błędy podczas importu:");
                csvSummary.getErrorList().forEach(error -> System.out.println("  - " + error));
            }
        } catch (Exception e) {
            System.out.println("✗ Nie udało się zaimportować z CSV: " + e.getMessage());
        }
        System.out.println();

        System.out.println("2. DODAWANIE PRACOWNIKÓW Z KONFIGURACJI XML");
        System.out.println("-------------------------------------------");
        int xmlAdded = 0;
        for (Worker worker : xmlEmployees) {
            if (employeeService.addWorker(worker)) {
                xmlAdded++;
                System.out.println("✓ Dodano: " + worker.getName() + " " + worker.getSurname() 
                    + " (" + worker.getPosition() + ", " + worker.getCorpName() + ")");
            }
        }
        System.out.println("Dodano " + xmlAdded + "/" + xmlEmployees.size() + " pracowników z XML\n");

        System.out.println("3. POBIERANIE DANYCH Z REST API");
        System.out.println("--------------------------------");
        try {
            List<Worker> apiWorkers = apiService.fetchEmployeesFromAPI();
            int apiAdded = 0;
            for (Worker worker : apiWorkers) {
                if (employeeService.addWorker(worker)) {
                    apiAdded++;
                }
            }
            System.out.println("✓ Pobrano " + apiWorkers.size() + " pracowników z API");
            System.out.println("✓ Dodano " + apiAdded + " nowych pracowników do systemu");
        } catch (Exception e) {
            System.out.println("✗ Nie udało się pobrać danych z API: " + e.getMessage());
        }
        System.out.println();

        System.out.println("4. STATYSTYKI DLA FIRM");
        System.out.println("----------------------");
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        
        if (!stats.isEmpty()) {
            stats.forEach((companyName, stat) -> {
                System.out.println("\nFirma: " + companyName);
                System.out.println("  Liczba pracowników: " + stat.getWorkerCount());
                System.out.println("  Średnia pensja: " + String.format("%.2f", stat.getAvgSalary()));
                System.out.println("  Najlepiej zarabiający: " + stat.getBestEarningWorker());
            });
        } else {
            System.out.println("Brak danych o firmach");
        }
        System.out.println();

        System.out.println("5. WALIDACJA SPÓJNOŚCI WYNAGRODZEŃ");
        System.out.println("----------------------------------");
        List<Worker> inconsistentWorkers = employeeService.validateSalaryConsistency();
        
        if (inconsistentWorkers.isEmpty()) {
            System.out.println("✓ Wszystkie wynagrodzenia są zgodne z bazowymi stawkami");
        } else {
            System.out.println("⚠ Znaleziono " + inconsistentWorkers.size() 
                + " pracowników z wynagrodzeniem poniżej bazowej stawki:");
            
            for (Worker worker : inconsistentWorkers) {
                System.out.println("  - " + worker.getName() + " " + worker.getSurname() 
                    + " (" + worker.getPosition() + "): " 
                    + worker.getSalary() + " zł (bazowa: " 
                    + worker.getPosition().getSalary() + " zł)");
            }
        }
        System.out.println();

        System.out.println("6. PODSUMOWANIE");
        System.out.println("---------------");
        System.out.println("Łączna liczba pracowników w systemie: " + employeeService.getAllWorkers().size());
        System.out.println("Średnia pensja wszystkich pracowników: " + String.format("%.2f", employeeService.averageSalary()) + " zł");
        
        employeeService.highestSalary().ifPresent(worker -> 
            System.out.println("Najwyższa pensja: " + worker.getName() + " " + worker.getSurname() 
                + " - " + worker.getSalary() + " zł"));

        System.out.println("\n========================================");
        System.out.println("SYSTEM ZARZĄDZANIA PRACOWNIKAMI - KONIEC");
        System.out.println("========================================\n");
    }
}