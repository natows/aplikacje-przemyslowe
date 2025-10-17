import service.ImportService;
import service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.ImportSummary;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

public class TestImportService {
    ImportService importService;
    EmployeeService employeeService;
    String validFilePath;
    String invalidFilePath;

    @BeforeEach
    void setup() {
        validFilePath = "src/test/resources/correct_employee.csv";
        invalidFilePath = "src/test/resources/error_employee.csv";
        employeeService = new EmployeeService();
        importService = new ImportService(employeeService);
    }


//     Linia 3: Pusta linia (powinna być pominięta)
// Linia 4: Nieistniejące stanowisko NIEISTNIEJACE_STANOWISKO
// Linia 5: Ujemna pensja -500.00
// Linia 6: Brak kolumny salary
// Linia 7: Puste lastName
// Linia 8: Puste firstName
// Linia 9: Pensja 0 (nie jest dodatnia)
// Linia 10: Pensja jako tekst dziewiec_tysiecy
    @Test 
    void testImportFromCSV_ValidFile() throws IOException {
        ImportSummary summary = importService.importFromCsv(validFilePath);
        assertEquals(10, summary.getImportedCount());
        assertEquals(0, summary.getErrorList().size());


    }

    @Test
    void testImportFromCSV_InvalidFile() throws IOException {
        ImportSummary summary = importService.importFromCsv(invalidFilePath);
        assertEquals(2, summary.getImportedCount());
        assertEquals(8, summary.getErrorList().size());
        assertEquals("linia 5: zła pozycja NIEISTNIEJACE_STANOWISKO", summary.getErrorList().get(0));
        assertEquals("linia 6: niepoprawna wartość pensji: -500.00", summary.getErrorList().get(1));
        assertEquals("linia 7: niepełna liczba kolumn", summary.getErrorList().get(2));
        assertEquals("linia 8: pusta wartosc", summary.getErrorList().get(3));
        assertEquals("linia 9: pusta wartosc", summary.getErrorList().get(4));
        assertEquals("linia 10: niepoprawna wartość pensji: 0", summary.getErrorList().get(5));
        assertEquals("linia 11: pensja nie jest liczba: dziewiec_tysiecy", summary.getErrorList().get(6));
    }
    
}
