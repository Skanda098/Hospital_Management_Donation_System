package com.meddonate.thread;

import com.meddonate.model.MedicineBatch;
import com.meddonate.service.InventoryService;

import java.util.List;
import java.util.function.Consumer;

public class ExpiryMonitorThread extends Thread {
    private final InventoryService inventoryService;
    private final Consumer<List<MedicineBatch>> onAlertUpdate;
    private volatile boolean running = true;

    public ExpiryMonitorThread(InventoryService inventoryService, Consumer<List<MedicineBatch>> onAlertUpdate) {
        this.inventoryService = inventoryService;
        this.onAlertUpdate = onAlertUpdate;
        setName("Expiry-Monitor-Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Flags eligible batches expiring within 120 days so alerts are visible during demo
                List<MedicineBatch> expiring = inventoryService.getExpiringSoonBatches(120);
                onAlertUpdate.accept(expiring);
                Thread.sleep(15000); // Re-check every 15 seconds
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
                // Keep monitoring if DB temporarily busy
            }
        }
    }

    public void stopMonitor() {
        running = false;
        interrupt();
    }
}