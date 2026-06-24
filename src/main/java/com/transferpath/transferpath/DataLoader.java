package com.transferpath.transferpath;

import com.transferpath.transferpath.university.Program;
import com.transferpath.transferpath.university.ProgramRepository;
import com.transferpath.transferpath.university.University;
import com.transferpath.transferpath.university.UniversityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Path UNIVERSITIES_CSV =
            Path.of("data", "processed", "universities_clean.csv");

    private static final Path PROGRAMS_CSV =
            Path.of("data", "processed", "programs_clean.csv");

    private final UniversityRepository universityRepository;
    private final ProgramRepository programRepository;

    public DataLoader(
            UniversityRepository universityRepository,
            ProgramRepository programRepository
    ) {
        this.universityRepository = universityRepository;
        this.programRepository = programRepository;
    }

    @Override
    public void run(String... args) {
        if (!Files.exists(UNIVERSITIES_CSV) || !Files.exists(PROGRAMS_CSV)) {
            return;
        }

        Map<String, University> universitiesByName = importUniversities();
        importPrograms(universitiesByName);
    }

    private Map<String, University> importUniversities() {
        Map<String, University> universitiesByName = loadExistingUniversitiesByName();

        List<Map<String, String>> rows = readCsv(UNIVERSITIES_CSV);

        for (Map<String, String> row : rows) {
            String name = row.get("name");

            if (universitiesByName.containsKey(normalizeKey(name))) {
                continue;
            }

            University university = new University(
                    name,
                    row.get("country"),
                    parseDouble(row.get("min_gpa")),
                    row.get("state"),
                    row.get("city"),
                    row.get("application_deadline"),
                    row.get("website_url"),
                    parseBoolean(row.get("accepts_international")),
                    parseBoolean(row.get("accepts_fall")),
                    parseBoolean(row.get("accepts_spring")),
                    row.get("application_portal"),
                    row.get("transfer_requirements")
            );

            University savedUniversity = universityRepository.save(university);
            universitiesByName.put(normalizeKey(savedUniversity.getName()), savedUniversity);
        }

        return universitiesByName;
    }

    private void importPrograms(Map<String, University> universitiesByName) {
        List<Map<String, String>> rows = readCsv(PROGRAMS_CSV);

        for (Map<String, String> row : rows) {
            University university = universitiesByName.get(normalizeKey(row.get("university_name")));

            if (university == null) {
                throw new IllegalStateException(
                        "Program references unknown university: " + row.get("university_name")
                );
            }

            if (programAlreadyExists(university, row.get("major_name"), row.get("degree_level"))) {
                continue;
            }

            Program program = new Program(
                    row.get("major_name"),
                    row.get("degree_level"),
                    parseDouble(row.get("estimated_annual_cost")),
                    parseDouble(row.get("minimum_recommended_gpa")),
                    row.get("competitiveness_level"),
                    row.get("program_url"),
                    university
            );

            programRepository.save(program);
        }
    }

    private Map<String, University> loadExistingUniversitiesByName() {
        Map<String, University> universitiesByName = new HashMap<>();

        for (University university : universityRepository.findAll()) {
            universitiesByName.put(normalizeKey(university.getName()), university);
        }

        return universitiesByName;
    }

    private boolean programAlreadyExists(
            University university,
            String majorName,
            String degreeLevel
    ) {
        return programRepository.findByUniversityId(university.getId())
                .stream()
                .anyMatch(program ->
                        normalizeKey(program.getMajorName()).equals(normalizeKey(majorName))
                                && normalizeKey(program.getDegreeLevel()).equals(normalizeKey(degreeLevel))
                );
    }

    private List<Map<String, String>> readCsv(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);

            if (lines.isEmpty()) {
                return List.of();
            }

            List<String> headers = parseCsvLine(lines.get(0));
            List<Map<String, String>> rows = new ArrayList<>();

            for (int index = 1; index < lines.size(); index++) {
                String line = lines.get(index);

                if (line.isBlank()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);
                Map<String, String> row = new HashMap<>();

                for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                    String header = headers.get(columnIndex);
                    String value = columnIndex < values.size() ? values.get(columnIndex) : "";
                    row.put(header, value);
                }

                rows.add(row);
            }

            return rows;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read CSV file: " + path, exception);
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean insideQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char currentCharacter = line.charAt(index);

            if (currentCharacter == '"') {
                insideQuotes = !insideQuotes;
            } else if (currentCharacter == ',' && !insideQuotes) {
                values.add(currentValue.toString().trim());
                currentValue.setLength(0);
            } else {
                currentValue.append(currentCharacter);
            }
        }

        values.add(currentValue.toString().trim());

        return values;
    }

    private String normalizeKey(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toLowerCase();
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Double.parseDouble(value);
    }

    private Boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return Boolean.parseBoolean(value);
    }
}