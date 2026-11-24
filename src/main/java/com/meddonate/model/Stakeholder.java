package com.meddonate.model;

public abstract class Stakeholder {
    protected int id;
    protected String name;
    protected String contact;
    protected String address;

    public Stakeholder(int id, String name, String contact, String address) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.address = address;
    }

    public abstract boolean verifyCredentials();
    public abstract String getRoleSummary();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}