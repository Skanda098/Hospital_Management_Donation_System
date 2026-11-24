package com.meddonate.ui;

import com.meddonate.dao.StakeholderDAO;
import com.meddonate.model.Donor;
import com.meddonate.model.RecipientOrg;
import com.meddonate.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RegistrationPanel extends JPanel {
    private final StakeholderDAO stakeholderDAO;
    private final DefaultTableModel orgTableModel;

    public RegistrationPanel(StakeholderDAO stakeholderDAO) {
        this.stakeholderDAO = stakeholderDAO;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel formsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        formsPanel.add(createDonorForm());
        formsPanel.add(createRecipientForm());
        add(formsPanel, BorderLayout.NORTH);

        orgTableModel = new DefaultTableModel(new String[]{"Org ID", "Organization Name", "License No", "Authorized", "Contact"}, 0);
        JTable orgTable = new JTable(orgTableModel);
        add(new JScrollPane(orgTable), BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createDonorForm() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Register Medicine Donor"));

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Pharmacy", "Clinic", "Hospital", "Manufacturer"});
        JButton addBtn = new JButton("Register Donor");

        panel.add(new JLabel("Donor Name:")); panel.add(nameField);
        panel.add(new JLabel("Contact:")); panel.add(contactField);
        panel.add(new JLabel("Address:")); panel.add(addressField);
        panel.add(new JLabel("Donor Type:")); panel.add(typeBox);
        panel.add(new JLabel("")); panel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                Donor donor = new Donor(0, nameField.getText().trim(), contactField.getText().trim(),
                        addressField.getText().trim(), (String) typeBox.getSelectedItem());
                if (!donor.verifyCredentials()) {
                    JOptionPane.showMessageDialog(this, "Name and Contact are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                stakeholderDAO.addDonor(donor);
                JOptionPane.showMessageDialog(this, "Donor registered successfully!");
                nameField.setText(""); contactField.setText(""); addressField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    private JPanel createRecipientForm() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Register Authorized Recipient Org"));

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField licenseField = new JTextField("HL-");
        JCheckBox authCheck = new JCheckBox("Verified Healthcare License", true);
        JButton addBtn = new JButton("Register Organization");

        panel.add(new JLabel("Org Name:")); panel.add(nameField);
        panel.add(new JLabel("Contact:")); panel.add(contactField);
        panel.add(new JLabel("Address:")); panel.add(addressField);
        panel.add(new JLabel("License (HL-...):")); panel.add(licenseField);
        panel.add(authCheck); panel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String license = ValidationUtil.normalizeCode(licenseField.getText());
                if (!ValidationUtil.isNonEmpty(name) || !ValidationUtil.isNonEmpty(license)) {
                    JOptionPane.showMessageDialog(this, "Organization Name and License are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                RecipientOrg org = new RecipientOrg(0, name, contactField.getText().trim(),
                        addressField.getText().trim(), license, authCheck.isSelected());
                stakeholderDAO.addRecipientOrg(org);
                JOptionPane.showMessageDialog(this, "Recipient organization registered!");
                nameField.setText(""); contactField.setText(""); addressField.setText(""); licenseField.setText("HL-");
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    public void refreshData() {
        try {
            orgTableModel.setRowCount(0);
            for (RecipientOrg org : stakeholderDAO.getAllRecipientOrgs()) {
                orgTableModel.addRow(new Object[]{
                        org.getId(), org.getName(), org.getLicenseNumber(),
                        org.isAuthorized() ? "YES" : "NO", org.getContact()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load organizations: " + ex.getMessage());
        }
    }
}