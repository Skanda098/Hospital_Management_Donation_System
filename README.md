# Community Health and Medicine Donation Management System

A desktop application built in **Java 21**, **Java Swing (FlatLaf)**, and **MySQL (JDBC)** to manage eligible, unopened medicine donations and facilitate safe transfers exclusively through authorized healthcare organizations[cite: 3, 5, 10].

Developed as an Alternate Assessment Tool (AAT) mini-project for **Object-Oriented Java Programming (`26CS3PCOOP`)**, aligned with **UN SDG 3 (Good Health and Well-being)** and **UN SDG 12 (Responsible Consumption and Production)**[cite: 1, 3, 10].

---

## Key Features & Functional Requirements

1. **Stakeholder Registration:** Register medicine donors (pharmacies, clinics, hospitals, manufacturers) and recipient healthcare organizations with license verification[cite: 10].
2. **Donation Intake & Safety Validation:** Record medicine names, batch numbers (`BCH-...`), quantities, expiry dates, and storage conditions[cite: 10]. Automatically rejects opened/unsealed packages or batches with less than 90 days of remaining shelf life using custom exceptions[cite: 10].
3. **Real-Time Inventory Management:** View all available and distributed medicine batches in a sortable table with $O(1)$ in-memory cache lookup by batch number[cite: 10].
4. **Authorized Organizational Transfers:** Transfer stock strictly to verified organizations holding valid `HL-` healthcare licenses, blocking unauthorized transfers and logging complete distribution histories[cite: 10].
5. **Background Expiry Monitoring:** A dedicated daemon thread (`ExpiryMonitorThread`) periodically scans inventory for near-expiry batches and updates live alerts in the UI[cite: 1, 10].
6. **File-Based Audit Reports:** Export formatted inventory and expiry alert reports (`.txt`) to the local `reports/` directory using buffered byte streams[cite: 1, 10].

---
## Preview : <br>
<img height="400" alt="Screenshot 2026-11-24 204844" src="https://github.com/user-attachments/assets/7d5dd0e6-1f8a-43e0-b56b-8cf4f1b56f7f" />

---
## Tech Stack

* **Language:** Java 21 (LTS)
* **GUI Framework:** Java Swing + FlatLaf 3.5.1[cite: 3, 4]
* **Database:** MySQL 8.0+
* **Database Connectivity:** JDBC (`mysql-connector-j` 8.4.0)[cite: 5, 10]
* **Build & Dependency Management:** Apache Maven

---

## Folder Structure

```text
MedicineDonationSystem/
├── pom.xml                                 # Maven dependencies (MySQL JDBC & FlatLaf)
├── README.md                               # Project documentation & setup instructions
├── sql/
│   └── schema.sql                          # Database DDL & sample seed data
├── reports/                                # Auto-generated inventory & expiry .txt reports
└── src/
    └── main/
        ├── java/com/meddonate/
        │   ├── Main.java                   # Application entry point
        │   ├── dao/                        # JDBC Data Access Objects
        │   ├── enums/                      # DonationStatus & StorageType enums
        │   ├── exception/                  # Custom checked exceptions
        │   ├── interfaces/                 # EligibleCheckable & Reportable interfaces
        │   ├── model/                      # Domain entities & inheritance hierarchy
        │   ├── service/                    # Business rules, caching & File I/O services
        │   ├── thread/                     # Background expiry monitor thread
        │   ├── ui/                         # Swing JFrame & tabbed JPanel views
        │   └── util/                       # String validation & GenericCache<K, V>
        └── resources/
            └── db.properties               # MySQL connection configuration
```

---

## Prerequisites (What to Install)

Before running the project on a new system, ensure the following tools are installed:

