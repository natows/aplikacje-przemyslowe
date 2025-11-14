package techCorp.dto;

public class CompanyStatisticsDTO {
    private String companyName;
    private int employeeCount;
    private double averageSalary;
    private String topEarnerName;

    public CompanyStatisticsDTO() {
    }

    public CompanyStatisticsDTO(String companyName, int employeeCount, double averageSalary, String topEarnerName) {
        this.companyName = companyName;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.topEarnerName = topEarnerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public double getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(double averageSalary) {
        this.averageSalary = averageSalary;
    }

    public String getTopEarnerName() {
        return topEarnerName;
    }

    public void setTopEarnerName(String topEarnerName) {
        this.topEarnerName = topEarnerName;
    }
}