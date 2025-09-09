package com.releasetracker.model;

public enum Environment {
    DEV("Development"),
    QA("Quality Assurance"),
    UAT("User Acceptance Testing"),
    PROD("Production");
    
    private final String displayName;
    
    Environment(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Environment getNext() {
        switch (this) {
            case DEV:
                return QA;
            case QA:
                return UAT;
            case UAT:
                return PROD;
            case PROD:
                return null;
            default:
                return null;
        }
    }
    
    public Environment getPrevious() {
        switch (this) {
            case QA:
                return DEV;
            case UAT:
                return QA;
            case PROD:
                return UAT;
            case DEV:
                return null;
            default:
                return null;
        }
    }
}