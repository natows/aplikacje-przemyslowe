import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import model.Position;
public class TestPosition {
    
    @Test
    public void testCount(){
        assertEquals(5, Position.values().length);
    }

    @Test
    public void getSalary(){
        assertEquals(25000.0, Position.PREZES.getSalary());
        assertEquals(18000.0, Position.WICEPREZES.getSalary());
        assertEquals(12000.0, Position.MANAGER.getSalary());
        assertEquals(8000.0, Position.PROGRAMISTA.getSalary());
        assertEquals(3000.0, Position.STAŻYSTA.getSalary());
    }

    @Test
    public void getRank(){
        assertEquals(1, Position.PREZES.getRank());
        assertEquals(2, Position.WICEPREZES.getRank());
        assertEquals(3, Position.MANAGER.getRank());
        assertEquals(4, Position.PROGRAMISTA.getRank());
        assertEquals(5, Position.STAŻYSTA.getRank());
    }
}
