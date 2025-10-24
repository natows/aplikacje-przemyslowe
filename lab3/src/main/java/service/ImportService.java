package service;


import model.Position;
import model.Worker;
import model.ImportSummary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




public class ImportService {
    private EmployeeService workerSystem;

    public ImportService(EmployeeService workerSystem) {
        this.workerSystem = workerSystem;
    }

    public ImportSummary importFromCsv(String filepath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            int importedCount = 0;
            int lineNumber = 0;
            List<String> errorList = new ArrayList<>();

            reader.readLine();
            lineNumber++;

            while((line = reader.readLine()) != null){
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue; 
                }

                String[] row = line.split(",");

                String verificationError = verifyRow(row, lineNumber);
                if (verificationError != null) {
                    errorList.add(verificationError);
                    System.out.println("Błąd w wierszu " + lineNumber + ": " + verificationError);
                    continue;
                }

                try {
                    Worker worker = createWorker(row);
                    boolean added =  workerSystem.addWorker(worker);
                    if (added)  {
                        importedCount++;
                        System.out.println("Zaimportowano pracownika: " + worker);
                    }


                } catch (Exception e){
                    errorList.add("linia " + lineNumber + ": nieoczekiwany błąd: " + e.getMessage());
                }


            }
            return new ImportSummary(importedCount, errorList);

            
        }
    }



    private String verifyRow(String[] row, int rowNumber){
        if (row.length < 6) {
            return "linia " + rowNumber + ": niepełna liczba kolumn";
        }
        for (String field : row) {
            if (field == null || field.trim().isEmpty()) {
                return "linia " + rowNumber + ": pusta wartosc";
            }
        }
        if (!checkIfPositionExists(row[4].trim())){
            return "linia " + rowNumber + ": zła pozycja " + row[4];      
        }
        try {
            double salary = Double.parseDouble(row[5].trim());
            if (salary <= 0) {
                return "linia " + rowNumber + ": niepoprawna wartość pensji: " + row[5];
            }
        } catch (NumberFormatException e) {
            return "linia " + rowNumber + ": pensja nie jest liczba: " + row[5];
        }
        return null;


    }

    private boolean checkIfPositionExists(String positionName){
        try{
            Position.valueOf(positionName.toUpperCase()); //to zwroci cala stala wartosc z enum lub wyrzuci blad
            return true;
        } catch(IllegalArgumentException | NullPointerException e){
            return false;
        }

    }

    private Worker createWorker(String[] row){
        String firstName = row[0].trim();
        String lastName = row[1].trim();
        String email = row[2].trim();
        String company = row[3].trim();
        Position position = Position.valueOf(row[4].trim().toUpperCase());
        double salary = Double.parseDouble(row[5].trim());

        return new Worker(firstName, lastName, email, company, position, salary);
    }

    
}
