import service.ImportService;
import service.EmployeeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import model.ImportSummary;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.nio.file.Files;


public class TestImportService {

    @TempDir //katalog tymczasowy z jumit5
    Path tempDir;

    private Path createCsvFile(String fileName, List<String> lines) throws IOException {
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, lines);
        return filePath;
    }

    ImportService importService;
    EmployeeService employeeService;
    List<String> csvContentCorrectAll;
    List<String> csvContent8Wrong;
    Path csvFileCorrect;
    Path csvFile8Wrong;

    @BeforeEach
    void setup() throws IOException{
        employeeService = new EmployeeService();
        importService = new ImportService(employeeService);
        csvContentCorrectAll = List.of(
                "firstName,lastName,email,company,position,salary",
                "Jan,Kowalski,jan.kowalski@example.com,TechCorp,PROGRAMISTA,8000.00",
                "Anna,Nowak,anna.nowak@example.com,DataSystems,WICEPREZES,6000.00",
                "Piotr,Wiśniewski,piotr.wisniewski@example.com,TechCorp,MANAGER,7500.00",
                "Maria,Wójcik,maria.wojcik@example.com,CloudServices,PROGRAMISTA,8500.00",
                "Tomasz,Kowalczyk,tomasz.kowalczyk@example.com,DataSystems,MANAGER,9000.00",
                "Katarzyna,Kamińska,katarzyna.kaminska@example.com,TechCorp,STAŻYSTA,6200.00",
                "Michał,Lewandowski,michal.lewandowski@example.com,CloudServices,PROGRAMISTA,7800.00",
                "Agnieszka,Zielińska,agnieszka.zielinska@example.com,DataSystems,PROGRAMISTA,8200.00",
                "Krzysztof,Szymański,krzysztof.szymanski@example.com,TechCorp,PREZES,9500.00",
                "Magdalena,Woźniak,magdalena.wozniak@example.com,CloudServices,WICEPREZES,6100.00"
            );
        csvFileCorrect = createCsvFile("test_correct.csv", csvContentCorrectAll);


        csvContent8Wrong = List.of(
            "firstName,lastName,email,company,position,salary",
            "Jan,Kowalski,jan.kowalski@example.com,TechCorp,PROGRAMISTA,8000.00",
            "Anna,Nowak,anna.nowak@example.com,DataSystems,STAŻYSTA,6000.00",
            "",
            "Piotr,Wiśniewski,piotr.wisniewski@example.com,TechCorp,NIEISTNIEJACE_STANOWISKO,7500.00",
            "Maria,Wójcik,maria.wojcik@example.com,CloudServices,PROGRAMISTA,-500.00",
            "Tomasz,Kowalczyk,tomasz.kowalczyk@example.com,DataSystems,MANAGER",
            "Katarzyna,,katarzyna.kaminska@example.com,TechCorp,TESTER,6200.00",
            ",Lewandowski,michal.lewandowski@example.com,CloudServices,DEVOPS,7800.00",
            "Agnieszka,Zielińska,agnieszka.zielinska@example.com,DataSystems,PROGRAMISTA,0",
            "Krzysztof,Szymański,krzysztof.szymanski@example.com,TechCorp,MANAGER,dziewiec_tysiecy",
            "Magdalena,Woźniak,magdalena.wozniak@example.com,CloudServices,TESTER,6100.00"
        );
        csvFile8Wrong = createCsvFile("test_incorrect.csv", csvContent8Wrong);
    }



    @Test
    void shouldImportAllWorkersCorrectlyFromCorrectFileAndAssert10WorkersImported() throws IOException{
        ImportSummary summary = importService.importFromCsv(csvFileCorrect.toString());

        assertEquals(10, summary.getImportedCount());

    }

     @Test
        void shouldImportAllWorkersCorrectlyFromCorrectFileAndAssertWorkesAddedInSystem() throws IOException{
            
            importService.importFromCsv(csvFileCorrect.toString());

            assertEquals(10, employeeService.getAllWorkers().size());

        }


    @Test
    void shouldImportOnly2WorkersAndAssertAll8Issues() throws IOException {
       
        ImportSummary summary = importService.importFromCsv(csvFile8Wrong.toString());
        
        assertEquals(2, summary.getImportedCount());
        assertEquals(8, summary.getErrorList().size());
    
    
        assertAll("Weryfikacja wszystkich 8 błędów importu",
            () -> assertEquals("linia 5: zła pozycja NIEISTNIEJACE_STANOWISKO", 
                            summary.getErrorList().get(0)),
            () -> assertEquals("linia 6: niepoprawna wartość pensji: -500.00", 
                            summary.getErrorList().get(1)),
            () -> assertEquals("linia 7: niepełna liczba kolumn", 
                            summary.getErrorList().get(2)),
            () -> assertEquals("linia 8: pusta wartosc", 
                            summary.getErrorList().get(3)),
            () -> assertEquals("linia 9: pusta wartosc", 
                            summary.getErrorList().get(4)),
            () -> assertEquals("linia 10: niepoprawna wartość pensji: 0", 
                            summary.getErrorList().get(5)),
            () -> assertEquals("linia 11: pensja nie jest liczba: dziewiec_tysiecy", 
                            summary.getErrorList().get(6)),
            () -> assertEquals("linia 12: zła pozycja TESTER", 
                            summary.getErrorList().get(7)));

    }


    @Test
    void shouldImportOnly2WorkersAndAssert2WorkersInSystem() throws IOException {
       
        importService.importFromCsv(csvFile8Wrong.toString());
        assertEquals(2, employeeService.getAllWorkers().size());
    }
    
}
