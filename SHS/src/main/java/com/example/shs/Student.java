package com.example.shs;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {
    private static final long serialVersionUID = 2L;

    private String username;
    private String password;
    private String fullName;
    private String email;

    // Obtained marks
    private double matricMarks;
    private double fscMarks;
    private double ntsMarks;
    private double netMarks; // NUST NET score

    // Total marks
    private double matricTotal;
    private double fscTotal;
    private double ntsTotal;
    private double netTotal;

    // Persisted saved/applied universities (stored as names)
    private List<String> savedUniversities;
    private List<String> appliedUniversities;

    public Student(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.matricMarks = 0.0;
        this.fscMarks = 0.0;
        this.ntsMarks = 0.0;
        this.netMarks = 0.0;
        this.matricTotal = 1100.0;
        this.fscTotal = 1100.0;
        this.ntsTotal = 100.0;
        this.netTotal = 200.0;
    }

    // Percentage helpers
    public double getMatricPercent() {
        return matricTotal > 0 ? (matricMarks / matricTotal) * 100.0 : 0;
    }
    public double getFscPercent() {
        return fscTotal > 0 ? (fscMarks / fscTotal) * 100.0 : 0;
    }
    public double getNtsPercent() {
        return ntsTotal > 0 ? (ntsMarks / ntsTotal) * 100.0 : 0;
    }
    public double getNetPercent() {
        return netTotal > 0 ? (netMarks / netTotal) * 100.0 : 0;
    }

    // General aggregate (for COMSATS, UET etc): Matric 10% + FSC 40% + NTS 50%
    public double getGeneralAggregate() {
        return (getMatricPercent() * 0.10) + (getFscPercent() * 0.40) + (getNtsPercent() * 0.50);
    }

    // NUST aggregate: SSC 10% + FSC 15% + NET 75%
    public double getNustAggregate() {
        return (getMatricPercent() * 0.10) + (getFscPercent() * 0.15) + (getNetPercent() * 0.75);
    }

    // UET aggregate (ECAT): Matric 17% + FSC 50% + ECAT 33%
    public double getUetAggregate() {
        return (getMatricPercent() * 0.17) + (getFscPercent() * 0.50) + (getNtsPercent() * 0.33);
    }

    public boolean hasCompleteMarks() {
        return matricMarks > 0 && fscMarks > 0 && ntsMarks > 0;
    }

    public boolean hasNETMarks() {
        return netMarks > 0;
    }

    public double getAverageMarks() {
        if (!hasCompleteMarks()) return 0.0;
        return (getMatricPercent() + getFscPercent() + getNtsPercent()) / 3.0;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getFullName() { return fullName; }
    public void setFullName(String n) { this.fullName = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }

    public double getMatricMarks() { return matricMarks; }
    public void setMatricMarks(double v) { this.matricMarks = v; }
    public double getFscMarks() { return fscMarks; }
    public void setFscMarks(double v) { this.fscMarks = v; }
    public double getNtsMarks() { return ntsMarks; }
    public void setNtsMarks(double v) { this.ntsMarks = v; }
    public double getNetMarks() { return netMarks; }
    public void setNetMarks(double v) { this.netMarks = v; }

    public double getMatricTotal() { return matricTotal; }
    public void setMatricTotal(double v) { this.matricTotal = v; }
    public double getFscTotal() { return fscTotal; }
    public void setFscTotal(double v) { this.fscTotal = v; }
    public double getNtsTotal() { return ntsTotal; }
    public void setNtsTotal(double v) { this.ntsTotal = v; }
    public double getNetTotal() { return netTotal; }
    public void setNetTotal(double v) { this.netTotal = v; }

    public void setMarks(double matric, double fsc, double nts) {
        this.matricMarks = matric;
        this.fscMarks = fsc;
        this.ntsMarks = nts;
    }

    public List<String> getSavedUniversities()              { return savedUniversities; }
    public void setSavedUniversities(List<String> list)     { this.savedUniversities = list; }
    public List<String> getAppliedUniversities()            { return appliedUniversities; }
    public void setAppliedUniversities(List<String> list)   { this.appliedUniversities = list; }

    @Override
    public String toString() {
        return String.format("Student{username='%s', fullName='%s', matric=%.0f/%.0f, fsc=%.0f/%.0f, nts=%.0f/%.0f, net=%.0f/%.0f}",
                username, fullName, matricMarks, matricTotal, fscMarks, fscTotal, ntsMarks, ntsTotal, netMarks, netTotal);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student s = (Student) obj;
        return username.equals(s.username);
    }

    @Override
    public int hashCode() { return username.hashCode(); }
}
