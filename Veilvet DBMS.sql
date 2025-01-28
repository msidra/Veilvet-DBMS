/*
Assignment 10 
Section 02
CPS 510

Group: 09 
Members: Sidra Musheer      (501122840)
         Maryam Fahmi       (501096276)
         Veezish Ahmad      (501080184)
*/

-- Types for Grouped Attributes

-- Type for Veil Description
CREATE OR REPLACE TYPE VeilDescType AS OBJECT (
    VMaterial VARCHAR2(50),
    VLength_cm NUMBER,
    VColor VARCHAR2(20)
);

-- Type for Gown Description
CREATE OR REPLACE TYPE GownDescType AS OBJECT (
    GMaterial VARCHAR2(50),
    Neckline VARCHAR2(20),
    GownStyle VARCHAR2(50),
    Embellishments VARCHAR2(50),
    GColor VARCHAR2(20)
);

-- Type for Client Name (First Name, Last Name)
CREATE OR REPLACE TYPE NameType AS OBJECT (
    FirstName VARCHAR2(50),
    LastName VARCHAR2(50)
);

-- Type for Client Measurements
CREATE OR REPLACE TYPE CMeasurementsType AS OBJECT (
    CBust_cm VARCHAR2(20),
    CWaist_cm VARCHAR2(20),
    CHips_cm VARCHAR2(20),
    CShoulders_cm VARCHAR2(20),
    CSleeves_cm VARCHAR2(20),
    CLength_cm VARCHAR2(20)
);

-- Type for Gown Measurements
CREATE OR REPLACE TYPE GMeasurementsType AS OBJECT (
    GBust_cm VARCHAR2(20),
    GWaist_cm VARCHAR2(20),
    GHips_cm VARCHAR2(20),
    GShoulders_cm VARCHAR2(20),
    GSleeves_cm VARCHAR2(20),
    GLength_cm VARCHAR2(20)
);

-- Type for Client Address
CREATE OR REPLACE TYPE AddressType AS OBJECT (
    Street VARCHAR2(100),
    City VARCHAR2(50),
    Province VARCHAR2(2),
    PC VARCHAR2(7),  -- Postal Code (7 characters)
    Country VARCHAR2(50)
);

-- Create Tables

-- Table: Veil
CREATE TABLE Veil (
    VeilID NUMBER NOT NULL PRIMARY KEY,
    VPrice_CAD NUMBER(10, 2),
    VeilDesc VeilDescType,
    VStockQuantity NUMBER,
    VLastRestocked DATE,
    VMinimumQuantity NUMBER
);

-- Table: Gown
CREATE TABLE Gown (
    GownID NUMBER NOT NULL PRIMARY KEY,
    GPrice_CAD NUMBER(10, 2),
    GStockQuantity NUMBER,
    GownDesc GownDescType,
    GLastRestocked DATE,
    GMinimumQuantity NUMBER,
    GMeasurements GMeasurementsType
);

-- Table: Client
CREATE TABLE Client (
    ClientID NUMBER NOT NULL PRIMARY KEY,
    FullName NameType,            
    Email VARCHAR2(100),
    Phone NUMBER(10),
    Address AddressType,          
    CMeasurements CMeasurementsType 
);

-- Table: BuyOrder
CREATE TABLE BuyOrder (
    BuyOrderID NUMBER NOT NULL PRIMARY KEY,
    ClientID NUMBER,
    OrderDate DATE,
    OrdTotalAmount_CAD NUMBER(10, 2),
    OrderStatus VARCHAR2(50),
    TaxAmount_CAD NUMBER(10, 2),
    DueDate DATE,
    FOREIGN KEY (ClientID) REFERENCES Client(ClientID)
);

-- Table: RentalOrder
CREATE TABLE RentalOrder (
    RentalOrderID NUMBER NOT NULL PRIMARY KEY,
    ClientID NUMBER,
    RentDate DATE,
    ReturnDate DATE,
    RentTotalAmount_CAD NUMBER(10, 2),
    RentStatus VARCHAR2(50),
    TaxAmount_CAD NUMBER(10, 2),
    FOREIGN KEY (ClientID) REFERENCES Client(ClientID)
);

