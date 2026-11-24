package com.meddonate.ui;

import com.meddonate.dao.DistributionDAO;
import com.meddonate.dao.StakeholderDAO;
import com.meddonate.exception.UnauthorizedOrgException;
import com.meddonate.model.DistributionRecord;
import com.meddonate.model.MedicineBatch;
import com.meddonate.model.RecipientOrg;
import com.meddonate.service.InventoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DistributionPanel extends JPanel {
    private final InventoryService inventoryService;
    private final StakeholderDAO stakeholderDAO;
    private final DistributionDAO distributionDAO;

    private final JComboBox<MedicineBatch> batchCombo = new JComboBox<>();
    private final JComboBox<RecipientOrg> orgCombo = new JComboBox<>();
    private final DefaultTableModel historyModel;

    public DistributionPanel(InventoryService inventoryService, StakeholderDAO stakeholderDAO, DistributionDAO distributionDAO) {
        this.inventoryService = inventoryService;
        this.stakeholderDAO = stakeholderDAO;
        this.distributionDAO = distributionDAO;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Authorized Organizational Medicine Transfer"));

        JTextField qtyField = new JTextField();
        JButton transferBtn = new JButton("Authorize & Transfer Stock");

        form.add(new JLabel("Available Medicine Batch:")); form.add(batchCombo);
        form.add(new JLabel("Recipient Healthcare Org:")); form.add(orgCombo);
        form.add(new JLabel("Quantity to Transfer:")); form.add(qtyField);
        form.add(new JLabel("")); form.add(transferBtn);

        add(form, BorderLayout.NORTH);

        historyModel = new DefaultTableModel(
                new String[]{"Dist ID", "Medicine", "Recipient Org", "Qty Transferred", "Date", "Status"}, 0);
        add(new JScrollPane(new JTable(historyModel)), BorderLayout.CENTER);

        transferBtn.addActionListener(e -> {
            try {
                MedicineBatch batch = (MedicineBatch) batchCombo.getSelectedItem();
                RecipientOrg org = (RecipientOrg) orgCombo.getSelectedItem();
                if (batch == null || org == null) {
                    JOptionPane.showMessageDialog(this, "Select both an available batch and a recipient organization.");
                    return;
                }
                int qty = Integer.parseInt(qtyField.getText().trim());
                inventoryService.distributeMedicine(batch, org, qty);
                JOptionPane.showMessageDialog(this, "Transfer completed and logged!");
                qtyField.setText("");
                refreshData();
            } catch (UnauthorizedOrgException authEx) {
                JOptionPane.showMessageDialog(this, authEx.getMessage(), "Authorization Blocked", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Transfer Failed: " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        refreshData();
    }

    public void refreshData() {
        try {
            batchCombo.removeAllItems();
            for (MedicineBatch b : inventoryService.getAvailableBatches()) {
                batchCombo.addItem(b);
            }
            orgCombo.removeAllItems();
            for (RecipientOrg o : stakeholderDAO.getAllRecipientOrgs()) {
                orgCombo.addItem(o);
            }
            historyModel.setRowCount(0);
            for (DistributionRecord r : distributionDAO.getAllDistributions()) {
                historyModel.addRow(new Object[]{
                        r.getDistId(), r.getMedicineName(), r.getOrgName(),
                        r.getQuantityTransferred(), r.getTransferDate(), r.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load distribution data: " + ex.getMessage());
        }
    }
}