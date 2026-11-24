package com.meddonate.service;

import com.meddonate.interfaces.EligibleCheckable;
import com.meddonate.interfaces.Reportable;
import com.meddonate.model.MedicineBatch;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService implements Reportable {
    private static final String REPORT_DIR = "reports";

    private String writeReportFile(String prefix, String content) throws IOException {
        File dir = new File(REPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File file = new File(dir, prefix + "_" + timestamp + ".txt");

        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(content.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        }
        return file.getAbsolutePath();
    }

    @Override
    public String generateInventoryReport(List<MedicineBatch> batches) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMMUNITY MEDICINE INVENTORY REPORT ===\n");
        sb.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        sb.append(String.format("%-12s %-22s %-8s %-12s %-14s %-12s\n",
                "Batch No", "Medicine Name", "Qty", "Expiry", "Storage", "Status"));
        sb.append("------------------------------------------------------------------------------------\n");

        for (MedicineBatch b : batches) {
            sb.append(String.format("%-12s %-22s %-8d %-12s %-14s %-12s\n",
                    b.getBatchNumber(), b.getMedicineName(), b.getQuantity(),
                    b.getExpiryDate(), b.getStorageType(), b.getStatus()));
        }
        return writeReportFile("inventory_report", sb.toString());
    }

    @Override
    public String generateExpiryAlertReport(List<MedicineBatch> expiringBatches) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MEDICINE EXPIRY ALERT REPORT (EXPIRING WITHIN 120 DAYS) ===\n");
        sb.append("Generated: ").append(LocalDateTime.now()).append("\n\n");

        if (expiringBatches.isEmpty()) {
            sb.append("No medicines currently flagged for near expiry.\n");
        } else {
            for (MedicineBatch b : expiringBatches) {
                long daysLeft = EligibleCheckable.daysUntilExpiry(b.getExpiryDate());
                sb.append(String.format("ALERT: %s (%s) | Qty: %d | Expires: %s (%d days left)\n",
                        b.getMedicineName(), b.getBatchNumber(), b.getQuantity(), b.getExpiryDate(), daysLeft));
            }
        }
        return writeReportFile("expiry_alerts", sb.toString());
    }
}