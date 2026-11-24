package com.meddonate.model;

import com.meddonate.enums.DonationStatus;
import java.time.LocalDate;

public class DistributionRecord {
    private int distId;
    private int batchId;
    private int orgId;
    private String medicineName;
    private String orgName;
    private int quantityTransferred;
    private LocalDate transferDate;
    private DonationStatus status;

    public DistributionRecord(int distId, int batchId, int orgId, String medicineName, String orgName,
                              int quantityTransferred, LocalDate transferDate, DonationStatus status) {
        this.distId = distId;
        this.batchId = batchId;
        this.orgId = orgId;
        this.medicineName = medicineName;
        this.orgName = orgName;
        this.quantityTransferred = quantityTransferred;
        this.transferDate = transferDate;
        this.status = status;
    }

    public int getDistId() { return distId; }
    public int getBatchId() { return batchId; }
    public int getOrgId() { return orgId; }
    public String getMedicineName() { return medicineName; }
    public String getOrgName() { return orgName; }
    public int getQuantityTransferred() { return quantityTransferred; }
    public LocalDate getTransferDate() { return transferDate; }
    public DonationStatus getStatus() { return status; }
}