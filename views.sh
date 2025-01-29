#!/bin/sh
export LD_LIBRARY_PATH=/usr/lib/oracle/12.1/client64/lib
sqlplus64 smusheer/12162840@'(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=oracle.scs.ryerson.ca)(Port=1521))(CONNECT_DATA=(SID=orcl)))' <<EOF

-- View 1: Client Purchase Summary
CREATE OR REPLACE VIEW ClientPurchaseSummary AS
SELECT c.ClientID, c.FirstName || ' ' || c.LastName AS FullName, 
       b.OrdTotalAmount_CAD, p.PayAmount_CAD
FROM Client c
JOIN BuyOrder b ON c.ClientID = b.ClientID
JOIN Payment p ON b.BuyOrderID = p.BuyOrderID;

-- View 2: Gown Stock Summary
CREATE OR REPLACE VIEW GownStockSummary AS
SELECT g.GownID, 
       g.GPrice_CAD, 
       g.GStockQuantity, 
       g.GownStyle AS GownStyle,   
       g.GColor AS GownColor       
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
SELECT c.ClientID, c.FirstName || ' ' || c.LastName AS FullName, 
       SUM(p.PayAmount_CAD) AS TotalPayments
FROM Client c
JOIN Payment p ON c.ClientID = p.ClientID
GROUP BY c.ClientID, c.FirstName, c.LastName;

exit;
EOF