package com.meddonate.interfaces;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public interface EligibleCheckable {
    boolean isEligibleForDonation();

    default boolean checkSealedCondition(boolean isSealed) {
        return verifyBooleanFlag(isSealed);
    }

    private boolean verifyBooleanFlag(boolean flag) {
        return flag;
    }

    static long daysUntilExpiry(LocalDate expiryDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}