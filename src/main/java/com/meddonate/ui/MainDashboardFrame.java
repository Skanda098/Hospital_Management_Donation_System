package com.meddonate.ui;

import com.meddonate.dao.DistributionDAO;
import com.meddonate.dao.MedicineDAO;
import com.meddonate.dao.StakeholderDAO;
import com.meddonate.service.EligibilityService;
import com.meddonate.service.InventoryService;
import com.meddonate.service.ReportService;
import com.meddonate.thread.ExpiryMonitorThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainDashboardFrame extends JFrame {

    public MainDashboardFrame() {
        setTitle("Community Health & Medicine Donation Management System (SDG 3 & 12)");
        setSize(950, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize Layers
        StakeholderDAO stakeholderDAO = new StakeholderDAO();
        MedicineDAO medicineDAO = new MedicineDAO();
        DistributionDAO distributionDAO = new DistributionDAO();
        EligibilityService eligibilityService = new EligibilityService();
        InventoryService inventoryService = new InventoryService(medicineDAO, distributionDAO, eligibilityService);
        ReportService reportService = new ReportService();

        // Initialize UI Tabs
        RegistrationPanel regPanel = new RegistrationPanel(stakeholderDAO);
        DonationEntryPanel donationPanel = new DonationEntryPanel(stakeholderDAO, inventoryService);
        InventoryPanel inventoryPanel = new InventoryPanel(inventoryService);
        DistributionPanel distributionPanel = new DistributionPanel(inventoryService, stakeholderDAO, distributionDAO);
        ReportsAlertsPanel reportsPanel = new ReportsAlertsPanel(inventoryService, reportService);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1. Registration", regPanel);
        tabs.addTab("2. Donate Medicine", donationPanel);
        tabs.addTab("3. Inventory", inventoryPanel);
        tabs.addTab("4. Distribute Stock", distributionPanel);
        tabs.addTab("5. Alerts & Reports", reportsPanel);

        // Auto-refresh active tab when switched
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) regPanel.refreshData();
            else if (idx == 1) donationPanel.refreshDonors();
            else if (idx == 2) inventoryPanel.refreshData();
            else if (idx == 3) distributionPanel.refreshData();
        });

        add(tabs, BorderLayout.CENTER);

        // Start Background Expiry Monitor Thread
        ExpiryMonitorThread monitorThread = new ExpiryMonitorThread(inventoryService, reportsPanel::updateAlerts);
        monitorThread.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                monitorThread.stopMonitor();
            }
        });
    }
}