-- Table: BuyItems
CREATE TABLE BuyItems (
    BuyItemID NUMBER NOT NULL PRIMARY KEY,
    BuyOrderID NUMBER,
    VeilID NUMBER,
    GownID NUMBER,
    Quantity NUMBER,
    ItemPrice_CAD NUMBER(10, 2),
    FOREIGN KEY (BuyOrderID) REFERENCES BuyOrder(BuyOrderID),
    FOREIGN KEY (VeilID) REFERENCES Veil(VeilID),
    FOREIGN KEY (GownID) REFERENCES Gown(GownID)
);

-- Table: RentalItems
CREATE TABLE RentalItems (
    RentalItemsID NUMBER NOT NULL PRIMARY KEY,
    RentalOrderID NUMBER,
    VeilID NUMBER,
    GownID NUMBER,
    Quantity NUMBER,
    RentalPrice_CAD NUMBER(10, 2),
    FOREIGN KEY (RentalOrderID) REFERENCES RentalOrder(RentalOrderID),
    FOREIGN KEY (VeilID) REFERENCES Veil(VeilID),
    FOREIGN KEY (GownID) REFERENCES Gown(GownID)
);

-- Table: Alterations
CREATE TABLE Alterations (
    AlterationID NUMBER NOT NULL PRIMARY KEY,
    BuyItemID NUMBER,
    AlterationDesc VARCHAR2(100),
    Cost_CAD NUMBER(10, 2),
    AltDueDate DATE,
    AltStatus VARCHAR2(50),
    AlterationCompletedDate DATE,
    FOREIGN KEY (BuyItemID) REFERENCES BuyItems(BuyItemID)
);

-- Table: Payment
CREATE TABLE Payment (
    PaymentID NUMBER NOT NULL PRIMARY KEY,
    BuyOrderID NUMBER,
    RentalOrderID NUMBER,
    ClientID NUMBER,
    PayAmount_CAD NUMBER(10, 2),
    PaymentDate DATE,
    PaymentType VARCHAR2(50),
    PayStatus VARCHAR2(50),
    FOREIGN KEY (BuyOrderID) REFERENCES BuyOrder(BuyOrderID),
    FOREIGN KEY (RentalOrderID) REFERENCES RentalOrder(RentalOrderID),
    FOREIGN KEY (ClientID) REFERENCES Client(ClientID)
);

-- Data Insertion

-- Insert into Veil
INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity)
VALUES (1, 250.00, VeilDescType('Silk', 180, 'Ivory'), 10, TO_DATE('2024-01-01', 'YYYY-MM-DD'), 5);

INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity)
VALUES (2, 300.00, VeilDescType('Lace', 200, 'White'), 8, TO_DATE('2024-02-01', 'YYYY-MM-DD'), 4);

-- Insert into Gown
INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements)
VALUES (1, 1500.00, 5, GownDescType('Satin', 'Sweetheart', 'Ball Gown', 'Beads', 'Ivory'), TO_DATE('2024-01-15', 'YYYY-MM-DD'), 2, 
        GMeasurementsType('36.0', '28.0', '38.0', '14.0', '24.0', '62.0'));

INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements)
VALUES (2, 2000.00, 3, GownDescType('Tulle', 'V-Neck', 'A-Line', 'Sequins', 'Champagne'), TO_DATE('2024-02-10', 'YYYY-MM-DD'), 1, 
        GMeasurementsType('34.0', '26.0', '36.0', '13.5', '23.0', '60.0'));

-- Insert into Client
INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements)
VALUES (
    1, 
    NameType('Justin', 'Trudeau'),  
    'j.true@example.com', 
    1234567890, 
    AddressType('123 Bridal Lane', 'Toronto', 'ON', 'M5A 1B1', 'Canada'), 
    CMeasurementsType('34.0', '26.5', '36.0', '14.5', '22.0', '60.0')
);

INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements)
VALUES (
    2, 
    NameType('Chanandler', 'Bing'),  
    'channie.bing@example.com', 
    9876543210, 
    AddressType('456 Wedding St', 'Mississauga', 'ON', 'L5N 2T3', 'Canada'), 
    CMeasurementsType('36.0', '28.0', '38.0', '15.0', '23.5', '61.0')
);

