import org.junit.jupiter.api.BeforeEach;
import model.Worker;
import service.EmployeeService;
import model.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import model.CompanyStatistics; 


public class TestEmployeeService {
    EmployeeService system;
    Worker worker1;
    Worker worker2;
    Worker worker3;


    @BeforeEach
    void setUp() {
        system = new EmployeeService();
        worker1 = new Worker("Pola", "W", "polamail.com", "MegaCorp", Position.PREZES);
        worker2 = new Worker("Ala", "K", "alamail.com", "MegaCorp", Position.MANAGER);
        worker3 = new Worker("Jan", "N", "janmail.com", "OtherCorp", Position.MANAGER);

    }


    @Test
    void shouldAddWorkerSuccessfullyWhenWorkerIsNew() {
        assertTrue(system.addWorker(worker1));
        assertEquals(1,system.getAllWorkers().size());        
    }

    @Test 
    void shouldReturnFalseWhenAddingDuplicateWorker(){
        system.addWorker(worker1);
        assertFalse(system.addWorker(worker1));
        assertEquals(1, system.getAllWorkers().size()); 

    }

    @Test
    void shouldReturnFalseWhenWorkersHaveSameEmail(){
        system.addWorker(worker1);
        Worker workerSameEmail = new Worker("nowy", "worker", "polamail.com", "jakas", Position.PREZES);
        assertFalse(system.addWorker(workerSameEmail));

    }


    @Test
    void shouldReturnEmptyListWhenNoWorkersAdded() {
        assertTrue(system.getAllWorkers().isEmpty());
        assertEquals(0, system.getAllWorkers().size());
    }

    @Test
    void checkIfAllWorkersWereAddedSuccesssfuly() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        
        List<Worker> workers = system.getAllWorkers();
        assertEquals(2, workers.size());
        assertTrue(workers.contains(worker1));
        assertTrue(workers.contains(worker2));
    }

    @Test
    void shouldReturnFalseWhenWorkerIsNull() {
        assertFalse(system.addWorker(null));
    }

   

    @Test
    void shouldReturnWorkersFromSpecificCompany() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        
        List<Worker> megaCorpWorkers = system.getCompanyWorkers("MegaCorp");
        assertEquals(2, megaCorpWorkers.size());
        assertTrue(megaCorpWorkers.containsAll(List.of(worker1, worker2)));
    }

    @Test
    void shouldReturnEmptyWorkerListWithNonexistenCompany() {
        system.addWorker(worker1);
        assertEquals(0,system.getCompanyWorkers("NonExistentCorp").size());
    }

    @Test
    void shouldReturnEmptyWorkerListWhenCompanyIsNull() {
        system.addWorker(worker1);
        assertEquals(0,system.getCompanyWorkers(null).size());
    }

    @Test 
    void shouldReturnEmptyWorkerListWhenCompanyIsEmptyString() {
        assertEquals(0,system.getCompanyWorkers("").size());
    }

    @Test
    void shouldSortWorkersBySurnameAlphabeticly() {
        system.addWorker(worker1); //W
        system.addWorker(worker2); //K
        system.addWorker(worker3); //N
        List<Worker> sorted = system.sortBySurname();
        assertEquals("K",sorted.get(0).getSurname());
        assertEquals("N",sorted.get(1).getSurname());
        assertEquals("W",sorted.get(2).getSurname());

    }

    @Test
    void shouldGroupWorkersByPosition() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        var grouped = system.groupByPosition();
        assertTrue(grouped.get(Position.PREZES).contains(worker1));
        assertEquals(1,grouped.get(Position.PREZES).size());
        assertTrue(grouped.get(Position.MANAGER).contains(worker2));
        assertTrue(grouped.get(Position.MANAGER).contains(worker3));
        assertEquals(2,grouped.get(Position.MANAGER).size());

    }

    @Test
    void shouldCountWorkersByPosition() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        var counted = system.countByPosition();
        assertEquals(1,counted.get(Position.PREZES));
        assertEquals(2,counted.get(Position.MANAGER));
    }

    @Test 
    void shouldCalculateAverageSalary() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        double avg = system.averageSalary();
        double expected = (25000.0 + 12000.0 * 2) /3.0;
        assertEquals(expected,avg);
    }

    @Test
    void shouldReturn0SalaryWhenNoWorkers() {
        double avg = system.averageSalary();
        assertEquals(0.0,avg);
    }

    @Test
    void shouldReturnWorkerWithHighestSalary() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        var highest = system.highestSalary();
        assertTrue(highest.isPresent());
        assertEquals(worker1, highest.get());
    }

    @Test
    void shouldReturnNoHighestSalaryWorkerWhenNoWorkers() {
        var highest = system.highestSalary();
        assertTrue(highest.isEmpty());
    }

    @Test
    void shouldReturnWrongSlaryWorkerList() {
        Worker wrongSalaryWorker1 = new Worker("Conchita", "Wurst", "conchitamail.com", "MegaCorp", Position.MANAGER, 9000.0);
        Worker wrongSalaryWorker2 = new Worker("Low", "Salary", "lowmail.com", "MegaCorp", Position.PREZES, 5000.0);

        system.addWorker(wrongSalaryWorker1);
        system.addWorker(wrongSalaryWorker2);
        system.addWorker(worker1);

        List<Worker> inconsistent = system.validateSalaryConsistency();
        
        assertEquals(2,inconsistent.size());
        assertTrue(inconsistent.contains(wrongSalaryWorker1));
        assertTrue(inconsistent.contains(wrongSalaryWorker2));
        assertFalse(inconsistent.contains(worker1));

    }

    @Test
    void shouldReturnCorrectCompanyStatistics() {
        system.addWorker(worker1);
        system.addWorker(worker2); 
        system.addWorker(worker3); 

        Map<String, CompanyStatistics> stats = system.getCompanyStatistics();

        assertEquals(2,stats.size());
        
        assertEquals(2,stats.get("MegaCorp").getWorkerCount());
        assertEquals(1,stats.get("OtherCorp").getWorkerCount());
        assertEquals((25000.0 + 12000.0) / 2.0, stats.get("MegaCorp").getAvgSalary());
        assertEquals(12000.0,stats.get("OtherCorp").getAvgSalary());
        assertEquals(worker1.getName() + " " + worker1.getSurname(),stats.get("MegaCorp").getBestEarningWorker() );
        assertEquals(worker3.getName() + " " + worker3.getSurname(),stats.get("OtherCorp").getBestEarningWorker());
        
    }
}
