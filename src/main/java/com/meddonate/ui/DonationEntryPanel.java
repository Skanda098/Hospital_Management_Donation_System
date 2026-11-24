package com.meddonate.ui;

import com.meddonate.dao.StakeholderDAO;
import com.meddonate.enums.DonationStatus;
import com.meddonate.enums.StorageType;
import com.meddonate.exception.ExpiredMedicineException;
import com.meddonate.exception.InvalidStorageException;
import com.meddonate.model.Donor;
import com.meddonate.model.MedicineBatch;
import com.meddonate.service.InventoryService;
import com.meddonate.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DonationEntryPanel extends JPanel {
    private final StakeholderDAO stakeholderDAO;
    private final InventoryService inventoryService;
    private final JComboBox<Donor> donorCombo = new JComboBox<>();

    public DonationEntryPanel(StakeholderDAO stakeholderDAO, InventoryService inventoryService) {
        this.stakeholderDAO = stakeholderDAO;
        this.inventoryService = inventoryService;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 12));
        form.setBorder(BorderFactory.createTitledBorder("Record Eligible Medicine Donation"));

        JTextField medNameField = new JTextField();
        JTextField batchNoField = new JTextField("BCH-");
        JTextField qtyField = new JTextField();
        JTextField expiryField = new JTextField(LocalDate.now().plusDays(180).toString());
        JComboBox<StorageType> storageBox = new JComboBox<>(StorageType.values());
        JCheckBox sealedCheck = new JCheckBox("Original Factory Seal Intact (Unopened)", true);
        JButton submitBtn = new JButton("Validate & Add to Inventory");

        form.add(new JLabel("Select Donor:")); form.add(donorCombo);
        form.add(new JLabel("Medicine Name:")); form.add(medNameField);
        form.add(new JLabel("Batch Number (BCH-...):")); form.add(batchNoField);
        form.add(new JLabel("Quantity (Units/Strips):")); form.add(qtyField);
        form.add(new JLabel("Expiry Date (YYYY-MM-DD):")); form.add(expiryField);
        form.add(new JLabel("Storage Requirement:")); form.add(storageBox);
        form.add(new JLabel("Safety Check:")); form.add(sealedCheck);
        form.add(new JLabel("")); form.add(submitBtn);

        add(form, BorderLayout.NORTH);

        submitBtn.addActionListener(e -> {
            try {
                Donor donor = (Donor) donorCombo.getSelectedItem();
                if (donor == null) {
                    JOptionPane.showMessageDialog(this, "Please register a donor first.");
                    return;
                }
                String batchNo = ValidationUtil.normalizeCode(batchNoField.getText());
                if (!ValidationUtil.isValidBatchNumber(batchNo)) {
                    JOptionPane.showMessageDialog(this, "Batch number must start with 'BCH-' (min 6 chars).", "Format Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int qty = Integer.parseInt(qtyField.getText().trim());
                LocalDate expiry = LocalDate.parse(expiryField.getText().trim());

                MedicineBatch batch = new MedicineBatch(
                        0, donor.getId(), medNameField.getText().trim(), batchNo,
                        qty, expiry, sealedCheck.isSelected(),
                        (StorageType) storageBox.getSelectedItem(), DonationStatus.PENDING
                );

                inventoryService.registerDonation(batch);
                JOptionPane.showMessageDialog(this, "Donation passed safety checks and added to inventory!");
                medNameField.setText(""); batchNoField.setText("BCH-"); qtyField.setText("");

            } catch (ExpiredMedicineException | InvalidStorageException safetyEx) {
                JOptionPane.showMessageDialog(this, safetyEx.getMessage(), "Eligibility Rejected", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException | DateTimeParseException parseEx) {
                JOptionPane.showMessageDialog(this, "Enter a valid integer quantity and YYYY-MM-DD date.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshDonors();
    }

    public void refreshDonors() {
        try {
            donorCombo.removeAllItems();
            for (Donor d : stakeholderDAO.getAllDonors()) {
                donorCombo.addItem(d);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load donors: " + ex.getMessage());
        }
    }
}