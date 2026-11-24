package com.meddonate.ui;

import com.meddonate.model.MedicineBatch;
import com.meddonate.service.InventoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryPanel extends JPanel {
    private final InventoryService inventoryService;
    private final DefaultTableModel tableModel;

    public InventoryPanel(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(18);
        JButton searchBtn = new JButton("Search Batch (Cache)");
        JButton refreshBtn = new JButton("Show All");

        topBar.add(new JLabel("Batch No:"));
        topBar.add(searchField);
        topBar.add(searchBtn);
        topBar.add(refreshBtn);
        add(topBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Batch No", "Medicine Name", "Qty", "Expiry Date", "Sealed", "Storage", "Status"}, 0);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            MedicineBatch found = inventoryService.searchByBatchNumber(searchField.getText());
            tableModel.setRowCount(0);
            if (found != null) {
                addRow(found);
            } else {
                JOptionPane.showMessageDialog(this, "No batch found matching: " + searchField.getText());
            }
        });

        refreshBtn.addActionListener(e -> refreshData());
        refreshData();
    }

    private void addRow(MedicineBatch b) {
        tableModel.addRow(new Object[]{
                b.getBatchId(), b.getBatchNumber(), b.getMedicineName(), b.getQuantity(),
                b.getExpiryDate(), b.isSealed() ? "Yes" : "No", b.getStorageType(), b.getStatus()
        });
    }

    public void refreshData() {
        try {
            tableModel.setRowCount(0);
            for (MedicineBatch b : inventoryService.getAllBatches()) {
                addRow(b);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load inventory: " + ex.getMessage());
        }
    }
}