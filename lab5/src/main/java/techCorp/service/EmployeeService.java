package techCorp.service;

import org.springframework.stereotype.Service;

import techCorp.model.CompanyStatistics;
import techCorp.model.EmploymentStatus;
import techCorp.model.Position;
import techCorp.model.Worker;

import java.util.*;
import java.util.stream.Collectors;



@Service
public class EmployeeService {
    private Set<Worker> workers = new HashSet<>();


    public boolean addWorker(Worker worker) {
        if (worker == null) {
            return false;
        }
        boolean added = workers.add(worker);
        if (added) {
            System.out.println("Added worker: " + worker);
        }else {
            System.out.println("Worker already exists: " + worker);
        }
        return added;
    }


    public boolean updateWorker(String email, Worker updatedWorker) {
        Optional<Worker> existingWorker = findByEmail(email);
        if (existingWorker.isPresent()) {
            workers.remove(existingWorker.get());
            workers.add(updatedWorker);
            return true;
        }
        return false;
    }


    public boolean deleteWorker(String email) {
        return workers.removeIf(w -> w.getEmail().equals(email));
    }



    public List<Worker> getAllWorkers() {
        return new ArrayList<>(workers);
    }


    public Optional<Worker> findByEmail(String email) {
        return workers.stream()
                .filter(w -> w.getEmail().equals(email))
                .findFirst();
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

    public List<Worker> getWorkersByStatus(EmploymentStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return workers.stream()
                .filter(w -> status.equals(w.getStatus()))
                .collect(Collectors.toList());
    }

    

    public Map<Position, Long> countByPosition() {
        return workers.stream()
                .collect(Collectors.groupingBy(Worker::getPosition, Collectors.counting()));    
    }

    public Map<EmploymentStatus, Long> countByStatus(){
        return workers.stream()
                .collect(Collectors.groupingBy(Worker::getStatus, Collectors.counting()));
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

    public List<Worker> validateSalaryConsistency() {
        return workers.stream()
                .filter(w -> w.getSalary() < w.getPosition().getSalary())
                .collect(Collectors.toList());

    }

    public Map<String, CompanyStatistics> getCompanyStatistics() {
        return workers.stream()
                .collect(Collectors.groupingBy(Worker::getCorpName,
                        Collectors.collectingAndThen(Collectors.toList(),
                        companyWorkers -> {
                            long count = companyWorkers.size();
                            double avgSalary = companyWorkers.stream()
                                    .mapToDouble(Worker::getSalary)
                                    .average()
                                    .orElse(0.0);
                            Worker topEarner = companyWorkers.stream()
                                    .max(Comparator.comparingDouble(Worker::getSalary))
                                    .orElse(null);
                            
                            String topName = (topEarner != null)
                                    ? topEarner.getName() + " " + topEarner.getSurname()
                                    : "firma nie ma pracownikow";

                            String corpName = companyWorkers.get(0).getCorpName();

                            return new CompanyStatistics(corpName, count, avgSalary, topName);

                        })));

    }


    
    
}
