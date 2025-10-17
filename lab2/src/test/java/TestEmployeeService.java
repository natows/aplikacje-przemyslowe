import org.junit.jupiter.api.BeforeEach;
import model.Worker;
import service.EmployeeService;
import model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testAddWorker() {
        assert system.addWorker(worker1);
        assert !system.addWorker(worker1); 
        assert system.addWorker(worker2);
        assert !system.addWorker(worker2);
        
    }


    @Test
    void testGetAllWorkers() {
        assert system.getAllWorkers().isEmpty();
        system.addWorker(worker1);
        assert system.getAllWorkers().size() == 1;
        system.addWorker(worker2);
        assert system.getAllWorkers().size() == 2;
    }

    @Test
    void testGetCompanyWorkers() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        assert system.getCompanyWorkers("MegaCorp").size() == 2;
        assert system.getCompanyWorkers("OtherCorp").isEmpty();
        system.addWorker(worker3);
        assert system.getCompanyWorkers("OtherCorp").size() == 1;
        assert system.getCompanyWorkers(null).isEmpty();
    }

    @Test
    void testSortBySurname() {
        system.addWorker(worker1); //W
        system.addWorker(worker2); //K
        system.addWorker(worker3); //N
        List<Worker> sorted = system.sortBySurname();
        assert sorted.get(0).getSurname().equals("K");
        assert sorted.get(1).getSurname().equals("N");
        assert sorted.get(2).getSurname().equals("W");

    }

    @Test
    void groupByPosition() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        var grouped = system.groupByPosition();
        assert grouped.get(Position.PREZES).contains(worker1);
        assert grouped.get(Position.PREZES).size() == 1;
        assert grouped.get(Position.MANAGER).contains(worker2);
        assert grouped.get(Position.MANAGER).contains(worker3);
        assert grouped.get(Position.MANAGER).size() == 2;

    }

    @Test
    void countByPosition() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        var counted = system.countByPosition();
        assert counted.get(Position.PREZES) == 1;
        assert counted.get(Position.MANAGER) == 2;
    }

    @Test 
    void averageSalary() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        double avg = system.averageSalary();
        double expected = (25000.0 + 12000.0 * 2) /3.0;
        assert (avg == expected) : "Expected: " + expected + ", but got: " + avg;
    }

    @Test
    void highestSalary() {
        system.addWorker(worker1);
        system.addWorker(worker2);
        system.addWorker(worker3);
        var highest = system.highestSalary();
        assert highest.isPresent();
        assert highest.get().equals(worker1);
    }

    @Test
    void highestSalaryNoWorkers() {
        var highest = system.highestSalary();
        assert highest.isEmpty();
    }

    @Test
    void validateSalaryConsistency() {
        Worker wrongSalaryWorker1 = new Worker("Conchita", "Wurst", "conchitamail.com", "MegaCorp", Position.MANAGER, 9000.0);
        Worker wrongSalaryWorker2 = new Worker("Low", "Salary", "lowmail.com", "MegaCorp", Position.PREZES, 5000.0);

        system.addWorker(wrongSalaryWorker1);
        system.addWorker(wrongSalaryWorker2);
        system.addWorker(worker1);

        List<Worker> inconsistent = system.validateSalaryConsistency();
        
        assert inconsistent.size() == 2;
        assert inconsistent.contains(wrongSalaryWorker1);
        assert inconsistent.contains(wrongSalaryWorker2);
        assert !inconsistent.contains(worker1);

    }

    @Test
    void getCompanyStatistics() {
        system.addWorker(worker1);
        system.addWorker(worker2); 
        system.addWorker(worker3); 

        Map<String, CompanyStatistics> stats = system.getCompanyStatistics();

        assertEquals(stats.size(), 2);
        
        assertEquals(stats.get("MegaCorp").getWorkerCount(), 2);
        assertEquals(stats.get("OtherCorp").getWorkerCount(), 1);
        assertEquals(stats.get("MegaCorp").getAvgSalary(), (25000.0 + 12000.0) / 2.0);
        assertEquals(stats.get("OtherCorp").getAvgSalary(), 12000.0);
        assertEquals(stats.get("MegaCorp").getBestEarningWorker(), worker1.getName() + " " + worker1.getSurname());
        assertEquals(stats.get("OtherCorp").getBestEarningWorker(), worker3.getName() + " " + worker3.getSurname());





        
    }
}
