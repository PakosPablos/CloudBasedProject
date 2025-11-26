package com.example.DTO;

public class YearPopulation {

    private int year;
    private PopulationBreakdown foreigners;
    private PopulationBreakdown luxembourgers;
    private long totalPopulation; // foreigners.total + luxembourgers.total

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

    // ✅ these two helper getters satisfy:
    // "number of males" and "number of females"
    public long getTotalMales() {
        long luxMales = luxembourgers == null ? 0 : luxembourgers.getMales();
        long forMales = foreigners == null ? 0 : foreigners.getMales();
        return luxMales + forMales;
    }

    public long getTotalFemales() {
        long luxFemales = luxembourgers == null ? 0 : luxembourgers.getFemales();
        long forFemales = foreigners == null ? 0 : foreigners.getFemales();
        return luxFemales + forFemales;
    }
}
