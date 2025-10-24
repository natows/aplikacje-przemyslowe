package model;

import java.util.List;

public class ImportSummary {
    private int importedCount;
    private List<String> errorList; 
    public ImportSummary(int importedCount, List<String> errorList) {
        this.importedCount = importedCount;
        this.errorList = errorList;
    }

    public int getImportedCount() {
        return this.importedCount;
    }

    public List<String> getErrorList() {
        return this.errorList;
    }
    
}
