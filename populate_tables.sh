#!/bin/sh
export LD_LIBRARY_PATH=/usr/lib/oracle/12.1/client64/lib
sqlplus64 smusheer/12162840@'(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=oracle.scs.ryerson.ca)(Port=1521))(CONNECT_DATA=(SID=orcl)))' <<EOF

-- Insert into Veil
INSERT INTO Veil (VeilID, VPrice_CAD, VMaterial, VLength_cm, VColor, VStockQuantity, VLastRestocked, VMinimumQuantity)
VALUES (1, 250.00, 'Silk', 180, 'Ivory', 10, TO_DATE('2024-01-01', 'YYYY-MM-DD'), 5);

INSERT INTO Veil (VeilID, VPrice_CAD, VMaterial, VLength_cm, VColor, VStockQuantity, VLastRestocked, VMinimumQuantity)
VALUES (2, 300.00, 'Lace', 200, 'White', 8, TO_DATE('2024-02-01', 'YYYY-MM-DD'), 4);

-- Insert into Gown
INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GMaterial, Neckline, GownStyle, Embellishments, GColor, GLastRestocked, GMinimumQuantity, GBust_cm, GWaist_cm, GHips_cm, GShoulders_cm, GSleeves_cm, GLength_cm)
VALUES (1, 1500.00, 5, 'Satin', 'Sweetheart', 'Ball Gown', 'Beads', 'Ivory', TO_DATE('2024-01-15', 'YYYY-MM-DD'), 2, '36.0', '28.0', '38.0', '14.0', '24.0', '62.0');

INSERT INTO Gown (GownID, GPrice_CAD, GStockQuantity, GMaterial, Neckline, GownStyle, Embellishments, GColor, GLastRestocked, GMinimumQuantity, GBust_cm, GWaist_cm, GHips_cm, GShoulders_cm, GSleeves_cm, GLength_cm)
VALUES (2, 2000.00, 3, 'Tulle', 'V-Neck', 'A-Line', 'Sequins', 'Champagne', TO_DATE('2024-02-10', 'YYYY-MM-DD'), 1, '34.0', '26.0', '36.0', '13.5', '23.0', '60.0');

-- Insert into Client
INSERT INTO Client (ClientID, FirstName, LastName, Email, Phone, Street, City, Province, PC, Country, CBust_cm, CWaist_cm, CHips_cm, CShoulders_cm, CSleeves_cm, CLength_cm)
VALUES (1, 'Justin', 'Trudeau', 'j.true@example.com', 1234567890, '123 Bridal Lane', 'Toronto', 'ON', 'M5A 1B1', 'Canada', '34.0', '26.5', '36.0', '14.5', '22.0', '60.0');

INSERT INTO Client (ClientID, FirstName, LastName, Email, Phone, Street, City, Province, PC, Country, CBust_cm, CWaist_cm, CHips_cm, CShoulders_cm, CSleeves_cm, CLength_cm)
VALUES (2, 'Chanandler', 'Bing', 'channie.bing@example.com', 9876543210, '456 Wedding St', 'Mississauga', 'ON', 'L5N 2T3', 'Canada', '36.0', '28.0', '38.0', '15.0', '23.5', '61.0');

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