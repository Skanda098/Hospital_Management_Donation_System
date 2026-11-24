package com.meddonate.ui;

import com.meddonate.interfaces.EligibleCheckable;
import com.meddonate.model.MedicineBatch;
import com.meddonate.service.InventoryService;
import com.meddonate.service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsAlertsPanel extends JPanel {
    private final DefaultTableModel alertModel;

    public ReportsAlertsPanel(InventoryService inventoryService, ReportService reportService) {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        JButton exportInvBtn = new JButton("Export Inventory Report (.txt)");
        JButton exportAlertBtn = new JButton("Export Expiry Alerts (.txt)");

        btnPanel.add(exportInvBtn);
        btnPanel.add(exportAlertBtn);
        add(btnPanel, BorderLayout.NORTH);

        alertModel = new DefaultTableModel(
                new String[]{"Batch No", "Medicine Name", "Qty", "Expiry Date", "Days Remaining"}, 0);
        JTable alertTable = new JTable(alertModel);
        JScrollPane scroll = new JScrollPane(alertTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Live Expiry Alerts (Updated by Background Thread)"));
        add(scroll, BorderLayout.CENTER);

        exportInvBtn.addActionListener(e -> {
            try {
                String path = reportService.generateInventoryReport(inventoryService.getAllBatches());
                JOptionPane.showMessageDialog(this, "Inventory report saved to:\n" + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export Error: " + ex.getMessage());
            }
        });

        exportAlertBtn.addActionListener(e -> {
            try {
                String path = reportService.generateExpiryAlertReport(inventoryService.getExpiringSoonBatches(120));
                JOptionPane.showMessageDialog(this, "Expiry alert report saved to:\n" + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export Error: " + ex.getMessage());
            }
        });
    }

    public void updateAlerts(List<MedicineBatch> expiringBatches) {
        SwingUtilities.invokeLater(() -> {
            alertModel.setRowCount(0);
            for (MedicineBatch b : expiringBatches) {
                alertModel.addRow(new Object[]{
                        b.getBatchNumber(), b.getMedicineName(), b.getQuantity(),
                        b.getExpiryDate(), EligibleCheckable.daysUntilExpiry(b.getExpiryDate()) + " days"
                });
            }
        });
    }
}