# **Clothing Retail Store DBMS**
---
This project is a Database Management System designed for a hypothetical bridal store, as part of a Database Systems course. The system integrates SQL, Java, and Shell Scripting to offer robust functionalities such as inventory and client management, order processing, and reporting. It also features both terminal-based and graphical user interfaces for ease of operation.

## **Features & Core Functionalities**

### **Inventory Management**
- Tracks **veils and gowns**, storing details such as **stock quantity, material, and restocking dates**.
- Supports **real-time inventory updates** for purchases, rentals, and alterations.

### **Customer & Order Management**
- Maintains **customer profiles**, including **measurements and transaction history**.
- **Buy Orders:** Processes **purchases**, including tax calculations and order tracking.
- **Rental Orders:** Manages **rental transactions**, due dates, and rental fees.
- **Alterations:** Logs **customization requests, cost estimates, and deadlines**.

### **Payment Processing**
- Handles payments **linked to orders** with attributes like **payment type, amount, and status**.
- Generates **pending payment reports** to track outstanding balances.

### **Advanced Data Integrity & Reporting**
- **Data consistency** enforced using **relational constraints, normalization (3NF), and functional dependencies**.
- SQL-based **reports on revenue, top-selling items, and client transactions**.
- Predefined and **custom SQL queries** for quick data retrieval.

## **Tech Stack**
| Component | Technology Used |
|-----------|----------------|
| **Database** | Oracle SQL |
| **Backend** | Java (JDBC) |
| **Frontend** | Java Swing (GUI) |
| **Automation** | Bash Shell Scripts |
| **Version Control** | Git & GitHub |

---
## **Setup & Installation**

### **Database Setup**
1. Install **Oracle SQL** or any compatible relational database.
2. Run the `Veilvet DBMS.sql` script to create the database schema.
3. Execute `populate_tables.sh` to insert sample data.

### **Java Application Setup**
1. Ensure you have **Java (JDK 11+)** installed.
2. Compile and run `Menu.java` for a terminal-based interface.
3. Run `MenuGUI.java` for a **Swing-based GUI application**.

### **Running Queries**
- Predefined queries are in `queries.sh`.
- Execute custom SQL queries via **`CustomQueryGUI.java`**.

---


