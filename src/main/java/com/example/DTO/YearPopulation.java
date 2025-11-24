package com.example.DTO;

public class YearPopulation{

    private int year;
    private PopulationBreakdown foreigners;
    private PopulationBreakdown luxembourgers;
    private long totalpopulation  = foreigners.getTotal() + luxembourgers.getTotal();
    
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

    

}