-- Insert into BuyOrder
INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate)
VALUES (1, 1, TO_DATE('2024-02-15', 'YYYY-MM-DD'), 2750.00, 'Completed', 100.00, TO_DATE('2024-02-20', 'YYYY-MM-DD'));

INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate)
VALUES (2, 2, TO_DATE('2024-02-16', 'YYYY-MM-DD'), 1800.00, 'Processing', 80.00, TO_DATE('2024-02-25', 'YYYY-MM-DD'));

INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate)
VALUES (3, 1, TO_DATE('2024-03-01', 'YYYY-MM-DD'), 2500.00, 'Processing', 125.00, TO_DATE('2024-03-10', 'YYYY-MM-DD'));

-- Insert into RentalOrder
INSERT INTO RentalOrder (RentalOrderID, ClientID, RentDate, ReturnDate, RentTotalAmount_CAD, RentStatus, TaxAmount_CAD)
VALUES (3, 1, TO_DATE('2024-03-05', 'YYYY-MM-DD'), TO_DATE('2024-03-15', 'YYYY-MM-DD'), 800.00, 'Completed', 40.00);

-- Insert into BuyItems
INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (1, 1, NULL, 1, 1, 1500.00);

INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (2, 2, 2, 2, 1, 1800.00);

INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (3, 3, 1, 1, 2, 2500.00);

-- Insert into RentalItems
INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD)
VALUES (3, 3, 2, 2, 1, 800.00);

-- Insert into Payment
INSERT INTO Payment (PaymentID, BuyOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (1, 1, 1, 2750.00, TO_DATE('2024-02-15', 'YYYY-MM-DD'), 'Credit Card', 'Paid');

INSERT INTO Payment (PaymentID, BuyOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (2, 2, 2, 1800.00, TO_DATE('2024-02-16', 'YYYY-MM-DD'), 'PayPal', 'Pending');

INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (3, 3, NULL, 1, 2500.00, TO_DATE('2024-03-01', 'YYYY-MM-DD'), 'Credit Card', 'Paid');

INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (4, NULL, 3, 1, 800.00, TO_DATE('2024-03-05', 'YYYY-MM-DD'), 'Credit Card', 'Paid');

INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements)
VALUES (
    3, 
    NameType('Monica', 'Geller'),  
    'monica.geller@example.com', 
    1112223333, 
    AddressType('789 Fancy Ave', 'Toronto', 'ON', 'M5B 2H1', 'Canada'), 
    CMeasurementsType('35.0', '27.0', '37.0', '14.0', '22.5', '59.0')
);

INSERT INTO Client (ClientID, FullName, Email, Phone, Address, CMeasurements)
VALUES (
    4, 
    NameType('Rachel', 'Green'),  
    'rachel.green@example.com', 
    9998887777, 
    AddressType('101 Fashion St', 'New York', 'NY', '10001', 'USA'), 
    CMeasurementsType('34.5', '25.0', '36.0', '13.5', '22.0', '60.0')
);

INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate)
VALUES (4, 3, TO_DATE('2024-03-10', 'YYYY-MM-DD'), 2200.00, 'Completed', 110.00, TO_DATE('2024-03-15', 'YYYY-MM-DD'));

INSERT INTO BuyOrder (BuyOrderID, ClientID, OrderDate, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, DueDate)
VALUES (5, 4, TO_DATE('2024-03-11', 'YYYY-MM-DD'), 1950.00, 'Processing', 90.00, TO_DATE('2024-03-20', 'YYYY-MM-DD'));

INSERT INTO RentalOrder (RentalOrderID, ClientID, RentDate, ReturnDate, RentTotalAmount_CAD, RentStatus, TaxAmount_CAD)
VALUES (4, 3, TO_DATE('2024-03-12', 'YYYY-MM-DD'), TO_DATE('2024-03-18', 'YYYY-MM-DD'), 750.00, 'Processing', 30.00);

INSERT INTO RentalOrder (RentalOrderID, ClientID, RentDate, ReturnDate, RentTotalAmount_CAD, RentStatus, TaxAmount_CAD)
VALUES (5, 4, TO_DATE('2024-03-13', 'YYYY-MM-DD'), TO_DATE('2024-03-20', 'YYYY-MM-DD'), 800.00, 'Completed', 40.00);

INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (4, 4, NULL, 1, 1, 1500.00);

INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (5, 5, 1, 2, 1, 1950.00);

INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD)
VALUES (4, 4, 1, 1, 1, 750.00);

INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD)
VALUES (5, 5, 2, 2, 1, 800.00);

INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (5, 4, NULL, 3, 2200.00, TO_DATE('2024-03-10', 'YYYY-MM-DD'), 'Credit Card', 'Paid');

INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (6, 5, NULL, 4, 1950.00, TO_DATE('2024-03-11', 'YYYY-MM-DD'), 'Credit Card', 'Pending');

INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (7, NULL, 4, 3, 750.00, TO_DATE('2024-03-12', 'YYYY-MM-DD'), 'Debit Card', 'Paid');

INSERT INTO Payment (PaymentID, BuyOrderID, RentalOrderID, ClientID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus)
VALUES (8, NULL, 5, 4, 800.00, TO_DATE('2024-03-13', 'YYYY-MM-DD'), 'Credit Card', 'Paid');

INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity)
VALUES (3, 275.00, VeilDescType('Tulle', 150, 'White'), 12, TO_DATE('2024-01-20', 'YYYY-MM-DD'), 6);

INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity)
VALUES (4, 320.00, VeilDescType('Chiffon', 160, 'Ivory'), 7, TO_DATE('2024-02-10', 'YYYY-MM-DD'), 3);

INSERT INTO Veil (VeilID, VPrice_CAD, VeilDesc, VStockQuantity, VLastRestocked, VMinimumQuantity)
VALUES (5, 350.00, VeilDescType('Organza', 180, 'Champagne'), 9, TO_DATE('2024-02-25', 'YYYY-MM-DD'), 4);

INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements)
VALUES (3, 1700.00, 4, GownDescType('Organza', 'Sweetheart', 'Mermaid', 'Lace', 'Ivory'), TO_DATE('2024-02-05', 'YYYY-MM-DD'), 2, GMeasurementsType('36.5', '27.0', '37.5', '14.0', '23.0', '60.0'));

INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements)
VALUES (4, 2500.00, 3, GownDescType('Satin', 'Off-Shoulder', 'Ball Gown', 'Beads', 'White'), TO_DATE('2024-03-01', 'YYYY-MM-DD'), 1, GMeasurementsType('34.0', '25.0', '36.0', '13.5', '22.5', '62.0'));

INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GownDesc, GLastRestocked, GMinimumQuantity, GMeasurements)
VALUES (5, 1900.00, 6, GownDescType('Tulle', 'Halter', 'A-Line', 'Sequins', 'Blush'), TO_DATE('2024-03-10', 'YYYY-MM-DD'), 3, GMeasurementsType('35.0', '26.5', '37.5', '14.0', '23.5', '61.0'));

INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (6, 4, 3, 3, 1, 1700.00);

INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (7, 4, 4, 4, 1, 2500.00);

INSERT INTO BuyItems (BuyItemID, BuyOrderID, VeilID, GownID, Quantity, ItemPrice_CAD)
VALUES (8, 5, 5, 5, 2, 3800.00);

INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD)
VALUES (6, 4, 3, 3, 1, 750.00);

INSERT INTO RentalItems (RentalItemsID, RentalOrderID, VeilID, GownID, Quantity, RentalPrice_CAD)
VALUES (7, 5, 4, 4, 1, 800.00);

-- Queries for each table

-- Query 1 for Veil Table: Display all veils in stock ordered by price.
SELECT DISTINCT v.VeilID, v.VPrice_CAD, v.VStockQuantity, 
       v.VeilDesc.VMaterial, v.VeilDesc.VColor
FROM Veil v
WHERE v.VStockQuantity > 0
ORDER BY v.VPrice_CAD DESC;

-- Query 2 for Veil Table: Group by VMaterial to show total stock for each material.
SELECT v.VeilDesc.VMaterial, SUM(v.VStockQuantity) AS TotalStock
FROM Veil v
GROUP BY v.VeilDesc.VMaterial;

-- Query 1 for Gown Table: Display gowns with stock greater than a minimum quantity.
SELECT g.GownID, g.GPrice_CAD, g.GStockQuantity, 
       g.GownDesc.GownStyle, g.GMeasurements.GBust_cm, g.GMeasurements.GWaist_cm
