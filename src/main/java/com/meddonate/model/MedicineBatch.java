package com.meddonate.model;

import com.meddonate.enums.DonationStatus;
import com.meddonate.enums.StorageType;
import com.meddonate.interfaces.EligibleCheckable;
import java.time.LocalDate;

public class MedicineBatch implements EligibleCheckable {
    public static final int MIN_SHELF_LIFE_DAYS = 90;
    private static int totalBatchesCreated = 0;

    private int batchId;
    private int donorId;
    private String medicineName;
    private String batchNumber;
    private int quantity;
    private LocalDate expiryDate;
    private boolean sealed;
    private StorageType storageType;
    private DonationStatus status;

    public MedicineBatch(int batchId, int donorId, String medicineName, String batchNumber,
                         int quantity, LocalDate expiryDate, boolean sealed,
                         StorageType storageType, DonationStatus status) {
        this.batchId = batchId;
        this.donorId = donorId;
        this.medicineName = medicineName;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.sealed = sealed;
        this.storageType = storageType;
        this.status = status;
        totalBatchesCreated++;
    }

    @Override
    public boolean isEligibleForDonation() {
        return checkSealedCondition(sealed)
                && EligibleCheckable.daysUntilExpiry(expiryDate) >= MIN_SHELF_LIFE_DAYS;
    }

    public static int getTotalBatchesCreated() { return totalBatchesCreated; }
    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }
    public int getDonorId() { return donorId; }
    public String getMedicineName() { return medicineName; }
    public String getBatchNumber() { return batchNumber; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isSealed() { return sealed; }
    public StorageType getStorageType() { return storageType; }
    public DonationStatus getStatus() { return status; }
    public void setStatus(DonationStatus status) { this.status = status; }

    @Override
    public String toString() {
        return batchNumber + " - " + medicineName + " (Qty: " + quantity + ")";
    }
}