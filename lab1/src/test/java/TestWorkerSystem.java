import org.junit.jupiter.api.BeforeEach;
import Worker.Worker;
import WorkerSystem.WorkerSystem;
import Position.Position;
import org.junit.jupiter.api.Test;
import java.util.List;


public class TestWorkerSystem {
    WorkerSystem system;
    Worker worker1;
    Worker worker2;
    Worker worker3;


    @BeforeEach
    void setUp() {
        system = new WorkerSystem();
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

    // @Test
    // void addNullWorker() {
    //     assert !system.addWorker(null);
    // }

    // @Test
    // void addWorkerWithNullEmail() {
    //     Worker workerWithNullEmail = new Worker("Jan", "Kowalski", null, "corp", Position.PROGRAMISTA);
    //     assert !system.addWorker(workerWithNullEmail);
    // }

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



    
}