FROM Gown g
WHERE g.GStockQuantity > g.GMinimumQuantity
ORDER BY g.GStockQuantity DESC;

-- Query 2 for Gown Table: Count the number of gowns by GownStyle.
SELECT g.GownDesc.GownStyle, COUNT(g.GownID) AS GownCount
FROM Gown g
GROUP BY g.GownDesc.GownStyle;

-- Query 1 for Client Table: List all clients with their full name and email.
SELECT c.ClientID, 
       c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       c.Email, 
       c.FullName.LastName AS LastName
FROM Client c
ORDER BY LastName;

-- Query 2 for Client Table: Group by City to count the number of clients in each city.
SELECT c.Address.City, COUNT(c.ClientID) AS ClientCount
FROM Client c
GROUP BY c.Address.City;


-- Query 1 for BuyOrder Table: Show all buy orders along with the total amount and status.
SELECT b.BuyOrderID, b.ClientID, b.OrdTotalAmount_CAD, b.OrderStatus, b.TaxAmount_CAD, b.OrderDate
FROM BuyOrder b
ORDER BY b.OrdTotalAmount_CAD DESC;

-- Query 2 for BuyOrder Table: Show the total order amounts grouped by status.
SELECT b.OrderStatus, SUM(b.OrdTotalAmount_CAD) AS TotalAmount
FROM BuyOrder b
GROUP BY b.OrderStatus;

-- Query for RentalOrder Table: List all rental orders along with total rent amount and return date.
SELECT r.RentalOrderID, r.ClientID, r.RentTotalAmount_CAD, r.RentDate, r.ReturnDate
FROM RentalOrder r
ORDER BY r.RentTotalAmount_CAD DESC;


-- Query for BuyItems Table: Show all purchased items along with item price.
SELECT bi.BuyItemID, bi.BuyOrderID, bi.GownID, bi.Quantity, bi.ItemPrice_CAD
FROM BuyItems bi
ORDER BY bi.ItemPrice_CAD DESC;

-- Query for RentalItems Table: Show rental items along with rental price.
SELECT ri.RentalItemsID, ri.RentalOrderID, ri.GownID, ri.Quantity, ri.RentalPrice_CAD
FROM RentalItems ri
ORDER BY ri.RentalPrice_CAD DESC;

-- Query for Alterations Table: List all alterations with their cost and due date.
SELECT a.AlterationID, a.AlterationDesc, a.Cost_CAD, a.AltDueDate, a.AltStatus
FROM Alterations a
ORDER BY a.AltDueDate;

-- Query for Payment Table: Show all payments and their status.
SELECT p.PaymentID, p.PayAmount_CAD, p.PaymentDate, p.PaymentType, p.PayStatus
FROM Payment p
ORDER BY p.PaymentDate DESC;


-- Advanced Join Queries

-- Advanced Query 1: Display client details along with their buy orders and total amounts.
SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       b.BuyOrderID, b.OrdTotalAmount_CAD, p.PayAmount_CAD
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN Payment p ON b.BuyOrderID = p.BuyOrderID
ORDER BY b.OrdTotalAmount_CAD DESC;

-- Advanced Query 2: Display rental orders along with client details and payment info.
SELECT r.RentalOrderID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       r.RentTotalAmount_CAD, p.PayAmount_CAD
FROM RentalOrder r
JOIN Client c ON r.ClientID = c.ClientID
JOIN Payment p ON r.RentalOrderID = p.RentalOrderID
ORDER BY r.RentTotalAmount_CAD DESC;

-- Advanced Join Query 3: Show client details along with total payment amount across all orders.
SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       SUM(p.PayAmount_CAD) AS TotalPayments
FROM Client c
JOIN Payment p ON c.ClientID = p.ClientID
GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName
ORDER BY TotalPayments DESC;

