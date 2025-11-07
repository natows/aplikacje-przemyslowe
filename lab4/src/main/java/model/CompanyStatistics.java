package model;

public class CompanyStatistics {

    private String companyName;
    private long workerCount;
    private double avgSalary;
    private String bestEarningWorker;

    public CompanyStatistics(String companyName, long workerCount, double avgSalary, String bestEarningWorker) {
        this.companyName = companyName;
        this.workerCount = workerCount;
        this.avgSalary = avgSalary;
        this.bestEarningWorker = bestEarningWorker;
    }

    public String getCompanyName() {
        return companyName;
    }
    public int getWorkerCount() {
        return (int) workerCount;
    }
    public double getAvgSalary() {
        return avgSalary;
    }
    public String getBestEarningWorker() {
        return bestEarningWorker;
    }

    public String toString() {
        return "Dane dla " + companyName + "{" +
                "Ilość pracowników: " + workerCount + '\'' +
                "Średnie wynagrodzenie: " + avgSalary + '\'' +
                "Najlepiej zarabiający pracownik:" + bestEarningWorker + "}";

    }



}
