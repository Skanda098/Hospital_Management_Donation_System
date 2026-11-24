package com.meddonate.dao;

import com.meddonate.enums.DonationStatus;
import com.meddonate.enums.StorageType;
import com.meddonate.model.MedicineBatch;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public void addBatch(MedicineBatch batch) throws SQLException {
        String sql = "INSERT INTO medicine_batches (donor_id, medicine_name, batch_number, quantity, expiry_date, is_sealed, storage_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, batch.getDonorId());
            ps.setString(2, batch.getMedicineName());
            ps.setString(3, batch.getBatchNumber());
            ps.setInt(4, batch.getQuantity());
            ps.setDate(5, Date.valueOf(batch.getExpiryDate()));
            ps.setBoolean(6, batch.isSealed());
            ps.setString(7, batch.getStorageType().name());
            ps.setString(8, batch.getStatus().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    batch.setBatchId(keys.getInt(1));
                }
            }
        }
    }

    public List<MedicineBatch> getAllBatches() throws SQLException {
        List<MedicineBatch> list = new ArrayList<>();
        String sql = "SELECT * FROM medicine_batches ORDER BY expiry_date ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<MedicineBatch> getAvailableBatches() throws SQLException {
        List<MedicineBatch> list = new ArrayList<>();
        String sql = "SELECT * FROM medicine_batches WHERE quantity > 0 AND status = 'IN_INVENTORY' ORDER BY expiry_date ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void updateBatchQuantityAndStatus(int batchId, int newQty, DonationStatus status) throws SQLException {
        String sql = "UPDATE medicine_batches SET quantity = ?, status = ? WHERE batch_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setString(2, status.name());
            ps.setInt(3, batchId);
            ps.executeUpdate();
        }
    }

    private MedicineBatch mapRow(ResultSet rs) throws SQLException {
        return new MedicineBatch(
                rs.getInt("batch_id"),
                rs.getInt("donor_id"),
                rs.getString("medicine_name"),
                rs.getString("batch_number"),
                rs.getInt("quantity"),
                rs.getDate("expiry_date").toLocalDate(),
                rs.getBoolean("is_sealed"),
                StorageType.valueOf(rs.getString("storage_type")),
                DonationStatus.valueOf(rs.getString("status"))
        );
    }
}