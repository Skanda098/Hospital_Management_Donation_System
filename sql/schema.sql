CREATE DATABASE IF NOT EXISTS med_donation_db;
USE med_donation_db;

CREATE TABLE IF NOT EXISTS donors (
    donor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50) NOT NULL,
    address VARCHAR(200) NOT NULL,
    donor_type VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS recipient_orgs (
    org_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50) NOT NULL,
    address VARCHAR(200) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    is_authorized BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS medicine_batches (
    batch_id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    medicine_name VARCHAR(100) NOT NULL,
    batch_number VARCHAR(50) UNIQUE NOT NULL,
    quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    is_sealed BOOLEAN NOT NULL,
    storage_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    FOREIGN KEY (donor_id) REFERENCES donors(donor_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS distributions (
    dist_id INT AUTO_INCREMENT PRIMARY KEY,
    batch_id INT NOT NULL,
    org_id INT NOT NULL,
    quantity_transferred INT NOT NULL,
    transfer_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(batch_id) ON DELETE CASCADE,
    FOREIGN KEY (org_id) REFERENCES recipient_orgs(org_id) ON DELETE CASCADE
);

-- Sample Seed Data for Immediate Testing
INSERT IGNORE INTO donors (donor_id, name, contact, address, donor_type) VALUES
(1, 'Apollo Community Pharmacy', '9876543210', 'MG Road, Bengaluru', 'Pharmacy'),
(2, 'Dr. Ananya Rao', '9123456780', 'Indiranagar, Bengaluru', 'Clinic');

INSERT IGNORE INTO recipient_orgs (org_id, name, contact, address, license_number, is_authorized) VALUES
(1, 'Arogya Rural Health NGO', '080-22334455', 'Kanakapura Rd, Bengaluru', 'HL-KA-202601', TRUE),
(2, 'Unverified Local Group', '9000011111', 'Whitefield, Bengaluru', 'TEMP-001', FALSE);

INSERT IGNORE INTO medicine_batches (batch_id, donor_id, medicine_name, batch_number, quantity, expiry_date, is_sealed, storage_type, status) VALUES
(1, 1, 'Paracetamol 500mg', 'BCH-PARA01', 200, DATE_ADD(CURDATE(), INTERVAL 110 DAY), TRUE, 'ROOM_TEMP', 'IN_INVENTORY'),
(2, 2, 'Amoxicillin 250mg', 'BCH-AMOX02', 150, DATE_ADD(CURDATE(), INTERVAL 240 DAY), TRUE, 'COOL_DRY', 'IN_INVENTORY');