package com.meddonate.dao;

import com.meddonate.model.Donor;
import com.meddonate.model.RecipientOrg;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StakeholderDAO {

    public void addDonor(Donor donor) throws SQLException {
        String sql = "INSERT INTO donors (name, contact, address, donor_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, donor.getName());
            ps.setString(2, donor.getContact());
            ps.setString(3, donor.getAddress());
            ps.setString(4, donor.getDonorType());
            ps.executeUpdate();
        }
    }

    public List<Donor> getAllDonors() throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donors ORDER BY donor_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Donor(
                        rs.getInt("donor_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("address"),
                        rs.getString("donor_type")
                ));
            }
        }
        return list;
    }

    public void addRecipientOrg(RecipientOrg org) throws SQLException {
        String sql = "INSERT INTO recipient_orgs (name, contact, address, license_number, is_authorized) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, org.getName());
            ps.setString(2, org.getContact());
            ps.setString(3, org.getAddress());
            ps.setString(4, org.getLicenseNumber());
            ps.setBoolean(5, org.isAuthorized());
            ps.executeUpdate();
        }
    }

    public List<RecipientOrg> getAllRecipientOrgs() throws SQLException {
        List<RecipientOrg> list = new ArrayList<>();
        String sql = "SELECT * FROM recipient_orgs ORDER BY org_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new RecipientOrg(
                        rs.getInt("org_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("address"),
                        rs.getString("license_number"),
                        rs.getBoolean("is_authorized")
                ));
            }
        }
        return list;
    }
}