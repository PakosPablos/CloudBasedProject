package com.example.demo;

import com.example.DTO.PopulationBreakdown;
import com.example.DTO.YearPopulation;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class YearPopulationService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Data query URL – flat CSV with labels
    private static final String STATEC_CSV_URL =
            "https://lustat.statec.lu/rest/data/LU1,DF_B1100,1.0/.A" +
                    "?dimensionAtObservation=AllDimensions&format=csvfilewithlabels";

    // Split CSV on commas that are NOT inside quotes
    private static final Pattern CSV_SPLIT =
            Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    public List<YearPopulation> getPopulationForYear(int requestedYear) {

        // 1) Call Statec and get CSV as String
        String csv = restTemplate.getForObject(STATEC_CSV_URL, String.class);

        // 2) Parse CSV into year -> YearPopulation
        Map<Integer, YearPopulation> yearMap = parseCsvToYearPopulation(csv);

        // 3) If exact year exists -> list with 1 YearPopulation
        if (yearMap.containsKey(requestedYear)) {
            return List.of(yearMap.get(requestedYear));
        }

        // 4) Otherwise find closest before & after
        Integer beforeYear = null;
        Integer afterYear = null;

        for (Integer year : yearMap.keySet()) {
            if (year < requestedYear && (beforeYear == null || year > beforeYear)) {
                beforeYear = year;
            }
            if (year > requestedYear && (afterYear == null || year < afterYear)) {
                afterYear = year;
            }
        }

        List<YearPopulation> result = new ArrayList<>();
        if (beforeYear != null) result.add(yearMap.get(beforeYear));
        if (afterYear != null) result.add(yearMap.get(afterYear));

        return result;
    }

    // ------------ CSV PARSING ------------

    private Map<Integer, YearPopulation> parseCsvToYearPopulation(String csv) {
        Map<Integer, YearPopulation> result = new HashMap<>();
        if (csv == null || csv.isEmpty()) return result;

        String[] lines = csv.split("\\r?\\n");
        if (lines.length < 2) return result;

        // Debug header + first lines
        System.out.println("==== DEBUG: first CSV lines ====");
        for (int i = 0; i < Math.min(10, lines.length); i++) {
            System.out.println("CSV[" + i + "]: " + lines[i]);
        }
        System.out.println("==== END DEBUG ====");

        // --- 1) Header parsing ---
        String header = lines[0];
        String[] cols = CSV_SPLIT.split(header);

        int yearIndex = -1;
        int valueIndex = -1;
        int specIndexLabel = -1;

        for (int i = 0; i < cols.length; i++) {
            String col = cols[i].trim();
            String upper = col.toUpperCase();

            if (upper.equals("TIME_PERIOD")) {
                yearIndex = i;
            } else if (upper.equals("OBS_VALUE")) {
                valueIndex = i;
            } else if (col.equals("Specification")) { // human-readable label: "Total males", etc.
                specIndexLabel = i;
            }
        }

        System.out.println("Using columns: year=" + yearIndex +
                ", value=" + valueIndex + ", specLabel=" + specIndexLabel);

        if (yearIndex == -1 || valueIndex == -1 || specIndexLabel == -1) {
            System.err.println("Header not understood (need TIME_PERIOD, OBS_VALUE, Specification): " + header);
            return result;
        }

        // --- 2) Data lines ---
        for (int li = 1; li < lines.length; li++) {
            String line = lines[li].trim();
            if (line.isEmpty()) continue;

            String[] cells = CSV_SPLIT.split(line);
            if (cells.length <= Math.max(Math.max(yearIndex, valueIndex), specIndexLabel)) {
                continue;
            }

            String yearStr = cells[yearIndex].trim();      // e.g. "2015-12-31"
            String valueStr = cells[valueIndex].trim();    // "576 200" or empty
            String specLabel = cells[specIndexLabel].trim(); // e.g. "Total males"

            if (yearStr.isEmpty() || specLabel.isEmpty() || valueStr.isEmpty()) {
                continue; // skip "Not available" etc.
            }

            // Year is a DATE like 2015-12-31 → take first 4 chars
            String yearDigits = yearStr.substring(0, 4);
            int year;
            long value;
            try {
                year = Integer.parseInt(yearDigits);
                value = (long) Double.parseDouble(valueStr.replace(" ", ""));
            } catch (NumberFormatException e) {
                continue;
            }

            // --- 3) Get/create YearPopulation for that year ---
            YearPopulation yp = result.get(year);
            if (yp == null) {
                yp = new YearPopulation();
                yp.setYear(year);
                yp.setForeigners(new PopulationBreakdown());
                yp.setLuxembourgers(new PopulationBreakdown());
                yp.setTotalPopulation(0);
                yp.setTotalMales(0);
                yp.setTotalFemales(0);
                result.put(year, yp);
            }

            PopulationBreakdown lux = yp.getLuxembourgers();
            PopulationBreakdown fore = yp.getForeigners();

            // Normalise accents, then uppercase (handles "étrangères")
            String specNorm = Normalizer.normalize(specLabel, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            String specUpper = specNorm.toUpperCase(); // e.g. "LUXEMBOURGISH MALES"

            // --- 4) Match the row names from your table ---

            // "Total population"
            if (specUpper.startsWith("TOTAL POPULATION")) {
                yp.setTotalPopulation(value);
                continue;
            }

            // "Population per km²" -> ignore for this assignment
            if (specUpper.startsWith("POPULATION PER KM")) {
                continue;
            }

            // "Total males" / "Total females"
            if (specUpper.startsWith("TOTAL MALES")) {
                yp.setTotalMales(value);
                continue;
            }
            if (specUpper.startsWith("TOTAL FEMALES")) {
                yp.setTotalFemales(value);
                continue;
            }

            // "Luxembourgish males/females"
            if (specUpper.contains("LUXEMBOURGH") || specUpper.contains("LUXEMBOURGISH")) {
                if (specUpper.contains("MALE")) {
                    addToBreakdown(lux, true, false, value);
                    continue;
                }
                if (specUpper.contains("FEMALE")) {
                    addToBreakdown(lux, false, true, value);
                    continue;
                }
            }

            // "Foreign males" / "Femmes étrangères"
            if (specUpper.contains("FOREIGN")) {
                if (specUpper.contains("MALE")) {
                    addToBreakdown(fore, true, false, value);
                    continue;
                }
            }
            if (specUpper.contains("FEMMES ETRANGERES") || specUpper.contains("FEMME ETRANGERE")) {
                addToBreakdown(fore, false, true, value);
                continue;
            }

            // other rows are ignored
        }

        // --- 5) Fill missing totals from breakdowns, if possible ---
        for (YearPopulation yp : result.values()) {
            PopulationBreakdown lux = yp.getLuxembourgers();
            PopulationBreakdown fore = yp.getForeigners();

            if (lux == null) lux = new PopulationBreakdown();
            if (fore == null) fore = new PopulationBreakdown();

            // If total males/females missing, sum from nationality breakdowns
            if (yp.getTotalMales() == 0) {
                long males = lux.getMales() + fore.getMales();
                if (males > 0) yp.setTotalMales(males);
            }
            if (yp.getTotalFemales() == 0) {
                long females = lux.getFemales() + fore.getFemales();
                if (females > 0) yp.setTotalFemales(females);
            }

            // If total population missing, try sum of totals or males+females
            if (yp.getTotalPopulation() == 0) {
                long totalFromNation = lux.getTotal() + fore.getTotal();
                long totalFromSex = yp.getTotalMales() + yp.getTotalFemales();
                if (totalFromNation > 0) {
                    yp.setTotalPopulation(totalFromNation);
                } else if (totalFromSex > 0) {
                    yp.setTotalPopulation(totalFromSex);
                }
            }
        }

        System.out.println("Parsed years: " + result.keySet());
        return result;
    }

    private void addToBreakdown(PopulationBreakdown pb,
                                boolean isMale,
                                boolean isFemale,
                                long value) {
        if (pb == null) return;

        pb.setTotal(pb.getTotal() + value);
        if (isMale) {
            pb.setMales(pb.getMales() + value);
        }
        if (isFemale) {
            pb.setFemales(pb.getFemales() + value);
        }
    }
}
