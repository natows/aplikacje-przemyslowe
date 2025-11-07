import model.ImportSummary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

public class TestImportSummary {
    ImportSummary summary;


    @BeforeEach
    void setup() {
        int importedWorkers = 10;
        List<String> errorList = List.of("Error on line 2", "Error on line 5");
        summary = new ImportSummary(importedWorkers, errorList);

    }

    @Test
    void testGetImportedWorkers() {
        assertEquals(10, summary.getImportedCount());
    }

    @Test
    void testGetErrorList() {
        List<String> errors = summary.getErrorList();
        assertEquals(2, errors.size());
        assertEquals("Error on line 2", errors.get(0));
        assertEquals("Error on line 5", errors.get(1));
    }


    
    

    
}
