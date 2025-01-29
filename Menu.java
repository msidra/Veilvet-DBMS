/*  
    Assignment 09 
    Section 02
    CPS510

    Group 9: Sidra Musheer (501122840)
             Maryam Fahmi  (501096276)
             Veezish Ahmad (501080184)
*/ 

import java.sql.*;
import java.util.Scanner;

public class Menu {
    static final String JDBC_URL = "jdbc:oracle:thin:@oracle.scs.ryerson.ca:1521:ORCL";
    static final String USER = "smusheer";
    static final String PASSWORD = "12162840";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            System.out.println("Connected to the database!");
            displayMenu(connection);
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    public static void displayMenu(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Create Tables");
            System.out.println("2. Drop Tables");
            System.out.println("3. Populate Tables");
            System.out.println("4. Query Tables");
            System.out.println("5. Generate Reports (Bonus)");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1:
                    createTables(connection);
                    break;
                case 2:
                    dropTables(connection);
                    break;
                case 3:
                    populateTables(connection);
                    break;
                case 4:
                    queryTables(connection);
                    break;
                case 5:
                    generateReports(connection);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter an integer between 1 - 6. ");
            }
        }
    }

    public static void createTypes(Connection connection) {
        String[] createTypesSQL = {
            // Create Type for Veil Description
            "CREATE OR REPLACE TYPE VeilDescType AS OBJECT (" +
            "VMaterial VARCHAR2(50), " +
            "VLength_cm NUMBER, " +
            "VColor VARCHAR2(20))",
    
            // Create Type for Gown Description
            "CREATE OR REPLACE TYPE GownDescType AS OBJECT (" +
            "GMaterial VARCHAR2(50), " +
            "Neckline VARCHAR2(20), " +
            "GownStyle VARCHAR2(50), " +
            "Embellishments VARCHAR2(50), " +
            "GColor VARCHAR2(20))",
    
            // Create Type for Client Name
            "CREATE OR REPLACE TYPE NameType AS OBJECT (" +
            "FirstName VARCHAR2(50), " +
            "LastName VARCHAR2(50))",
    
            // Create Type for Client Measurements
            "CREATE OR REPLACE TYPE CMeasurementsType AS OBJECT (" +
            "CBust_cm VARCHAR2(20), " +
            "CWaist_cm VARCHAR2(20), " +
            "CHips_cm VARCHAR2(20), " +
            "CShoulders_cm VARCHAR2(20), " +
            "CSleeves_cm VARCHAR2(20), " +
            "CLength_cm VARCHAR2(20))",
    
            // Create Type for Gown Measurements
            "CREATE OR REPLACE TYPE GMeasurementsType AS OBJECT (" +
            "GBust_cm VARCHAR2(20), " +
            "GWaist_cm VARCHAR2(20), " +
            "GHips_cm VARCHAR2(20), " +
            "GShoulders_cm VARCHAR2(20), " +
            "GSleeves_cm VARCHAR2(20), " +
            "GLength_cm VARCHAR2(20))",

            // Create Type for Client Address
            "CREATE OR REPLACE TYPE AddressType AS OBJECT (" +
            "Street VARCHAR2(100), " +
            "City VARCHAR2(50), " +
            "Province VARCHAR2(2), " +
            "PC VARCHAR2(7), " +  // Postal Code (7 characters)
            "Country VARCHAR2(50))"
        };
    
        try (Statement stmt = connection.createStatement()) {
            for (String sql : createTypesSQL) {
                stmt.execute(sql);
            }
            System.out.println("Types for grouped attributes created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating types for grouped attributes.");
            e.printStackTrace();
        }
    }
    
    public static void createTables(Connection connection) {
        dropTables(connection); // Drop existing tables before creating new ones
        createTypes(connection); // Call to create the necessary types

        String[] createSQL = {
            "CREATE TABLE Veil (VeilID NUMBER NOT NULL PRIMARY KEY, VPrice_CAD NUMBER(10, 2), VeilDesc VeilDescType, VStockQuantity NUMBER, VLastRestocked DATE, VMinimumQuantity NUMBER)",
            "CREATE TABLE Gown (GownID NUMBER NOT NULL PRIMARY KEY, GPrice_CAD NUMBER(10, 2), GStockQuantity NUMBER, GownDesc GownDescType, GLastRestocked DATE, GMinimumQuantity NUMBER, GMeasurements GMeasurementsType)",
            "CREATE TABLE Client (" +
                                "ClientID NUMBER NOT NULL PRIMARY KEY, " +
                                "FullName NameType, " +
                                "Email VARCHAR2(100), " +
                                "Phone NUMBER(10), " +
                                "Address AddressType, " +
                                "CMeasurements CMeasurementsType)",


            "CREATE TABLE BuyOrder (" +
                                "BuyOrderID NUMBER NOT NULL PRIMARY KEY, " +
                                "ClientID NUMBER, " +
                                "OrderDate DATE, " +
                                "OrdTotalAmount_CAD NUMBER(10, 2), " +
                                "OrderStatus VARCHAR2(50), " +
                                "TaxAmount_CAD NUMBER(10, 2), " +
                                "DueDate DATE, " +
                                "FOREIGN KEY (ClientID) REFERENCES Client(ClientID))",

            "CREATE TABLE RentalOrder (" +
                                "RentalOrderID NUMBER NOT NULL PRIMARY KEY, " +
                                "ClientID NUMBER, " +
                                "RentDate DATE, " +
                                "ReturnDate DATE, " +
                                "RentTotalAmount_CAD NUMBER(10, 2), " +
                                "RentStatus VARCHAR2(50), " +
                                "TaxAmount_CAD NUMBER(10, 2), " +
                                "FOREIGN KEY (ClientID) REFERENCES Client(ClientID))",

            "CREATE TABLE BuyItems (" +
                                "BuyItemID NUMBER NOT NULL PRIMARY KEY, " +
                                "BuyOrderID NUMBER, " +
                                "VeilID NUMBER, " + 
                                "GownID NUMBER, " +
                                "Quantity NUMBER, " +
                                "ItemPrice_CAD NUMBER(10, 2), " +
                                "FOREIGN KEY (BuyOrderID) REFERENCES BuyOrder(BuyOrderID), " +
                                "FOREIGN KEY (VeilID) REFERENCES Veil(VeilID), " +
                                "FOREIGN KEY (GownID) REFERENCES Gown(GownID))",

            "CREATE TABLE RentalItems (" +
                                "RentalItemsID NUMBER NOT NULL PRIMARY KEY, " +
                                "RentalOrderID NUMBER, " +
                                "VeilID NUMBER, " +
                                "GownID NUMBER, " +
                                "Quantity NUMBER, " +
                                "RentalPrice_CAD NUMBER(10, 2), " +
                                "FOREIGN KEY (RentalOrderID) REFERENCES RentalOrder(RentalOrderID), " +
                                "FOREIGN KEY (VeilID) REFERENCES Veil(VeilID), " +
                                "FOREIGN KEY (GownID) REFERENCES Gown(GownID))",

                "CREATE TABLE Alterations (" +
                                "AlterationID NUMBER NOT NULL PRIMARY KEY, " +
                                "BuyItemID NUMBER, " +
                                "AlterationDesc VARCHAR2(100), " +
                                "Cost_CAD NUMBER(10, 2), " +
                                "AltDueDate DATE, " +
                                "AltStatus VARCHAR2(50), " +
                                "AlterationCompletedDate DATE, " +
                                "FOREIGN KEY (BuyItemID) REFERENCES BuyItems(BuyItemID))",

                "CREATE TABLE Payment (" +
                                "PaymentID NUMBER NOT NULL PRIMARY KEY, " +
                                "BuyOrderID NUMBER, " +
                                "RentalOrderID NUMBER, " +
                                "ClientID NUMBER, " +
                                "PayAmount_CAD NUMBER(10, 2), " +
                                "PaymentDate DATE, " +
                                "PaymentType VARCHAR2(50), " +
                                "PayStatus VARCHAR2(50), " +
                                "FOREIGN KEY (BuyOrderID) REFERENCES BuyOrder(BuyOrderID), " +
                                "FOREIGN KEY (RentalOrderID) REFERENCES RentalOrder(RentalOrderID), " +
                                "FOREIGN KEY (ClientID) REFERENCES Client(ClientID))"

        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : createSQL) {
                stmt.execute(sql);
            }
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating tables.");
            e.printStackTrace();
        }
    }

    public static void dropTables(Connection connection) {
        String[] dropSQL = {
            "DROP TABLE Veil CASCADE CONSTRAINTS",
            "DROP TABLE Gown CASCADE CONSTRAINTS",
            "DROP TABLE Client CASCADE CONSTRAINTS",
            "DROP TABLE BuyOrder CASCADE CONSTRAINTS",
            "DROP TABLE RentalOrder CASCADE CONSTRAINTS",
            "DROP TABLE BuyItems CASCADE CONSTRAINTS",
            "DROP TABLE RentalItems CASCADE CONSTRAINTS",
            "DROP TABLE Alterations CASCADE CONSTRAINTS",
            "DROP TABLE Payment CASCADE CONSTRAINTS",
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : dropSQL) {
                stmt.execute(sql);
            }
            System.out.println("Tables dropped successfully.");
        } catch (SQLException e) {
            System.out.println("Skipping drop: " + e.getMessage());
        }
    }

    public static void populateTables(Connection connection) {
        String[] insertSQL = {
        
            // Insert into Veil 
        "INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity) " +
        "VALUES (1, 250.00, VeilDescType('Silk', 180, 'Ivory'), 10, TO_DATE('2024-01-01', 'YYYY-MM-DD'), 5)",
        "INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity) " +
        "VALUES (2, 300.00, VeilDescType('Lace', 200, 'White'), 8, TO_DATE('2024-02-01', 'YYYY-MM-DD'), 4)",
        "INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity) " +
        "VALUES (3, 275.00, VeilDescType('Tulle', 150, 'White'), 12, TO_DATE('2024-01-20', 'YYYY-MM-DD'), 6)",
        "INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity) " +
        "VALUES (4, 320.00, VeilDescType('Chiffon', 160, 'Ivory'), 7, TO_DATE('2024-02-10', 'YYYY-MM-DD'), 3)",
        "INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity) " +
        "VALUES (5, 350.00, VeilDescType('Organza', 180, 'Champagne'), 9, TO_DATE('2024-02-25', 'YYYY-MM-DD'), 4)",

        // Insert into Gown
        "INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements) " +
        "VALUES (1, 1500.00, 5, GownDescType('Satin', 'Sweetheart', 'Ball Gown', 'Beads', 'Ivory'), " +
        "TO_DATE('2024-01-15', 'YYYY-MM-DD'), 2, GMeasurementsType('36.0', '28.0', '38.0', '14.0', '24.0', '62.0'))",
        "INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements) " +
        "VALUES (2, 2000.00, 3, GownDescType('Tulle', 'V-Neck', 'A-Line', 'Sequins', 'Champagne'), " +
        "TO_DATE('2024-02-10', 'YYYY-MM-DD'), 1, GMeasurementsType('34.0', '26.0', '36.0', '13.5', '23.0', '60.0'))",
        "INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements) " +
        "VALUES (3, 1700.00, 4, GownDescType('Organza', 'Sweetheart', 'Mermaid', 'Lace', 'Ivory'), " +
        "TO_DATE('2024-02-05', 'YYYY-MM-DD'), 2, GMeasurementsType('36.5', '27.0', '37.5', '14.0', '23.0', '60.0'))",
        "INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements) " +
        "VALUES (4, 2500.00, 3, GownDescType('Satin', 'Off-Shoulder', 'Ball Gown', 'Beads', 'White'), " +
        "TO_DATE('2024-03-01', 'YYYY-MM-DD'), 1, GMeasurementsType('34.0', '25.0', '36.0', '13.5', '22.5', '62.0'))",
        "INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements) " +
        "VALUES (5, 1900.00, 6, GownDescType('Tulle', 'Halter', 'A-Line', 'Sequins', 'Blush'), " +
        "TO_DATE('2024-03-10', 'YYYY-MM-DD'), 3, GMeasurementsType('35.0', '26.5', '37.5', '14.0', '23.5', '61.0'))",
    
        // Insert into Client
        "INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements) " +
        "VALUES (1, NameType('Justin', 'Trudeau'), 'j.true@example.com', 1234567890, " +
        "AddressType('123 Bridal Lane', 'Toronto', 'ON', 'M5A 1B1', 'Canada'), " +
        "CMeasurementsType('34.0', '26.5', '36.0', '14.5', '22.0', '60.0'))",

        "INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements) " +
        "VALUES (2, NameType('Chanandler', 'Bing'), 'channie.bing@example.com', 9876543210, " +
        "AddressType('456 Wedding St', 'Mississauga', 'ON', 'L5N 2T3', 'Canada'), " +
        "CMeasurementsType('36.0', '28.0', '38.0', '15.0', '23.5', '61.0'))",

        "INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements) " +
        "VALUES (3, NameType('Monica', 'Geller'), 'monica.geller@example.com', 1112223333, " +
        "AddressType('789 Fancy Ave', 'Toronto', 'ON', 'M5B 2H1', 'Canada'), " +
        "CMeasurementsType('35.0', '27.0', '37.0', '14.0', '22.5', '59.0'))",

        "INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements) " +
        "VALUES (4, NameType('Rachel', 'Green'), 'rachel.green@example.com', 9998887777, " +
        "AddressType('101 Fashion St', 'New York', 'NY', '10001', 'USA'), " +
        "CMeasurementsType('34.5', '25.0', '36.0', '13.5', '22.0', '60.0'))",

        //Insert into BuyOrder
        "INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate) " +
        "VALUES (1, 1, TO_DATE('2024-02-15', 'YYYY-MM-DD'), 2750.00, 'Completed', 100.00, TO_DATE('2024-02-20', 'YYYY-MM-DD'))",

        "INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate) " +
        "VALUES (2, 2, TO_DATE('2024-02-16', 'YYYY-MM-DD'), 1800.00, 'Processing', 80.00, TO_DATE('2024-02-25', 'YYYY-MM-DD'))",

        "INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate) " +
        "VALUES (3, 1, TO_DATE('2024-03-01', 'YYYY-MM-DD'), 2500.00, 'Processing', 125.00, TO_DATE('2024-03-10', 'YYYY-MM-DD'))",

        "INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate) " +
        "VALUES (4, 3, TO_DATE('2024-03-10', 'YYYY-MM-DD'), 2200.00, 'Completed', 110.00, TO_DATE('2024-03-15', 'YYYY-MM-DD'))",

        "INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate) " +
        "VALUES (5, 4, TO_DATE('2024-03-11', 'YYYY-MM-DD'), 1950.00, 'Processing', 90.00, TO_DATE('2024-03-20', 'YYYY-MM-DD'))",

        //Insert into RentalOrder
        "INSERT INTO RentalOrder (RentalOrderID, ClientID, RentDate, ReturnDate, RentTotalAmount_CAD, RentStatus, TaxAmount_CAD) " +
        "VALUES (3, 1, TO_DATE('2024-03-05', 'YYYY-MM-DD'), TO_DATE('2024-03-15', 'YYYY-MM-DD'), 800.00, 'Completed', 40.00)",

        "INSERT INTO RentalOrder (RentalOrderID, ClientID, RentDate, ReturnDate, RentTotalAmount_CAD, RentStatus, TaxAmount_CAD) " +
        "VALUES (4, 3, TO_DATE('2024-03-12', 'YYYY-MM-DD'), TO_DATE('2024-03-18', 'YYYY-MM-DD'), 750.00, 'Processing', 30.00)",

        "INSERT INTO RentalOrder (RentalOrderID, ClientID, RentDate, ReturnDate, RentTotalAmount_CAD, RentStatus, TaxAmount_CAD) " +
        "VALUES (5, 4, TO_DATE('2024-03-13', 'YYYY-MM-DD'), TO_DATE('2024-03-20', 'YYYY-MM-DD'), 800.00, 'Completed', 40.00)",

        //Insert into BuyItems 
        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (1, 1, NULL, 1, 1, 1500.00)",

        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (2, 2, 2, 2, 1, 1800.00)",

        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (3, 3, 1, 1, 2, 2500.00)",

        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (4, 4, NULL, 1, 1, 1500.00)",

        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (5, 5, 1, 2, 1, 1950.00)",

        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (6, 4, 3, 3, 1, 1700.00)",

        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (7, 4, 4, 4, 1, 2500.00)",

        "INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD) " +
        "VALUES (8, 5, 5, 5, 2, 3800.00)",

        //Insert into RentalItems
        "INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD) " +
        "VALUES (3, 3, 2, 2, 1, 800.00)",

        "INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD) " +
        "VALUES (4, 4, 1, 1, 1, 750.00)",

        "INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD) " +
        "VALUES (5, 5, 2, 2, 1, 800.00)",

        "INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD) " +
        "VALUES (6, 4, 3, 3, 1, 750.00)",

        "INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD) " +
        "VALUES (7, 5, 4, 4, 1, 800.00)",

        //Insert into Alterations
        "INSERT INTO Alterations (AlterationID, BuyItemID, AlterationDesc, Cost_CAD, AltDueDate, AltStatus, AlterationCompletedDate) " +
        "VALUES (1, 1, 'Shortened length', 50.00, TO_DATE('2024-02-25', 'YYYY-MM-DD'), 'Pending', NULL)",

        "INSERT INTO Alterations (AlterationID, BuyItemID, AlterationDesc, Cost_CAD, AltDueDate, AltStatus, AlterationCompletedDate) " +
        "VALUES (2, 2, 'Adjusted waistline', 75.00, TO_DATE('2024-02-28', 'YYYY-MM-DD'), 'Completed', TO_DATE('2024-02-27', 'YYYY-MM-DD'))",

        "INSERT INTO Alterations (AlterationID, BuyItemID, AlterationDesc, Cost_CAD, AltDueDate, AltStatus, AlterationCompletedDate) " +
        "VALUES (3, 3, 'Added sleeves', 100.00, TO_DATE('2024-03-05', 'YYYY-MM-DD'), 'Pending', NULL)",

        "INSERT INTO Alterations (AlterationID, BuyItemID, AlterationDesc, Cost_CAD, AltDueDate, AltStatus, AlterationCompletedDate) " +
        "VALUES (4, 4, 'Shortened sleeves', 50.00, TO_DATE('2024-03-10', 'YYYY-MM-DD'), 'Pending', NULL)",

        "INSERT INTO Alterations (AlterationID, BuyItemID, AlterationDesc, Cost_CAD, AltDueDate, AltStatus, AlterationCompletedDate) " +
        "VALUES (5, 5, 'Added embellishments', 120.00, TO_DATE('2024-03-15', 'YYYY-MM-DD'), 'Pending', NULL)",

        //Insert into Payment
        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (1, 1, NULL, 1, 2750.00, TO_DATE('2024-02-15', 'YYYY-MM-DD'), 'Credit Card', 'Paid')",

        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (2, 2, NULL, 2, 1800.00, TO_DATE('2024-02-16', 'YYYY-MM-DD'), 'PayPal', 'Pending')",

        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (3, 3, NULL, 1, 2500.00, TO_DATE('2024-03-01', 'YYYY-MM-DD'), 'Credit Card', 'Paid')",

        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (4, NULL, 3, 1, 800.00, TO_DATE('2024-03-05', 'YYYY-MM-DD'), 'Credit Card', 'Paid')",

        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (5, 4, NULL, 3, 2200.00, TO_DATE('2024-03-10', 'YYYY-MM-DD'), 'Credit Card', 'Paid')",

        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (6, 5, NULL, 4, 1950.00, TO_DATE('2024-03-11', 'YYYY-MM-DD'), 'Credit Card', 'Pending')",

        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (7, NULL, 4, 3, 750.00, TO_DATE('2024-03-12', 'YYYY-MM-DD'), 'Debit Card', 'Paid')",

        "INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) " +
        "VALUES (8, NULL, 5, 4, 800.00, TO_DATE('2024-03-13', 'YYYY-MM-DD'), 'Credit Card', 'Paid')",

    };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : insertSQL) {
                stmt.executeUpdate(sql);
            }
            System.out.println("Tables populated successfully.");
        } catch (SQLException e) {
            System.out.println("Error populating tables.");
            e.printStackTrace();
        }
    }

    public static void queryTables(Connection connection) {
        String[] queries = {
            // Retrieve veils that are in stock and sort by price descending
            "SELECT DISTINCT v.VeilID, v.VPrice_CAD, v.VStockQuantity, v.VeilDesc.VMaterial, v.VeilDesc.VColor " +
            "FROM Veil v WHERE v.VStockQuantity > 0 ORDER BY v.VPrice_CAD DESC",
    
            // Group veils by material and calculate total stock per material
            "SELECT v.VeilDesc.VMaterial, SUM(v.VStockQuantity) AS TotalStock " +
            "FROM Veil v GROUP BY v.VeilDesc.VMaterial",
    
            // Retrieve gowns with stock above the minimum, sorted by stock quantity
            "SELECT g.GownID, g.GPrice_CAD, g.GStockQuantity, g.GownDesc.GownStyle, g.GMeasurements.GBust_cm, g.GMeasurements.GWaist_cm " +
            "FROM Gown g WHERE g.GStockQuantity > g.GMinimumQuantity ORDER BY g.GStockQuantity DESC",
    
            // Count gowns by style
            "SELECT g.GownDesc.GownStyle, COUNT(g.GownID) AS GownCount " +
            "FROM Gown g GROUP BY g.GownDesc.GownStyle",
    
            // Retrieve all clients with names and email addresses, sorted by last name
            "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, c.Email, c.FullName.LastName AS LastName " +
            "FROM Client c ORDER BY LastName",
    
            // Group clients by city and count the number of clients per city
            "SELECT c.Address.City, COUNT(c.ClientID) AS ClientCount " +
            "FROM Client c GROUP BY c.Address.City",
    
            // Retrieve buy orders sorted by total amount
            "SELECT b.BuyOrderID, b.ClientID, b.OrdTotalAmount_CAD, b.OrderStatus, b.TaxAmount_CAD, b.OrderDate " +
            "FROM BuyOrder b ORDER BY b.OrdTotalAmount_CAD DESC",
    
            // Summarize total amounts by order status
            "SELECT b.OrderStatus, SUM(b.OrdTotalAmount_CAD) AS TotalAmount " +
            "FROM BuyOrder b GROUP BY b.OrderStatus",
    
            // Retrieve rental orders sorted by total rent amount
            "SELECT r.RentalOrderID, r.ClientID, r.RentTotalAmount_CAD, r.RentDate, r.ReturnDate " +
            "FROM RentalOrder r ORDER BY r.RentTotalAmount_CAD DESC",
    
            // Retrieve buy items sorted by item price
            "SELECT bi.BuyItemID, bi.BuyOrderID, bi.GownID, bi.Quantity, bi.ItemPrice_CAD " +
            "FROM BuyItems bi ORDER BY bi.ItemPrice_CAD DESC",
    
            // Retrieve rental items sorted by rental price
            "SELECT ri.RentalItemsID, ri.RentalOrderID, ri.GownID, ri.Quantity, ri.RentalPrice_CAD " +
            "FROM RentalItems ri ORDER BY ri.RentalPrice_CAD DESC",
    
            // Retrieve all alterations sorted by due date
            "SELECT a.AlterationID, a.AlterationDesc, a.Cost_CAD, a.AltDueDate, a.AltStatus " +
            "FROM Alterations a ORDER BY a.AltDueDate",
    
            // Retrieve all payments sorted by payment date
            "SELECT p.PaymentID, p.PayAmount_CAD, p.PaymentDate, p.PaymentType, p.PayStatus " +
            "FROM Payment p ORDER BY p.PaymentDate DESC",
    
            // Retrieve client details, buy order IDs, and total payments
            "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, b.BuyOrderID, b.OrdTotalAmount_CAD, p.PayAmount_CAD " +
            "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN Payment p ON b.BuyOrderID = p.BuyOrderID " +
            "ORDER BY b.OrdTotalAmount_CAD DESC",
    
            // Retrieve rental details with client names and payment amounts
            "SELECT r.RentalOrderID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, r.RentTotalAmount_CAD, p.PayAmount_CAD " +
            "FROM RentalOrder r JOIN Client c ON r.ClientID = c.ClientID JOIN Payment p ON r.RentalOrderID = p.RentalOrderID " +
            "ORDER BY r.RentTotalAmount_CAD DESC",
    
            // Retrieve total payments made by each client
            "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, SUM(p.PayAmount_CAD) AS TotalPayments " +
            "FROM Client c JOIN Payment p ON c.ClientID = p.ClientID " +
            "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName ORDER BY TotalPayments DESC",
    
            // Retrieve buy orders, rental orders, and payment details for each client
            "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, b.BuyOrderID, r.RentalOrderID, " +
            "b.OrdTotalAmount_CAD AS BuyOrderTotal, r.RentTotalAmount_CAD AS RentalOrderTotal, p.PayAmount_CAD AS PaymentAmount, " +
            "COUNT(bi.BuyItemID) AS TotalBuyItems, COUNT(ri.RentalItemsID) AS TotalRentalItems " +
            "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN RentalOrder r ON c.ClientID = r.ClientID " +
            "JOIN Payment p ON p.ClientID = c.ClientID LEFT JOIN BuyItems bi ON b.BuyOrderID = bi.BuyOrderID " +
            "LEFT JOIN RentalItems ri ON r.RentalOrderID = ri.RentalOrderID " +
            "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName, b.BuyOrderID, r.RentalOrderID, b.OrdTotalAmount_CAD, " +
            "r.RentTotalAmount_CAD, p.PayAmount_CAD HAVING COUNT(bi.BuyItemID) > 0 AND COUNT(ri.RentalItemsID) > 0 " +
            "ORDER BY c.FullName.LastName",
    
            // Retrieve total spent (buy + rental) by each client
            "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, " +
            "SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) AS TotalAmountSpent " +
            "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN RentalOrder r ON c.ClientID = r.ClientID " +
            "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName HAVING SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) > 0 " +
            "ORDER BY TotalAmountSpent DESC",
    
            // Retrieve top 3 best-selling gowns by quantity sold
            "SELECT GownID, GownStyle, TotalQuantity FROM (" +
            "SELECT g.GownID, g.GownDesc.GownStyle AS GownStyle, SUM(bi.Quantity) AS TotalQuantity " +
            "FROM Gown g JOIN BuyItems bi ON g.GownID = bi.GownID " +
            "GROUP BY g.GownID, g.GownDesc.GownStyle ORDER BY TotalQuantity DESC) WHERE ROWNUM <= 3",
    
            // Retrieve processing orders with payment status "Paid"
            "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, b.BuyOrderID, b.OrderStatus, p.PayStatus " +
            "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN Payment p ON b.BuyOrderID = p.BuyOrderID " +
            "WHERE b.OrderStatus = 'Processing' AND p.PayStatus = 'Paid' ORDER BY c.ClientID",
    
            // Group buy orders by city and calculate total spent per city
            "SELECT c.Address.City, COUNT(b.BuyOrderID) AS TotalOrders, SUM(b.OrdTotalAmount_CAD) AS TotalSpent " +
            "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID " +
            "GROUP BY c.Address.City ORDER BY TotalOrders DESC",
    
            // Group rental orders by city and calculate average, minimum, and maximum rent amounts
            "SELECT c.Address.City, AVG(r.RentTotalAmount_CAD) AS AvgRentAmount, MIN(r.RentTotalAmount_CAD) AS MinRentAmount, " +
            "MAX(r.RentTotalAmount_CAD) AS MaxRentAmount " +
            "FROM Client c JOIN RentalOrder r ON c.ClientID = r.ClientID " +
            "GROUP BY c.Address.City ORDER BY AvgRentAmount DESC"
        };
    
        try (Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            for (String query : queries) {
                // Print a short description for the current query
                String queryDescription = getQueryDescription(query);
                System.out.println("\nExecuting query: " + queryDescription);
    
                try (ResultSet rs = stmt.executeQuery(query)) {
                    // Fetch metadata for column names
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
    
                    // Display column headers
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.printf("%-20s", metaData.getColumnName(i));
                    }
                    System.out.println();
    
                    // Display rows
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.printf("%-20s", rs.getString(i));
                        }
                        System.out.println();
                    }
                } catch (SQLException e) {
                    System.out.println("Error executing query: " + queryDescription);
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection or query error.");
            e.printStackTrace();
        }
    }
    
    // Helper method to return a short description for a given query
    private static String getQueryDescription(String query) {
        if (query.contains("FROM Veil WHERE VStockQuantity > 0")) return "Veils in stock sorted by price";
        if (query.contains("GROUP BY v.VeilDesc.VMaterial")) return "Total veil stock by material";
        if (query.contains("FROM Gown WHERE g.GStockQuantity > g.GMinimumQuantity")) return "Gowns with sufficient stock";
        if (query.contains("GROUP BY g.GownDesc.GownStyle")) return "Count gowns by style";
        if (query.contains("FROM Client ORDER BY LastName")) return "Clients with names and emails";
        if (query.contains("GROUP BY c.Address.City")) return "Client count by city";
        if (query.contains("FROM BuyOrder ORDER BY b.OrdTotalAmount_CAD DESC")) return "Buy orders sorted by amount";
        if (query.contains("GROUP BY b.OrderStatus")) return "Total buy amount by order status";
        if (query.contains("FROM RentalOrder ORDER BY r.RentTotalAmount_CAD DESC")) return "Rental orders sorted by rent amount";
        if (query.contains("FROM BuyItems bi")) return "Buy items sorted by price";
        if (query.contains("FROM RentalItems ri")) return "Rental items sorted by price";
        if (query.contains("FROM Alterations a")) return "Alterations sorted by due date";
        if (query.contains("FROM Payment p")) return "Payments sorted by date";
        if (query.contains("JOIN BuyOrder b ON c.ClientID = b.ClientID")) return "Client buy orders and payments";
        if (query.contains("JOIN RentalOrder r ON c.ClientID = r.ClientID")) return "Rental orders and payments by client";
        if (query.contains("GROUP BY c.ClientID")) return "Total payments by client";
        if (query.contains("GROUP BY c.Address.City")) return "City-based orders or rentals";
        return "Other query";
    }
    

    public static void generateReports(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        boolean inReportMenu = true;
    
        while (inReportMenu) {
            // Display report options
            System.out.println("\nReport Options:");
            System.out.println("1. Revenue by Client");
            System.out.println("2. Top-Selling Items");
            System.out.println("3. Pending Payments");
            System.out.println("4. Clients with Outstanding Payments and Total Dues");
            System.out.println("5. Top Clients by Total Orders and Spending");
            System.out.println("6. Monthly Revenue Breakdown");
            System.out.println("7. Most Altered Gowns and Alteration Costs");
            System.out.println("8. Return to Main Menu");
            System.out.print("Enter your choice: ");
            int reportChoice = scanner.nextInt();
    
            String query = null;
    
            switch (reportChoice) {
                case 1:
                    query = "SELECT c.ClientID, " +
                            "       c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, " +
                            "       SUM(p.PayAmount_CAD) AS TotalPayments " +
                            "FROM Client c " +
                            "JOIN Payment p ON c.ClientID = p.ClientID " +
                            "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName " +
                            "ORDER BY TotalPayments DESC";
                    break;
    
                case 2:
                    query = "SELECT g.GownID, g.GownDesc.GownStyle, SUM(bi.Quantity) AS TotalSold " +
                            "FROM Gown g " +
                            "JOIN BuyItems bi ON g.GownID = bi.GownID " +
                            "GROUP BY g.GownID, g.GownDesc.GownStyle " +
                            "ORDER BY TotalSold DESC";
                    break;
    
                case 3:
                    query = "SELECT c.ClientID, " +
                            "       c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, " +
                            "       b.BuyOrderID, b.OrderStatus, p.PayStatus " +
                            "FROM Client c " +
                            "JOIN BuyOrder b ON c.ClientID = b.ClientID " +
                            "JOIN Payment p ON b.BuyOrderID = p.BuyOrderID " +
                            "WHERE b.OrderStatus = 'Processing' AND p.PayStatus = 'Pending' " +
                            "ORDER BY c.ClientID";
                    break;
    
                case 4:
                    query = "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, " +
                            "       SUM(p.PayAmount_CAD) AS TotalOutstanding " +
                            "FROM Client c " +
                            "JOIN Payment p ON c.ClientID = p.ClientID " +
                            "WHERE p.PayStatus = 'Pending' " +
                            "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName " +
                            "ORDER BY TotalOutstanding DESC";
                    break;             
    
                    case 5:
                    query = "SELECT * FROM (" +
                            "   SELECT c.ClientID, " +
                            "          c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, " +
                            "          COUNT(b.BuyOrderID) AS TotalOrders, " +
                            "          SUM(b.OrdTotalAmount_CAD) AS TotalSpent " +
                            "   FROM Client c " +
                            "   JOIN BuyOrder b ON c.ClientID = b.ClientID " +
                            "   GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName " +
                            "   ORDER BY TotalSpent DESC" +
                            ") WHERE ROWNUM <= 5";
                    break;                
    
                case 6:
                    query = "SELECT TO_CHAR(b.OrderDate, 'YYYY-MM') AS Month, SUM(b.OrdTotalAmount_CAD) AS TotalRevenue " +
                            "FROM BuyOrder b WHERE b.OrderStatus = 'Completed' " +
                            "GROUP BY TO_CHAR(b.OrderDate, 'YYYY-MM') " +
                            "ORDER BY Month ASC";
                    break;
    
                case 7:
                    query = "SELECT * FROM (" +
                            "   SELECT g.GownID, " +
                            "          g.GownDesc.GownStyle AS GownStyle, " +
                            "          COUNT(a.AlterationID) AS TotalAlterations, " +
                            "          SUM(a.Cost_CAD) AS TotalCost " +
                            "   FROM Gown g " +
                            "   JOIN BuyItems bi ON g.GownID = bi.GownID " +
                            "   JOIN Alterations a ON bi.BuyItemID = a.BuyItemID " +
                            "   GROUP BY g.GownID, g.GownDesc.GownStyle " +
                            "   ORDER BY TotalAlterations DESC, TotalCost DESC" +
                            ") WHERE ROWNUM <= 5";
                    break;

    
                case 8:
                    System.out.println("Returning to Main Menu...");
                    inReportMenu = false;
                    continue;
    
                default:
                    System.out.println("Invalid choice. Please enter a number between 1-9.");
                    continue;
            }
    
            // Execute the query and display the results
            if (query != null) {
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
    
                    ResultSetMetaData rsMeta = rs.getMetaData();
                    int columnCount = rsMeta.getColumnCount();
    
                    // Print column headers
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.printf("%-25s", rsMeta.getColumnName(i));
                    }
                    System.out.println();
    
                    // Print row data
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.printf("%-25s", rs.getString(i) != null ? rs.getString(i) : "N/A");
                        }
                        System.out.println();
                    }
                } catch (SQLException e) {
                    System.out.println("Error generating report.");
                    e.printStackTrace();
                }
            }
    
            System.out.println("\nReturning to Report Menu...");
        }
    }    
    
}