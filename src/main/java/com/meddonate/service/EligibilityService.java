package com.meddonate.service;

import com.meddonate.exception.ExpiredMedicineException;
import com.meddonate.exception.InvalidStorageException;
import com.meddonate.exception.UnauthorizedOrgException;
import com.meddonate.interfaces.EligibleCheckable;
import com.meddonate.model.MedicineBatch;
import com.meddonate.model.RecipientOrg;
import com.meddonate.model.Stakeholder;

public class EligibilityService {

    public void validateDonation(MedicineBatch batch)
            throws ExpiredMedicineException, InvalidStorageException {

        if (!batch.isSealed()) {
            throw new InvalidStorageException(
                    "Safety Violation: Only unopened, factory-sealed medicines are eligible for donation.");
        }

        long daysLeft = EligibleCheckable.daysUntilExpiry(batch.getExpiryDate());
        if (daysLeft < MedicineBatch.MIN_SHELF_LIFE_DAYS) {
            throw new ExpiredMedicineException(
                    "Expiry Violation: Batch expires in " + daysLeft + " days. Minimum required is "
                            + MedicineBatch.MIN_SHELF_LIFE_DAYS + " days.");
        }

        if (batch.getStorageType() == null) {
            throw new InvalidStorageException("Storage condition must be specified.");
        }
    }

    public void validateRecipient(RecipientOrg org) throws UnauthorizedOrgException {
        // Dynamic method dispatch via abstract Stakeholder reference
        Stakeholder stakeholder = org;
        if (!stakeholder.verifyCredentials()) {
            throw new UnauthorizedOrgException(
                    "Transfer Blocked: '" + org.getName()
                            + "' is not an authorized healthcare organization or lacks a valid HL- license.");
        }
    }
}