-- Advanced Query 4: Retrieve client name, buy and rental order IDs, 
--               total amounts for both buy and rental orders, total payment amounts, 
--               and total number of buy and rental items for each client
SELECT c.ClientID, 
       c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       b.BuyOrderID, r.RentalOrderID,
       b.OrdTotalAmount_CAD AS BuyOrderTotal, 
       r.RentTotalAmount_CAD AS RentalOrderTotal, 
       p.PayAmount_CAD AS PaymentAmount,
       COUNT(bi.BuyItemID) AS TotalBuyItems,
       COUNT(ri.RentalItemsID) AS TotalRentalItems
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN RentalOrder r ON c.ClientID = r.ClientID
JOIN Payment p ON p.ClientID = c.ClientID
LEFT JOIN BuyItems bi ON b.BuyOrderID = bi.BuyOrderID
LEFT JOIN RentalItems ri ON r.RentalOrderID = ri.RentalOrderID
GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName, b.BuyOrderID, r.RentalOrderID, 
         b.OrdTotalAmount_CAD, r.RentTotalAmount_CAD, p.PayAmount_CAD
HAVING COUNT(bi.BuyItemID) > 0 AND COUNT(ri.RentalItemsID) > 0
ORDER BY c.FullName.LastName;

-- Advanced Query 5: Clients with Buy and Rental Orders Total Amount
SELECT c.ClientID, 
       c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName,
       SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) AS TotalAmountSpent
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN RentalOrder r ON c.ClientID = r.ClientID
GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName
HAVING SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) > 0
ORDER BY TotalAmountSpent DESC;

-- Advanced Query 6: Top 3 Most Frequently Ordered Gowns
SELECT GownID, GownStyle, TotalQuantity
FROM (
    SELECT g.GownID, g.GownDesc.GownStyle AS GownStyle, SUM(bi.Quantity) AS TotalQuantity
    FROM Gown g
    JOIN BuyItems bi ON g.GownID = bi.GownID
    GROUP BY g.GownID, g.GownDesc.GownStyle
    ORDER BY TotalQuantity DESC
)
WHERE ROWNUM <= 3;

-- Advanced Query 7: Clients with Payments and Pending Orders
SELECT c.ClientID, 
       c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       b.BuyOrderID, b.OrderStatus, p.PayStatus
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN Payment p ON b.BuyOrderID = p.BuyOrderID
WHERE b.OrderStatus = 'Processing' AND p.PayStatus = 'Paid'
ORDER BY c.ClientID;

-- Advanced Query 8: Total Buy Orders and Amount by Client City
SELECT c.Address.City, COUNT(b.BuyOrderID) AS TotalOrders, SUM(b.OrdTotalAmount_CAD) AS TotalSpent
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
GROUP BY c.Address.City
ORDER BY TotalOrders DESC;

-- Advanced Query 9: Rental Orders Statistics by City
SELECT c.Address.City, 
       AVG(r.RentTotalAmount_CAD) AS AvgRentAmount, 
       MIN(r.RentTotalAmount_CAD) AS MinRentAmount, 
       MAX(r.RentTotalAmount_CAD) AS MaxRentAmount
FROM Client c
JOIN RentalOrder r ON c.ClientID = r.ClientID
GROUP BY c.Address.City
ORDER BY AvgRentAmount DESC;

-- Views

-- View 1: Client Purchase Summary
CREATE OR REPLACE VIEW ClientPurchaseSummary AS
SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       b.OrdTotalAmount_CAD, p.PayAmount_CAD
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN Payment p ON b.BuyOrderID = p.BuyOrderID;

-- View 2: Gown Stock Summary
CREATE OR REPLACE VIEW GownStockSummary AS
SELECT g.GownID, 
       g.GPrice_CAD, 
       g.GStockQuantity, 
       g.GownDesc.GownStyle AS GownStyle,   
       g.GownDesc.GColor AS GownColor       
FROM Gown g
WHERE g.GStockQuantity > g.GMinimumQuantity;

-- View 3: Veil Sales Summary
CREATE OR REPLACE VIEW VeilSalesSummary AS
SELECT b.BuyOrderID, b.ClientID, v.VeilID, v.VPrice_CAD, bi.Quantity, bi.ItemPrice_CAD
FROM BuyOrder b
JOIN BuyItems bi ON b.BuyOrderID = bi.BuyOrderID
JOIN Veil v ON bi.VeilID = v.VeilID;

-- View 4: Payment Summary by Client
CREATE OR REPLACE VIEW PaymentSummaryByClient AS
SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, 
       SUM(p.PayAmount_CAD) AS TotalPayments
FROM Client c
JOIN Payment p ON c.ClientID = p.ClientID
GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName;

