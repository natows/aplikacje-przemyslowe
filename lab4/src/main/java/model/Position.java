package model;

public enum Position {
    PREZES(25000,1),
    WICEPREZES(18000,2),
    MANAGER(12000,3),
    PROGRAMISTA(8000,4),
    STAŻYSTA(3000,5);

    private double salary;
    private int rank;

    Position(double salary, int rank) {
        this.salary = salary;
        this.rank = rank;
    }

    public double getSalary() {
        return salary;
    }

    public int getRank() {
        return rank;
    }


}
