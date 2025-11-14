package techCorp.model;
import java.util.Objects;

import techCorp.model.Position;



public class Worker {
    private String name;
    private String surname;
    private String email;
    private String corpName;
    private Position position;
    private double salary;
    private EmploymentStatus status;

    public Worker(String name, String surname, String email, String corpName, Position position) {
        this(name, surname, email, corpName, position, position.getSalary(), EmploymentStatus.ACTIVE); 
    }

    public Worker(String name, String surname, String email, String corpName, Position position, double salary) {
        this(name, surname, email, corpName, position, salary, EmploymentStatus.ACTIVE);
    }

    public Worker(String name, String surname, String email, String corpName, Position position, double salary, EmploymentStatus status) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.corpName = corpName;
        this.position = position;
        this.salary = salary;
        this.status = status != null ? status : EmploymentStatus.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String newEmail) {
        this.email = newEmail;
        
    }
    public String getCorpName() {
        return corpName;

    }
    
    public Position getPosition() {
        return position;
    }
    public double getSalary() {
        return salary;
    }
    public EmploymentStatus getStatus(){
        return status;
    }
    public void setStatus(EmploymentStatus newStatus){
        this.status = newStatus;
    }



   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker)) return false;
        Worker worker = (Worker) o;
        return Objects.equals(email, worker.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", corpName='" + corpName + '\'' +
                ", position=" + position +
                ", salary=" + salary +
                '}';
    }





}

