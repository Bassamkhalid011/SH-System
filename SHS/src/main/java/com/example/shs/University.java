package com.example.shs;

import java.io.Serializable;

public class University implements Serializable {
    private static final long serialVersionUID = 2L;

    public enum MeritType { GENERAL, NUST, UET }

    private String name;
    private String location;
    private String type;
    private double matricRequired;   // minimum % required
    private double fscRequired;
    private double entryTestRequired;
    private double minAggregate;     // minimum aggregate % to be eligible
    private String programs;
    private MeritType meritType;

    public University(String name, String location, String type,
                      double matricReq, double fscReq, double entryTestReq,
                      double minAggregate, String programs, MeritType meritType) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.matricRequired = matricReq;
        this.fscRequired = fscReq;
        this.entryTestRequired = entryTestReq;
        this.minAggregate = minAggregate;
        this.programs = programs;
        this.meritType = meritType;
    }

    // Legacy constructor for backward compatibility
    public University(String name, String location, String type,
                      double matric, double fsc, double nts, String programs) {
        this(name, location, type, matric, fsc, nts, 50.0, programs, MeritType.GENERAL);
    }

    public double calculateAggregate(Student s) {
        switch (meritType) {
            case NUST:   return s.getNustAggregate();
            case UET:    return s.getUetAggregate();
            default:     return s.getGeneralAggregate();
        }
    }

    public boolean isEligible(Student s) {
        // Basic percentage requirements
        if (s.getMatricPercent() < matricRequired) return false;
        if (s.getFscPercent() < fscRequired) return false;
        // Entry test check
        if (meritType == MeritType.NUST) {
            if (!s.hasNETMarks()) return false;
            if (s.getNetPercent() < entryTestRequired) return false;
        } else {
            if (s.getNtsPercent() < entryTestRequired) return false;
        }
        return calculateAggregate(s) >= minAggregate;
    }

    // Legacy method kept for compatibility
    public boolean isEligible(double matricMarks, double fscMarks, double ntsMarks) {
        return matricMarks >= matricRequired && fscMarks >= fscRequired && ntsMarks >= entryTestRequired;
    }

    public double getEligibilityPercentage(double m, double f, double n) {
        return (Math.min(100, (m / matricRequired) * 100) +
                Math.min(100, (f / fscRequired) * 100) +
                Math.min(100, (n / entryTestRequired) * 100)) / 3.0;
    }

    public String getMeritFormula() {
        switch (meritType) {
            case NUST:    return "Matric 10% + FSC 15% + NET 75%";
            case UET:     return "Matric 17% + FSC 50% + ECAT 33%";
            default:      return "Matric 10% + FSC 40% + NTS/Entry Test 50%";
        }
    }

    public String getEntryTestName() {
        switch (meritType) {
            case NUST:  return "NET";
            case UET:   return "ECAT";
            default:    return "NTS/Entry Test";
        }
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getLocation() { return location; }
    public void setLocation(String l) { this.location = l; }
    public String getType() { return type; }
    public void setType(String t) { this.type = t; }
    public double getMatricRequired() { return matricRequired; }
    public void setMatricRequired(double v) { this.matricRequired = v; }
    public double getFscRequired() { return fscRequired; }
    public void setFscRequired(double v) { this.fscRequired = v; }
    public double getNtsRequired() { return entryTestRequired; }
    public void setNtsRequired(double v) { this.entryTestRequired = v; }
    public double getMinAggregate() { return minAggregate; }
    public void setMinAggregate(double v) { this.minAggregate = v; }
    public String getPrograms() { return programs; }
    public void setPrograms(String p) { this.programs = p; }
    public MeritType getMeritType() { return meritType; }
    public void setMeritType(MeritType m) { this.meritType = m; }

    public double getAverageRequirement() {
        return (matricRequired + fscRequired + entryTestRequired) / 3.0;
    }

    @Override
    public String toString() {
        return String.format("University{name='%s', location='%s', merit=%s}", name, location, meritType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        University u = (University) obj;
        return name.equals(u.name) && location.equals(u.location);
    }

    @Override
    public int hashCode() { return name.hashCode() + location.hashCode(); }
}
