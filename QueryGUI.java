/*
    Assignment 10
    Section 02
    CPS510

    Group 9: Sidra Musheer (501122840)
             Maryam Fahmi  (501096276)
             Veezish Ahmad (501080184)
*/

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class QueryGUI {
    private Connection connection;

    // Constructor to accept a database connection
    public QueryGUI(Connection connection) {
        this.connection = connection;
    }

    public void createGUI() {
        // Main frame
        JFrame frame = new JFrame("Query GUI");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        // Panel for dropdown and buttons
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Dropdown with predefined query descriptions
        String[] queryDescriptions = {
            "1. Veils in stock by price descending",
            "2. Total stock of veils by material",
            "3. Gowns with stock above minimum",
            "4. Count of gowns by style",
            "5. All clients and their details",
            "6. Client count by city",
            "7. Buy orders by total amount descending",
            "8. Total amount by order status",
            "9. Rental orders by total amount descending",
            "10. Buy items by item price descending",
            "11. Rental items by rental price descending",
            "12. Alterations by due date",
            "13. Payments by date descending",
            "14. Revenue by client and total payments",
            "15. Rental orders with client and payment details",
            "16. Total payments by client",
            "17. Buy and rental orders with items count",
            "18. Total amount spent by clients",
            "19. Top 3 best-selling gowns",
            "20. Processing orders with paid status",
            "21. Total orders and spent amount by city",
            "22. Rental amount by city (avg, min, max)"
        };

        JComboBox<String> queryDropdown = new JComboBox<>(queryDescriptions);
        JButton executeButton = new JButton("Execute Predefined Query");

        // Button for opening the CustomQueryGUI
        JButton customQueryButton = new JButton("Open Custom Query GUI");

        // Text area for results
        JTextArea resultArea = new JTextArea(20, 70);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Event for predefined queries
        executeButton.addActionListener(e -> {
            int selectedIndex = queryDropdown.getSelectedIndex();
            executePredefinedQuery(selectedIndex, resultArea);
        });

        // Add ActionListener to open CustomQueryGUI
        customQueryButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                CustomQueryGUI customQueryApp = new CustomQueryGUI(connection);
                customQueryApp.createGUI();
            });
        });

        // Panel for dropdown and predefined query execution
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(new JLabel("Select Predefined Query:"));
        controlPanel.add(queryDropdown);
        controlPanel.add(executeButton);

        // Add panels and components to the main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(customQueryButton, BorderLayout.SOUTH);
        frame.add(mainPanel, BorderLayout.NORTH);

        // Add scrollPane for results
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void executePredefinedQuery(int queryIndex, JTextArea resultArea) {
        String[] queries = {
            // Query 1: Veils in stock by price descending
                // RA: Π (VeilID, VPrice_CAD, VStockQuantity, VMaterial, VColor) (σ (VStockQuantity > 0) (Veil))
                "SELECT DISTINCT v.VeilID, v.VPrice_CAD, v.VStockQuantity, v.VeilDesc.VMaterial, v.VeilDesc.VColor " +
                "FROM Veil v WHERE v.VStockQuantity > 0 ORDER BY v.VPrice_CAD DESC",
    
                // Query 2: Total stock of veils by material
                // RA: Π (VMaterial, SUM(VStockQuantity)) (Veil)
                "SELECT v.VeilDesc.VMaterial, SUM(v.VStockQuantity) AS TotalStock " +
                "FROM Veil v GROUP BY v.VeilDesc.VMaterial",
    
                // Query 3: Gowns with stock above minimum
                // RA: Π (GownID, GPrice_CAD, GStockQuantity, GownStyle, GBust_cm, GWaist_cm) (σ (GStockQuantity > GMinimumQuantity) (Gown))
                "SELECT g.GownID, g.GPrice_CAD, g.GStockQuantity, g.GownDesc.GownStyle, g.GMeasurements.GBust_cm, g.GMeasurements.GWaist_cm " +
                "FROM Gown g WHERE g.GStockQuantity > g.GMinimumQuantity ORDER BY g.GStockQuantity DESC",
    
                // Query 4: Count of gowns by style
                // RA: Π (GownStyle, COUNT(GownID)) (Gown)
                "SELECT g.GownDesc.GownStyle, COUNT(g.GownID) AS GownCount " +
                "FROM Gown g GROUP BY g.GownDesc.GownStyle",
    
                // Query 5: All clients and their details
                // RA: Π (ClientID, FullName, Email, LastName) (Client)
                "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, c.Email, c.FullName.LastName AS LastName " +
                "FROM Client c ORDER BY LastName",
    
                // Query 6: Client count by city
                // RA: Π (City, COUNT(ClientID)) (Client)
                "SELECT c.Address.City, COUNT(c.ClientID) AS ClientCount " +
                "FROM Client c GROUP BY c.Address.City",
    
                // Query 7: Buy orders by total amount descending
                // RA: Π (BuyOrderID, ClientID, OrdTotalAmount_CAD, OrderStatus, TaxAmount_CAD, OrderDate) (BuyOrder)
                "SELECT b.BuyOrderID, b.ClientID, b.OrdTotalAmount_CAD, b.OrderStatus, b.TaxAmount_CAD, b.OrderDate " +
                "FROM BuyOrder b ORDER BY b.OrdTotalAmount_CAD DESC",
    
                // Query 8: Total amount by order status
                // RA: Π (OrderStatus, SUM(OrdTotalAmount_CAD)) (BuyOrder)
                "SELECT b.OrderStatus, SUM(b.OrdTotalAmount_CAD) AS TotalAmount " +
                "FROM BuyOrder b GROUP BY b.OrderStatus",
    
                // Query 9: Rental orders by total amount descending
                // RA: Π (RentalOrderID, ClientID, RentTotalAmount_CAD, RentDate, ReturnDate) (RentalOrder)
                "SELECT r.RentalOrderID, r.ClientID, r.RentTotalAmount_CAD, r.RentDate, r.ReturnDate " +
                "FROM RentalOrder r ORDER BY r.RentTotalAmount_CAD DESC",
    
                // Query 10: Buy items by item price descending
                // RA: Π (BuyItemID, BuyOrderID, GownID, Quantity, ItemPrice_CAD) (BuyItems)
                "SELECT bi.BuyItemID, bi.BuyOrderID, bi.GownID, bi.Quantity, bi.ItemPrice_CAD " +
                "FROM BuyItems bi ORDER BY bi.ItemPrice_CAD DESC",
    
                // Query 11: Rental items by rental price descending
                // RA: Π (RentalItemsID, RentalOrderID, GownID, Quantity, RentalPrice_CAD) (RentalItems)
                "SELECT ri.RentalItemsID, ri.RentalOrderID, ri.GownID, ri.Quantity, ri.RentalPrice_CAD " +
                "FROM RentalItems ri ORDER BY ri.RentalPrice_CAD DESC",
    
                // Query 12: Alterations by due date
                // RA: Π (AlterationID, AlterationDesc, Cost_CAD, AltDueDate, AltStatus) (Alterations)
                "SELECT a.AlterationID, a.AlterationDesc, a.Cost_CAD, a.AltDueDate, a.AltStatus " +
                "FROM Alterations a ORDER BY a.AltDueDate",
    
                // Query 13: Payments by date descending
                // RA: Π (PaymentID, PayAmount_CAD, PaymentDate, PaymentType, PayStatus) (Payment)
                "SELECT p.PaymentID, p.PayAmount_CAD, p.PaymentDate, p.PaymentType, p.PayStatus " +
                "FROM Payment p ORDER BY p.PaymentDate DESC",
    
                // Query 14: Revenue by client and total payments
                // RA: Π (ClientID, FullName, SUM(PayAmount_CAD)) (Client ⨝ Payment)
                "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, SUM(p.PayAmount_CAD) AS TotalPayments " +
                "FROM Client c JOIN Payment p ON c.ClientID = p.ClientID " +
                "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName ORDER BY TotalPayments DESC",
    
                // Query 15: Rental orders with client and payment details
                // RA: Π (RentalOrderID, FullName, RentTotalAmount_CAD, PayAmount_CAD) (RentalOrder ⨝ Client ⨝ Payment)
                "SELECT r.RentalOrderID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, r.RentTotalAmount_CAD, p.PayAmount_CAD " +
                "FROM RentalOrder r JOIN Client c ON r.ClientID = c.ClientID JOIN Payment p ON r.RentalOrderID = p.RentalOrderID " +
                "ORDER BY r.RentTotalAmount_CAD DESC",
    
                // Query 16: Total payments by client
                // RA: Π (ClientID, FullName, SUM(PayAmount_CAD)) (Client ⨝ Payment)
                "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, SUM(p.PayAmount_CAD) AS TotalPayments " +
                "FROM Client c JOIN Payment p ON c.ClientID = p.ClientID " +
                "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName ORDER BY TotalPayments DESC",
    
                // Query 17: Buy and rental orders with items count
                // RA: Complex join involving Client, BuyOrder, RentalOrder, BuyItems, RentalItems, and Payment
                "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, b.BuyOrderID, r.RentalOrderID, " +
                "b.OrdTotalAmount_CAD AS BuyOrderTotal, r.RentTotalAmount_CAD AS RentalOrderTotal, p.PayAmount_CAD AS PaymentAmount, " +
                "COUNT(bi.BuyItemID) AS TotalBuyItems, COUNT(ri.RentalItemsID) AS TotalRentalItems " +
                "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN RentalOrder r ON c.ClientID = r.ClientID " +
                "JOIN Payment p ON p.ClientID = c.ClientID LEFT JOIN BuyItems bi ON b.BuyOrderID = bi.BuyOrderID " +
                "LEFT JOIN RentalItems ri ON r.RentalOrderID = ri.RentalOrderID " +
                "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName, b.BuyOrderID, r.RentalOrderID, b.OrdTotalAmount_CAD, " +
                "r.RentTotalAmount_CAD, p.PayAmount_CAD HAVING COUNT(bi.BuyItemID) > 0 AND COUNT(ri.RentalItemsID) > 0 " +
                "ORDER BY c.FullName.LastName",
    
                // Query 18: Total amount spent by clients
                // RA: Π (ClientID, FullName, SUM(OrdTotalAmount_CAD + RentTotalAmount_CAD)) (Client ⨝ BuyOrder ⨝ RentalOrder)
                "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, " +
                "SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) AS TotalAmountSpent " +
                "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN RentalOrder r ON c.ClientID = r.ClientID " +
                "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName HAVING SUM(b.OrdTotalAmount_CAD + r.RentTotalAmount_CAD) > 0 " +
                "ORDER BY TotalAmountSpent DESC",
    
                // Query 19: Top 3 best-selling gowns
                // RA: Π (GownID, GownStyle, TotalQuantity) (Gown ⨝ BuyItems)
                "SELECT GownID, GownStyle, TotalQuantity FROM (" +
                "SELECT g.GownID, g.GownDesc.GownStyle AS GownStyle, SUM(bi.Quantity) AS TotalQuantity " +
                "FROM Gown g JOIN BuyItems bi ON g.GownID = bi.GownID " +
                "GROUP BY g.GownID, g.GownDesc.GownStyle ORDER BY TotalQuantity DESC) WHERE ROWNUM <= 3",
    
                // Query 20: Processing orders with paid status
                // RA: Π (ClientID, FullName, BuyOrderID, OrderStatus, PayStatus) (Client ⨝ BuyOrder ⨝ Payment)
                "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, b.BuyOrderID, b.OrderStatus, p.PayStatus " +
                "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN Payment p ON b.BuyOrderID = p.BuyOrderID " +
                "WHERE b.OrderStatus = 'Processing' AND p.PayStatus = 'Paid' ORDER BY c.ClientID",
    
                // Query 21: Total orders and spent amount by city
                // RA: Π (City, COUNT(BuyOrderID), SUM(OrdTotalAmount_CAD)) (Client ⨝ BuyOrder)
                "SELECT c.Address.City, COUNT(b.BuyOrderID) AS TotalOrders, SUM(b.OrdTotalAmount_CAD) AS TotalSpent " +
                "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID " +
                "GROUP BY c.Address.City ORDER BY TotalOrders DESC",
    
                // Query 22: Rental amount by city (avg, min, max)
                // RA: Π (City, AVG(RentTotalAmount_CAD), MIN(RentTotalAmount_CAD), MAX(RentTotalAmount_CAD)) (Client ⨝ RentalOrder)
                "SELECT c.Address.City, AVG(r.RentTotalAmount_CAD) AS AvgRentAmount, MIN(r.RentTotalAmount_CAD) AS MinRentAmount, " +
                "MAX(r.RentTotalAmount_CAD) AS MaxRentAmount " +
                "FROM Client c JOIN RentalOrder r ON c.ClientID = r.ClientID " +
                "GROUP BY c.Address.City ORDER BY AvgRentAmount DESC"
        };
        
        if (queryIndex < 0 || queryIndex >= queries.length) {
            resultArea.setText("Invalid query selection.");
            return;
        }
    
        String selectedQuery = queries[queryIndex]; // Select the query based on queryIndex
    
        try (Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(selectedQuery)) {
    
            StringBuilder results = new StringBuilder();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
    
            // Add column headers with proper alignment
            for (int i = 1; i <= columnCount; i++) {
                results.append(String.format("%-20s", metaData.getColumnName(i))); // Align headers
            }
            results.append("\n");
    
            // Add a separator line
            for (int i = 1; i <= columnCount; i++) {
                results.append("-".repeat(20));
            }
            results.append("\n");
    
            // Add rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i) != null ? rs.getString(i) : "NULL";
                    results.append(String.format("%-20s", value)); // Align rows
                }
                results.append("\n");
            }
    
            // Display the formatted results in the result area
            resultArea.setText(results.toString());
        } catch (SQLException e) {
            resultArea.setText("Error executing query:\n" + e.getMessage());
        }
    }
        

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@oracle.scs.ryerson.ca:1521:ORCL", 
                    "yourUsername", 
                    "yourPassword"
                );
                new QueryGUI(connection).createGUI();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
