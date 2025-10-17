import org.junit.jupiter.api.Test;
import model.CompanyStatistics;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;



public class TestCompanyStatistics {
    CompanyStatistics stats;

    @BeforeEach
    void setup() {
        String compString = "Test Company";
        long workerCount = 100;
        double avgSalary = 5500.50;
        String bestEarningWorker = "janek kowalski";

        stats = new CompanyStatistics(compString, workerCount, avgSalary, bestEarningWorker);
    }

    @Test
    void testGetCompanyName() {
        String name = stats.getCompanyName();
        assertEquals("Test Company", name);
    }

    @Test
    void testGetWorkerCount() {
        int count = stats.getWorkerCount();
        assertEquals(100, count);
    }

    @Test
    void testGetAvgSalary() {
        double avg = stats.getAvgSalary();
        assertEquals(5500.50, avg);
    }

    @Test
    void testGetBestEarningWorker() {
        String worker = stats.getBestEarningWorker();
        assertEquals("janek kowalski", worker);
    }

    @Test
    void testToString() {
        String expected = "Dane dla Test Company{" +
                "Ilość pracowników: 100'" +
                "Średnie wynagrodzenie: 5500.5'" +
                "Najlepiej zarabiający pracownik:janek kowalski}";
        assertEquals(expected, stats.toString());
    }
    
}
