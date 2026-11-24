package com.meddonate.exception;

public class ExpiredMedicineException extends Exception {
    public ExpiredMedicineException(String message) {
        super(message);
    }
}