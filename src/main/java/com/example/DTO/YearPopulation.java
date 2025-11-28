package com.example.DTO;

public class YearPopulation {

    private int year;
    private PopulationBreakdown foreigners;
    private PopulationBreakdown luxembourgers;
    private long totalPopulation;
    private long totalMales;
    private long totalFemales;

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public PopulationBreakdown getForeigners() {
        return foreigners;
    }
    public void setForeigners(PopulationBreakdown foreigners) {
        this.foreigners = foreigners;
    }

    public PopulationBreakdown getLuxembourgers() {
        return luxembourgers;
    }
    public void setLuxembourgers(PopulationBreakdown luxembourgers) {
        this.luxembourgers = luxembourgers;
    }

    public long getTotalPopulation() {
        return totalPopulation;
    }
    public void setTotalPopulation(long totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public long getTotalMales() {
        return totalMales;
    }
    public void setTotalMales(long totalMales) {
        this.totalMales = totalMales;
    }

    public long getTotalFemales() {
        return totalFemales;
    }
    public void setTotalFemales(long totalFemales) {
        this.totalFemales = totalFemales;
    }
}

