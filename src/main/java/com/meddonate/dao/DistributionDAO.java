package com.meddonate.dao;

import com.meddonate.enums.DonationStatus;
import com.meddonate.model.DistributionRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistributionDAO {

    public void addDistribution(DistributionRecord record) throws SQLException {
        String sql = "INSERT INTO distributions (batch_id, org_id, quantity_transferred, transfer_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, record.getBatchId());
            ps.setInt(2, record.getOrgId());
            ps.setInt(3, record.getQuantityTransferred());
            ps.setDate(4, Date.valueOf(record.getTransferDate()));
            ps.setString(5, record.getStatus().name());
            ps.executeUpdate();
        }
    }

    public List<DistributionRecord> getAllDistributions() throws SQLException {
        List<DistributionRecord> list = new ArrayList<>();
        String sql = """
                SELECT d.dist_id, d.batch_id, d.org_id, m.medicine_name, r.name AS org_name,
                       d.quantity_transferred, d.transfer_date, d.status
                FROM distributions d
                JOIN medicine_batches m ON d.batch_id = m.batch_id
                JOIN recipient_orgs r ON d.org_id = r.org_id
                ORDER BY d.dist_id DESC
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DistributionRecord(
                        rs.getInt("dist_id"),
                        rs.getInt("batch_id"),
                        rs.getInt("org_id"),
                        rs.getString("medicine_name"),
                        rs.getString("org_name"),
                        rs.getInt("quantity_transferred"),
                        rs.getDate("transfer_date").toLocalDate(),
                        DonationStatus.valueOf(rs.getString("status"))
                ));
            }
        }
        return list;
    }
}