package com.cpsc.efiling.model;

public class IdentifierData {
    private String identType;
    private String identifier;

    public IdentifierData() {}

    public IdentifierData(String identType, String identifier) {
        this.identType = identType;
        this.identifier = identifier;
    }

    public String getIdentType() { return identType; }
    public void setIdentType(String identType) { this.identType = identType; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
}
