package WorkerSystem;

import Worker.Worker;
import Position.Position;

import java.util.*;
import java.util.stream.Collectors;




public class WorkerSystem {
    private Set<Worker> workers = new HashSet<>();



    // private boolean checkEmail(String email) {
    //     return workers.stream()
    //             .anyMatch(w -> w.getEmail().equals(email));
    // }

    // public boolean addWorker(Worker worker) {
    //     if (worker == null || worker.getEmail() == null) return false;
    //     boolean exists = checkEmail(worker.getEmail());
    //     if (!exists) {
    //         workers.add(worker); 
    //         System.out.println("Worker added: " + worker);   
    //         return true;
    //     }
    //     System.out.println("Worker with email " + worker.getEmail() + " already exists in the database.");
    //     return false;
    // }

    public boolean addWorker(Worker worker) {
        boolean added = workers.add(worker);
        if (added) {
            System.out.println("Added worker: " + worker);
        }else {
            System.out.println("Worker already exists: " + worker);
        }
        return added;
    }



    public List<Worker> getAllWorkers() {
        return new ArrayList<>(workers);
    }

    public List<Worker> getCompanyWorkers(String companyName) {
        if (companyName == null) {
            return Collections.emptyList();
        }
        return workers.stream()
                .filter(w -> companyName.equals(w.getCorpName()))
                .toList();
    }

    public List<Worker> sortBySurname() {
        return workers.stream()
                .sorted(Comparator.comparing(Worker::getSurname))
                .toList();

    }

    public Map<Position, List<Worker>> groupByPosition() {
        return workers.stream()
                .collect(Collectors.groupingBy(Worker::getPosition));


    }

    public Map<Position, Long> countByPosition() {
        return workers.stream()
                .collect(Collectors.groupingBy(Worker::getPosition, Collectors.counting()));    
    }


    public double averageSalary() {
        return workers.stream()
                .mapToDouble(Worker::getSalary)
                .average()
                .orElse(0.0);
        
    }


    public Optional<Worker> highestSalary() {
        return workers.stream()
                .max(Comparator.comparing(Worker::getSalary));
                
    }



    
    
}
