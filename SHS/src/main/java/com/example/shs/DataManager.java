package com.example.shs;

import java.io.*;
import java.util.*;


public class DataManager {
    private static DataManager instance;
    private Map<String, Student> students;
    private List<University> universities;
    private Student currentStudent;

    private static final String DATA_DIRECTORY     = "data";
    private static final String STUDENTS_FILE      = "data/students.txt";
    private static final String UNIVERSITIES_FILE  = "data/universities.txt";
    private static final String SEPARATOR          = "|";
    private static final String LIST_SEPARATOR     = ",";

    private DataManager() {
        students     = new HashMap<>();
        universities = new ArrayList<>();
        createDataDirectory();
        loadData();
        if (universities.isEmpty()) {
            initializeUniversities();
            saveUniversities();
        }
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    private void createDataDirectory() {
        File dir = new File(DATA_DIRECTORY);
        if (!dir.exists()) dir.mkdirs();
    }

    // ─────────────────────────────────────────
    //  SAVE  students.txt
    // ─────────────────────────────────────────
    private void saveStudents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student s : students.values()) {
                String saved   = s.getSavedUniversities()   == null ? "" : String.join(LIST_SEPARATOR, s.getSavedUniversities());
                String applied = s.getAppliedUniversities() == null ? "" : String.join(LIST_SEPARATOR, s.getAppliedUniversities());

                pw.println(
                    s.getUsername()    + SEPARATOR +
                    s.getPassword()    + SEPARATOR +
                    s.getFullName()    + SEPARATOR +
                    s.getEmail()       + SEPARATOR +
                    s.getMatricMarks() + SEPARATOR +
                    s.getMatricTotal() + SEPARATOR +
                    s.getFscMarks()    + SEPARATOR +
                    s.getFscTotal()    + SEPARATOR +
                    s.getNtsMarks()    + SEPARATOR +
                    s.getNtsTotal()    + SEPARATOR +
                    s.getNetMarks()    + SEPARATOR +
                    s.getNetTotal()    + SEPARATOR +
                    saved              + SEPARATOR +
                    applied
                );
            }
            System.out.println("Saved " + students.size() + " students to students.txt");
        } catch (IOException e) {
            System.err.println("Error saving students.txt: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    //  LOAD  students.txt
    // ─────────────────────────────────────────
    private void loadStudents() {
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\" + SEPARATOR, -1);
                if (p.length < 14) continue;

                Student s = new Student(p[0], p[1], p[2], p[3]);
                s.setMatricMarks(parseDouble(p[4]));
                s.setMatricTotal(parseDouble(p[5]));
                s.setFscMarks(parseDouble(p[6]));
                s.setFscTotal(parseDouble(p[7]));
                s.setNtsMarks(parseDouble(p[8]));
                s.setNtsTotal(parseDouble(p[9]));
                s.setNetMarks(parseDouble(p[10]));
                s.setNetTotal(parseDouble(p[11]));

                // saved universities
                if (!p[12].trim().isEmpty()) {
                    s.setSavedUniversities(new ArrayList<>(Arrays.asList(p[12].split(LIST_SEPARATOR))));
                }
                // applied universities
                if (!p[13].trim().isEmpty()) {
                    s.setAppliedUniversities(new ArrayList<>(Arrays.asList(p[13].split(LIST_SEPARATOR))));
                }

                students.put(s.getUsername(), s);
            }
            System.out.println("Loaded " + students.size() + " students from students.txt");
        } catch (IOException e) {
            System.err.println("Error loading students.txt: " + e.getMessage());
            students = new HashMap<>();
        }
    }

    // ─────────────────────────────────────────
    //  SAVE  universities.txt
    // ─────────────────────────────────────────
    private void saveUniversities() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(UNIVERSITIES_FILE))) {
            for (University u : universities) {
                pw.println(
                    u.getName()             + SEPARATOR +
                    u.getLocation()         + SEPARATOR +
                    u.getType()             + SEPARATOR +
                    u.getMatricRequired()   + SEPARATOR +
                    u.getFscRequired()      + SEPARATOR +
                    u.getNtsRequired()      + SEPARATOR +
                    u.getMinAggregate()     + SEPARATOR +
                    u.getPrograms()         + SEPARATOR +
                    u.getMeritType().name()
                );
            }
            System.out.println("Saved " + universities.size() + " universities to universities.txt");
        } catch (IOException e) {
            System.err.println("Error saving universities.txt: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    //  LOAD  universities.txt
    // ─────────────────────────────────────────
    private void loadUniversities() {
        File file = new File(UNIVERSITIES_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\" + SEPARATOR, -1);
                if (p.length < 9) continue;

                University.MeritType mt;
                try { mt = University.MeritType.valueOf(p[8].trim()); }
                catch (Exception e) { mt = University.MeritType.GENERAL; }

                universities.add(new University(
                    p[0], p[1], p[2],
                    parseDouble(p[3]), parseDouble(p[4]), parseDouble(p[5]),
                    parseDouble(p[6]), p[7], mt
                ));
            }
            System.out.println("Loaded " + universities.size() + " universities from universities.txt");
        } catch (IOException e) {
            System.err.println("Error loading universities.txt: " + e.getMessage());
            universities = new ArrayList<>();
        }
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }

    // ─────────────────────────────────────────
    //  LOAD / SAVE (public)
    // ─────────────────────────────────────────
    private void loadData()  { loadStudents(); loadUniversities(); }
    public  void saveData()  { saveStudents(); saveUniversities(); }

    // ─────────────────────────────────────────
    //  SAVED / APPLIED  (persisted per student)
    // ─────────────────────────────────────────
    public void saveSavedUniversities(Student student, Set<String> uniNames) {
        student.setSavedUniversities(new ArrayList<>(uniNames));
        saveStudents();
    }

    public void saveAppliedUniversities(Student student, Set<String> uniNames) {
        student.setAppliedUniversities(new ArrayList<>(uniNames));
        saveStudents();
    }

    public Set<University> getSavedUniversities(Student student) {
        Set<University> result = new HashSet<>();
        if (student.getSavedUniversities() == null) return result;
        for (String name : student.getSavedUniversities()) {
            for (University u : universities) {
                if (u.getName().equals(name.trim())) { result.add(u); break; }
            }
        }
        return result;
    }

    public Set<University> getAppliedUniversities(Student student) {
        Set<University> result = new HashSet<>();
        if (student.getAppliedUniversities() == null) return result;
        for (String name : student.getAppliedUniversities()) {
            for (University u : universities) {
                if (u.getName().equals(name.trim())) { result.add(u); break; }
            }
        }
        return result;
    }

    // ─────────────────────────────────────────
    //  AUTH & REGISTRATION
    // ─────────────────────────────────────────
    public boolean authenticateUser(String username, String password) {
        Student student = students.get(username);
        if (student != null && student.getPassword().equals(password)) {
            currentStudent = student;
            return true;
        }
        return false;
    }

    public boolean registerUser(String username, String password, String fullName, String email) {
        if (students.containsKey(username)) return false;
        students.put(username, new Student(username, password, fullName, email));
        saveStudents();
        return true;
    }

    public void updateStudentProfile(Student student) {
        if (student != null && students.containsKey(student.getUsername())) {
            students.put(student.getUsername(), student);
            saveStudents();
        }
    }

    public void updateStudentMarks(Student student) {
        if (student != null && students.containsKey(student.getUsername())) {
            students.put(student.getUsername(), student);
            saveStudents();
        }
    }

    // ─────────────────────────────────────────
    //  UNIVERSITIES
    // ─────────────────────────────────────────
    private void initializeUniversities() {
        universities.add(new University("NUST", "Islamabad", "Public",
                60, 60, 50, 60.0,
                "Engineering, Computer Science, Business, Architecture",
                University.MeritType.NUST));

        universities.add(new University("UET Lahore", "Lahore", "Public",
                60, 60, 40, 55.0,
                "Electrical, Mechanical, Civil, Chemical Engineering",
                University.MeritType.UET));

        universities.add(new University("COMSATS University", "Islamabad/Lahore/Wah", "Public",
                50, 50, 50, 55.0,
                "CS, IT, Software Engineering, AI, Data Science, Electrical Engineering",
                University.MeritType.GENERAL));

        universities.add(new University("FAST-NUCES", "Islamabad/Lahore/Karachi", "Private",
                60, 60, 50, 60.0,
                "Computer Science, Software Engineering, Electrical Engineering, Data Science",
                University.MeritType.GENERAL));

        universities.add(new University("University of Punjab", "Lahore", "Public",
                45, 45, 40, 50.0,
                "Engineering, Medicine, Business, Arts, Social Sciences",
                University.MeritType.GENERAL));

        universities.add(new University("Quaid-i-Azam University", "Islamabad", "Public",
                50, 50, 40, 50.0,
                "Sciences, Social Sciences, Pharmacy, Biochemistry",
                University.MeritType.GENERAL));

        universities.add(new University("IBA Karachi", "Karachi", "Public",
                60, 65, 60, 65.0,
                "Business Administration, Computer Science, Economics",
                University.MeritType.GENERAL));

        universities.add(new University("University of Karachi", "Karachi", "Public",
                45, 45, 40, 45.0,
                "Arts, Sciences, Commerce, Pharmacy, Law",
                University.MeritType.GENERAL));

        universities.add(new University("LUMS", "Lahore", "Private",
                80, 80, 70, 75.0,
                "Business, CS, Economics, Social Sciences, Law",
                University.MeritType.GENERAL));

        universities.add(new University("University of Peshawar", "Peshawar", "Public",
                45, 45, 40, 48.0,
                "Arts, Sciences, Law, Islamic Studies, Medicine",
                University.MeritType.GENERAL));
    }

    public void addUniversity(University university) {
        universities.add(university);
        saveUniversities();
    }

    public boolean removeUniversity(String name) {
        boolean removed = universities.removeIf(u -> u.getName().equals(name));
        if (removed) saveUniversities();
        return removed;
    }

    public List<University> getEligibleUniversities(Student student) {
        List<University> eligible = new ArrayList<>();
        for (University u : universities) {
            if (u.isEligible(student)) eligible.add(u);
        }
        return eligible;
    }

    // ─────────────────────────────────────────
    //  EXPORT to CSV
    // ─────────────────────────────────────────
    public boolean exportStudentsToCSV(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Username,Full Name,Email,Matric,MatricTotal,FSC,FSCTotal,NTS,NTSTotal,NET,NETTotal,General Agg,NUST Agg,UET Agg");
            for (Student s : students.values()) {
                pw.printf("%s,%s,%s,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.2f,%.2f,%.2f%n",
                    s.getUsername(), s.getFullName(), s.getEmail(),
                    s.getMatricMarks(), s.getMatricTotal(),
                    s.getFscMarks(),   s.getFscTotal(),
                    s.getNtsMarks(),   s.getNtsTotal(),
                    s.getNetMarks(),   s.getNetTotal(),
                    s.getGeneralAggregate(), s.getNustAggregate(), s.getUetAggregate());
            }
            return true;
        } catch (IOException e) { return false; }
    }

    public boolean exportUniversitiesToCSV(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Name,Location,Type,MatricReq,FscReq,EntryTestReq,MinAggregate,Programs,MeritType");
            for (University u : universities) {
                pw.printf("%s,%s,%s,%.0f,%.0f,%.0f,%.0f,%s,%s%n",
                    u.getName(), u.getLocation(), u.getType(),
                    u.getMatricRequired(), u.getFscRequired(), u.getNtsRequired(),
                    u.getMinAggregate(), u.getPrograms(), u.getMeritType());
            }
            return true;
        } catch (IOException e) { return false; }
    }

    // ─────────────────────────────────────────
    //  BACKUP
    // ─────────────────────────────────────────
    public boolean backupData(String backupPath) {
        try {
            File dir = new File(backupPath);
            if (!dir.exists()) dir.mkdirs();
            copyFile(STUDENTS_FILE,     backupPath + "/students_backup.txt");
            copyFile(UNIVERSITIES_FILE, backupPath + "/universities_backup.txt");
            return true;
        } catch (IOException e) { return false; }
    }

    private void copyFile(String src, String dst) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(src));
             PrintWriter pw = new PrintWriter(new FileWriter(dst))) {
            String line;
            while ((line = br.readLine()) != null) pw.println(line);
        }
    }

    // ─────────────────────────────────────────
    //  STATS & MISC
    // ─────────────────────────────────────────
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", students.size());
        stats.put("totalUniversities", universities.size());
        int withMarks = 0;
        for (Student s : students.values()) {
            if (s.getMatricMarks() > 0 && s.getFscMarks() > 0) withMarks++;
        }
        stats.put("studentsWithCompleteMarks", withMarks);
        return stats;
    }

    public void shutdown() {
        saveData();
        System.out.println("Data saved. Shutting down.");
    }

    public Student getCurrentStudent()                  { return currentStudent; }
    public void setCurrentStudent(Student s)            { this.currentStudent = s; }
    public List<University> getUniversities()           { return universities; }
    public Map<String, Student> getStudents()           { return students; }
}
