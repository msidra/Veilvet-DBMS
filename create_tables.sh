#!/bin/sh
export LD_LIBRARY_PATH=/usr/lib/oracle/12.1/client64/lib
sqlplus64 smusheer/12162840@'(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=oracle.scs.ryerson.ca)(Port=1521))(CONNECT_DATA=(SID=orcl)))' <<EOF

CREATE TABLE Veil (
    VeilID NUMBER NOT NULL PRIMARY KEY,
    VPrice_CAD NUMBER(10, 2),
    VMaterial VARCHAR2(50),
    VLength_cm NUMBER,
    VColor VARCHAR2(20),
    VStockQuantity NUMBER,
    VLastRestocked DATE,
    VMinimumQuantity NUMBER
);

CREATE TABLE Gown (
    GownID NUMBER NOT NULL PRIMARY KEY,
    GPrice_CAD NUMBER(10, 2),
    GMaterial VARCHAR2(50),
    Neckline VARCHAR2(20),
    GownStyle VARCHAR2(50),
    Embellishments VARCHAR2(50),
    GColor VARCHAR2(20),
    GStockQuantity NUMBER,
    GLastRestocked DATE,
    GMinimumQuantity NUMBER,
    GBust_cm VARCHAR2(20),
    GWaist_cm VARCHAR2(20),
    GHips_cm VARCHAR2(20),
    GShoulders_cm VARCHAR2(20),
    GSleeves_cm VARCHAR2(20),
    GLength_cm VARCHAR2(20)
);

CREATE TABLE Client (
    ClientID NUMBER NOT NULL PRIMARY KEY,
    FirstName VARCHAR2(50),
    LastName VARCHAR2(50),
    Email VARCHAR2(100),
    Phone NUMBER(10),
    Street VARCHAR2(100),
    City VARCHAR2(50),
    Province VARCHAR2(2),
    PC VARCHAR2(7),
    Country VARCHAR2(50),
    CBust_cm VARCHAR2(20),
    CWaist_cm VARCHAR2(20),
    CHips_cm VARCHAR2(20),
    CShoulders_cm VARCHAR2(20),
    CSleeves_cm VARCHAR2(20),
    CLength_cm VARCHAR2(20)
);

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
exit;
EOF