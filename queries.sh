#!/bin/sh
export LD_LIBRARY_PATH=/usr/lib/oracle/12.1/client64/lib
sqlplus64 smusheer/12162840@'(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=oracle.scs.ryerson.ca)(Port=1521))(CONNECT_DATA=(SID=orcl)))' <<EOF

-- Query 1 for Veil Table: Display all veils in stock ordered by price.
SELECT DISTINCT v.VeilID, v.VPrice_CAD, v.VStockQuantity, v.VMaterial, v.VColor
FROM Veil v
WHERE v.VStockQuantity > 0
ORDER BY v.VPrice_CAD DESC;

-- Query 2 for Veil Table: Group by VMaterial to show total stock for each material.
SELECT v.VMaterial, SUM(v.VStockQuantity) AS TotalStock
FROM Veil v
GROUP BY v.VMaterial;

-- Query 1 for Gown Table: Display gowns with stock greater than a minimum quantity.
SELECT g.GownID, g.GPrice_CAD, g.GStockQuantity, g.GownStyle, g.GBust_cm, g.GWaist_cm
FROM Gown g
WHERE g.GStockQuantity > g.GMinimumQuantity
ORDER BY g.GStockQuantity DESC;

-- Query 2 for Gown Table: Count the number of gowns by GownStyle.
SELECT g.GownStyle, COUNT(g.GownID) AS GownCount
FROM Gown g
GROUP BY g.GownStyle;

-- Query 1 for Client Table: List all clients with their full name and email.
SELECT c.ClientID, c.FirstName || ' ' || c.LastName AS FullName, c.Email, c.LastName
FROM Client c
ORDER BY LastName;

-- Query 2 for Client Table: Group by City to count the number of clients in each city.
SELECT c.City, COUNT(c.ClientID) AS ClientCount
FROM Client c
GROUP BY c.City;

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
SELECT c.ClientID, c.FirstName || ' ' || c.LastName AS FullName, 
       b.BuyOrderID, b.OrdTotalAmount_CAD, p.PayAmount_CAD
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN Payment p ON b.BuyOrderID = p.BuyOrderID
ORDER BY b.OrdTotalAmount_CAD DESC;

-- Advanced Query 2: Display rental orders along with client details and payment info.
SELECT r.RentalOrderID, c.FirstName || ' ' || c.LastName AS FullName, 
       r.RentTotalAmount_CAD, p.PayAmount_CAD
FROM RentalOrder r
JOIN Client c ON r.ClientID = c.ClientID
JOIN Payment p ON r.RentalOrderID = p.RentalOrderID
ORDER BY r.RentTotalAmount_CAD DESC;

-- Advanced Join Query 3: Show client details along with total payment amount across all orders.
SELECT c.ClientID, c.FirstName || ' ' || c.LastName AS FullName, 
       SUM(p.PayAmount_CAD) AS TotalPayments
FROM Client c
JOIN Payment p ON c.ClientID = p.ClientID
GROUP BY c.ClientID, c.FirstName, c.LastName
ORDER BY TotalPayments DESC;

-- Advanced Query 4: Retrieve client name, buy and rental order IDs, total amounts for both buy and rental orders, total payment amounts, and total number of buy and rental items for each client
SELECT c.ClientID, c.FirstName || ' ' || c.LastName AS FullName, 
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
GROUP BY c.ClientID, c.FirstName, c.LastName, b.BuyOrderID, r.RentalOrderID, 
         b.OrdTotalAmount_CAD, r.RentTotalAmount_CAD, p.PayAmount_CAD
HAVING COUNT(bi.BuyItemID) > 0 AND COUNT(ri.RentalItemsID) > 0
ORDER BY c.LastName;

-- Advanced Query 5: Clients with Buy and Rental Orders Total Amount
SELECT c.ClientID, 
       c.FirstName || ' ' || c.LastName AS FullName,
       SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) AS TotalAmountSpent
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN RentalOrder r ON c.ClientID = r.ClientID
GROUP BY c.ClientID, c.FirstName, c.LastName
HAVING SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) > 0
ORDER BY TotalAmountSpent DESC;

-- Advanced Query 6: Top 3 Most Frequently Ordered Gowns
SELECT GownID, GownStyle, TotalQuantity
FROM (
    SELECT g.GownID, g.GownStyle AS GownStyle, SUM(bi.Quantity) AS TotalQuantity
    FROM Gown g
    JOIN BuyItems bi ON g.GownID = bi.GownID
    GROUP BY g.GownID, g.GownStyle
    ORDER BY TotalQuantity DESC
)
WHERE ROWNUM <= 3;

-- Advanced Query 7: Clients with Payments and Pending Orders
SELECT c.ClientID, 
       c.FirstName || ' ' || c.LastName AS FullName, 
       b.BuyOrderID, b.OrderStatus, p.PayStatus
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN Payment p ON b.BuyOrderID = p.BuyOrderID
WHERE b.OrderStatus = 'Processing' AND p.PayStatus = 'Paid'
ORDER BY c.ClientID;

-- Advanced Query 8: Total Buy Orders and Amount by Client City
SELECT c.City, COUNT(b.BuyOrderID) AS TotalOrders, SUM(b.OrdTotalAmount_CAD) AS TotalSpent
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
GROUP BY c.City
ORDER BY TotalOrders DESC;

-- Advanced Query 9: Rental Orders Statistics by City
SELECT c.City, 
       AVG(r.RentTotalAmount_CAD) AS AvgRentAmount, 
       MIN(r.RentTotalAmount_CAD) AS MinRentAmount, 
       MAX(r.RentTotalAmount_CAD) AS MaxRentAmount
FROM Client c
JOIN RentalOrder r ON c.ClientID = r.ClientID
GROUP BY c.City
ORDER BY AvgRentAmount DESC;

exit;
EOF