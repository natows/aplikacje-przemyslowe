
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Worker.Worker;
import Position.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;



public class TestWorker {
    private Worker worker;

    @BeforeEach
    public void setUp() {
        String companyName = "TechCorp";
        worker = new Worker("Bartosz", "Kakol", "bartosz.kakol@proton.me", companyName, Position.PROGRAMISTA);
    }

    @Test
    public void testGetName() {
        assertEquals("Bartosz", worker.getName()); 
    }

    @Test
    public void testGetSurname() {
        assertEquals("Kakol", worker.getSurname());
    }

    @Test
    public void testGetEmail() {
        assertEquals("bartosz.kakol@proton.me", worker.getEmail());
    }

    @Test
    public void testGetCompanyName() {
        assertEquals("TechCorp", worker.getCorpName());
    }

    @Test
    public void testGetPosition(){
        assertEquals(Position.PROGRAMISTA, worker.getPosition());
    }

    @Test
    public void testGetSalary(){
        assertEquals(8000, worker.getSalary());
    }


    @Test
    public void testEquals() {
        Worker sameEmailWorker = new Worker("New", "Worker", "bartosz.kakol@proton.me", "TechCorp", Position.PROGRAMISTA);
        Worker differentEmailWorker = new Worker("New", "Worker", "innymail.com", "Techcorp", Position.PROGRAMISTA);
        assert sameEmailWorker.equals(worker);
        assert !differentEmailWorker.equals(worker);

    }

    @Test 
    public void testHashCode() {
        Worker sameEmailWorker = new Worker("New", "Worker", "bartosz.kakol@proton.me", "TechCorp", Position.PROGRAMISTA);
        assertEquals(sameEmailWorker.hashCode(), worker.hashCode());

    }


    
}
