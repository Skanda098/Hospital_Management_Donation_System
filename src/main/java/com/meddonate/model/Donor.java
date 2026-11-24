package com.meddonate.model;

import com.meddonate.util.ValidationUtil;

public class Donor extends Stakeholder {
    private String donorType;

    public Donor(int id, String name, String contact, String address, String donorType) {
        super(id, name, contact, address);
        this.donorType = donorType;
    }

    public String getDonorType() { return donorType; }

    @Override
    public boolean verifyCredentials() {
        return ValidationUtil.isNonEmpty(name) && ValidationUtil.isNonEmpty(contact);
    }

    @Override
    public String getRoleSummary() {
        return "Donor (" + donorType + "): " + name;
    }
}