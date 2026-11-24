package com.meddonate.interfaces;

import com.meddonate.model.MedicineBatch;
import java.io.IOException;
import java.util.List;

public interface Reportable {
    String generateInventoryReport(List<MedicineBatch> batches) throws IOException;
    String generateExpiryAlertReport(List<MedicineBatch> expiringBatches) throws IOException;
}