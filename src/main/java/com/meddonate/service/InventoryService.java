package com.meddonate.service;

import com.meddonate.dao.DistributionDAO;
import com.meddonate.dao.MedicineDAO;
import com.meddonate.enums.DonationStatus;
import com.meddonate.exception.ExpiredMedicineException;
import com.meddonate.exception.InvalidStorageException;
import com.meddonate.exception.UnauthorizedOrgException;
import com.meddonate.interfaces.EligibleCheckable;
import com.meddonate.model.DistributionRecord;
import com.meddonate.model.MedicineBatch;
import com.meddonate.model.RecipientOrg;
import com.meddonate.util.GenericCache;
import com.meddonate.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InventoryService {
    private final MedicineDAO medicineDAO;
    private final DistributionDAO distributionDAO;
    private final EligibilityService eligibilityService;
    private final GenericCache<String, MedicineBatch> batchCache = new GenericCache<>();

    public InventoryService(MedicineDAO medicineDAO, DistributionDAO distributionDAO, EligibilityService eligibilityService) {
        this.medicineDAO = medicineDAO;
        this.distributionDAO = distributionDAO;
        this.eligibilityService = eligibilityService;
    }

    public void refreshCache() throws SQLException {
        batchCache.clear();
        for (MedicineBatch batch : medicineDAO.getAllBatches()) {
            batchCache.put(ValidationUtil.normalizeCode(batch.getBatchNumber()), batch);
        }
    }

    public void registerDonation(MedicineBatch batch)
            throws ExpiredMedicineException, InvalidStorageException, SQLException {
        eligibilityService.validateDonation(batch);
        batch.setStatus(DonationStatus.IN_INVENTORY);
        medicineDAO.addBatch(batch);
        batchCache.put(ValidationUtil.normalizeCode(batch.getBatchNumber()), batch);
    }

    public synchronized void distributeMedicine(MedicineBatch batch, RecipientOrg org, int qtyToTransfer)
            throws UnauthorizedOrgException, SQLException {
        eligibilityService.validateRecipient(org);

        // Demonstrating Type Wrappers & Autoboxing/Unboxing
        Integer currentStock = batch.getQuantity();
        Integer requested = qtyToTransfer;

        if (requested <= 0 || requested > currentStock) {
            throw new IllegalArgumentException("Invalid transfer quantity. Available stock: " + currentStock);
        }

        int remaining = currentStock - requested;
        DonationStatus updatedStatus = (remaining == 0) ? DonationStatus.DISTRIBUTED : DonationStatus.IN_INVENTORY;

        medicineDAO.updateBatchQuantityAndStatus(batch.getBatchId(), remaining, updatedStatus);

        DistributionRecord record = new DistributionRecord(
                0, batch.getBatchId(), org.getId(), batch.getMedicineName(),
                org.getName(), requested, LocalDate.now(), DonationStatus.DISTRIBUTED
        );
        distributionDAO.addDistribution(record);
        refreshCache();
    }

    public MedicineBatch searchByBatchNumber(String batchNumber) {
        return batchCache.get(ValidationUtil.normalizeCode(batchNumber));
    }

    public List<MedicineBatch> getAllBatches() throws SQLException {
        refreshCache();
        return new ArrayList<>(batchCache.getAll());
    }

    public List<MedicineBatch> getAvailableBatches() throws SQLException {
        return medicineDAO.getAvailableBatches();
    }

    public List<MedicineBatch> getExpiringSoonBatches(int withinDays) throws SQLException {
        List<MedicineBatch> all = medicineDAO.getAvailableBatches();
        List<MedicineBatch> expiring = new ArrayList<>();

        // Accessing collection via Iterator
        Iterator<MedicineBatch> iterator = all.iterator();
        while (iterator.hasNext()) {
            MedicineBatch batch = iterator.next();
            long daysLeft = EligibleCheckable.daysUntilExpiry(batch.getExpiryDate());
            if (daysLeft <= withinDays) {
                expiring.add(batch);
            }
        }
        return expiring;
    }
}