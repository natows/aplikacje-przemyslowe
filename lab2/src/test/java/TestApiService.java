import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.ApiException;
import model.Worker;
import service.APIService;
import model.Position;

public class TestApiService {
    private APIService apiService;

    @BeforeEach
    void setup() {
        apiService = new APIService();
    }

    @Test
    void testFetchEmployeesFromAPI_Success() throws ApiException {
        List<Worker> workers = apiService.fetchEmployeesFromAPI();

        assertNotNull(workers, "Lista pracowników nie powinna być null");
        assertFalse(workers.isEmpty(), "Lista pracowników nie powinna być pusta");

        Worker firstWorker = workers.get(0);
        assertNotNull(firstWorker.getName(), "Imię nie powinno być null");
        assertNotNull(firstWorker.getEmail(), "Email nie powinien być null");
        assertEquals(Position.PROGRAMISTA, firstWorker.getPosition(), "Pozycja powinna być PROGRAMISTA");
        assertTrue(firstWorker.getSalary() > 0, "Wynagrodzenie powinno być większe od 0");
    }

    @Test
    void testFetchEmployeesFromAPI_AllAreProgrammers() throws ApiException {
        List<Worker> workers = apiService.fetchEmployeesFromAPI();

        for (Worker worker : workers) {
            assertEquals(Position.PROGRAMISTA, worker.getPosition(),
                    "Każdy pracownik z API powinien mieć stanowisko PROGRAMISTA");
        }
    }

    @Test
    void testFetchEmployeesFromAPI_AllHaveBaseSalary() throws ApiException {
        List<Worker> workers = apiService.fetchEmployeesFromAPI();
        double expectedSalary = Position.PROGRAMISTA.getSalary();

        for (Worker worker : workers) {
            assertEquals(expectedSalary, worker.getSalary(),
                    "Każdy pracownik z API powinien mieć bazową stawkę PROGRAMISTA");
        }
    }

    
    

}
