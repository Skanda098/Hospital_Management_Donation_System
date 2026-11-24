package com.meddonate.model;

import com.meddonate.util.ValidationUtil;

public class RecipientOrg extends Stakeholder {
    private String licenseNumber;
    private boolean authorized;

    public RecipientOrg(int id, String name, String contact, String address, String licenseNumber, boolean authorized) {
        super(id, name, contact, address);
        this.licenseNumber = ValidationUtil.normalizeCode(licenseNumber);
        this.authorized = authorized;
    }

    public String getLicenseNumber() { return licenseNumber; }
    public boolean isAuthorized() { return authorized; }

    @Override
    public boolean verifyCredentials() {
        return authorized && ValidationUtil.isValidLicense(licenseNumber);
    }

    @Override
    public String getRoleSummary() {
        return "Recipient Org [" + licenseNumber + "] - " + (authorized ? "AUTHORIZED" : "UNVERIFIED");
    }
}