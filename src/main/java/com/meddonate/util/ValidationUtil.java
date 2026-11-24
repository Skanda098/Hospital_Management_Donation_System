package com.meddonate.util;

public class ValidationUtil {

    public static String normalizeCode(String code) {
        return (code == null) ? "" : code.trim().toUpperCase();
    }

    public static boolean isValidLicense(String license) {
        String clean = normalizeCode(license);
        return clean.startsWith("HL-") && clean.length() >= 6;
    }

    public static boolean isValidBatchNumber(String batchNumber) {
        String clean = normalizeCode(batchNumber);
        return clean.startsWith("BCH-") && clean.length() >= 6;
    }

    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}