1. **Java Development Kit (JDK) 21:** Download from [Adoptium (Eclipse Temurin)](https://adoptium.net/). Ensure `JAVA_HOME` is configured and `java -version` works in your terminal.
2. **MySQL Server 8.0+ & MySQL Workbench:** Download from [dev.mysql.com](https://dev.mysql.com/downloads/installer/). Note your MySQL `root` username and password during setup.
3. **Apache Maven** *(Optional if using VS Code's bundled Maven, required for terminal builds)*.
4. **Visual Studio Code** with the **Extension Pack for Java** (by Microsoft).

---

## Step-by-Step Setup & Running Instructions

### Step 1: Clone or Extract the Project
Download or clone the `MedicineDonationSystem` folder to your computer and open the root folder (`MedicineDonationSystem`) in **Visual Studio Code**.

### Step 2: Set Up the MySQL Database
1. Open **MySQL Workbench** and connect to your local MySQL server instance.
2. Open the file `sql/schema.sql` from this project (`File` → `Open SQL Script`).
3. Click the **Execute** (lightning bolt) icon to create the `med_donation_db` database, its 4 relational tables (`donors`, `recipient_orgs`, `medicine_batches`, `distributions`), and sample test records.
4. Refresh the **Schemas** panel in MySQL Workbench and confirm `med_donation_db` appears with all 4 tables.\

### OR

### Step 2: Set Up the MySQL Database using MySQL Command Line
1. Open **MySQL Command Line**
2. (Replace Path with your actual path)
3. type `source path ` (path indicates the path to the schema.sql file)


### Step 3: Configure Database Credentials
1. In VS Code, open `src/main/resources/db.properties`.
2. Update `db.user` and `db.password` to match your local MySQL credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/med_donation_db
   db.user=root
   db.password=YOUR_MYSQL_PASSWORD
   ```
3. Save the file (`Ctrl + S`).

### Step 4: Load Maven Dependencies
1. Right-click `pom.xml` in the VS Code Explorer and select **Reload Projects** (or **Update Project**).
2. Wait a few seconds for Maven to download `mysql-connector-j` and `flatlaf`.
3. *(If VS Code shows package path warnings)* Press `Ctrl + Shift + P`, run **Java: Clean Java Language Server Workspace**, and click **Reload and delete**.

### Step 5: Run the Application

#### Option A: Run via VS Code (Recommended)
1. Open `src/main/java/com/meddonate/Main.java`.
2. Click the **Run** button floating directly above `public static void main(String[] args)` (or press `F5`).

#### Option B: Run via Terminal (Maven CLI)
Open a terminal inside the `MedicineDonationSystem` root folder and execute:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.meddonate.Main"
```

---

## Testing & Verification Walkthrough

Once the desktop window opens, verify all modules using the pre-loaded seed data:

* **Tab 1 — Registration:** Confirm *Arogya Rural Health NGO* (`AUTHORIZED: YES`) and *Unverified Local Group* (`AUTHORIZED: NO`) appear in the table. Register a new donor or organization to test database insertion.
* **Tab 2 — Donate Medicine (Safety Checks):**
  * **Valid Entry:** Select a donor, enter a batch number starting with `BCH-` (e.g., `BCH-IBU10`), keep **Original Factory Seal Intact** checked, and use an expiry date more than 90 days ahead.
  * **Rejection Test:** Uncheck **Original Factory Seal Intact** or enter an expiry date within 30 days to verify that `InvalidStorageException` / `ExpiredMedicineException` blocks unsafe donations.
* **Tab 3 — Inventory:** View live stock levels and search by batch code (e.g., `BCH-PARA01`) to test the in-memory `GenericCache`.
* **Tab 4 — Distribute Stock (Authorization Check):**
  * **Blocked Transfer:** Attempt to transfer stock to *Unverified Local Group* to verify `UnauthorizedOrgException` blocks unauthorized recipients[cite: 10].
  * **Authorized Transfer:** Transfer units to *Arogya Rural Health NGO* and confirm inventory quantities decrement automatically.
* **Tab 5 — Alerts & Reports:** View near-expiry alerts populated by the background thread and click **Export Inventory Report (.txt)** or **Export Expiry Alerts (.txt)** to generate timestamped files inside the `reports/` folder.

---

## Troubleshooting

* **`Communications link failure` / `Access denied for user`:** Verify that the MySQL service is running and that `db.username` and `db.password` in `src/main/resources/db.properties` match your MySQL Workbench login.
* **Red underline on `package com.meddonate...`:** Ensure you opened the root `MedicineDonationSystem` folder directly in VS Code (not a parent folder) press ctrl+shift+P and run **Java: Clean Java Language Server Workspace**.
* **Missing FlatLaf or MySQL driver classes:** Run `mvn clean compile` in the terminal to force Maven to download dependencies.
