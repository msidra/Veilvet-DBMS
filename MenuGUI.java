/*  
    Assignment 10 
    Section 02
    CPS510

    Group 9: Sidra Musheer (501122840)
             Maryam Fahmi  (501096276)
             Veezish Ahmad (501080184)
*/ 

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MenuGUI {
    static final String JDBC_URL = "jdbc:oracle:thin:@oracle.scs.ryerson.ca:1521:ORCL";
    static final String USER = "smusheer";
    static final String PASSWORD = "12162840";

    private Connection connection;

    public MenuGUI() {
        // Establish database connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            JOptionPane.showMessageDialog(null, "Connected to the database!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void createGUI() {
        // Main frame
        JFrame frame = new JFrame("Database Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Panel for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        // Buttons
        JButton createTablesButton = new JButton("Create Tables");
        JButton dropTablesButton = new JButton("Drop Tables");
        JButton populateTablesButton = new JButton("Populate Tables");
        JButton queryTablesButton = new JButton("Query Tables");
        JButton generateReportsButton = new JButton("Generate Reports");
        JButton exitButton = new JButton("Exit");

        // Add action listeners to buttons
        createTablesButton.addActionListener(e -> createTables());
        dropTablesButton.addActionListener(e -> dropTables());
        populateTablesButton.addActionListener(e -> populateTables());
        queryTablesButton.addActionListener(e -> openQueryGUI());
        generateReportsButton.addActionListener(e -> generateReports());
        exitButton.addActionListener(e -> {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        // Add buttons to panel
        panel.add(createTablesButton);
        panel.add(dropTablesButton);
        panel.add(populateTablesButton);
        panel.add(queryTablesButton);
        panel.add(generateReportsButton);
        panel.add(exitButton);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);
    }

    private void createTables() {
        SwingUtilities.invokeLater(() -> {
            try {
                Menu.createTables(connection);
                JOptionPane.showMessageDialog(null, "Tables created successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error creating tables!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void dropTables() {
        SwingUtilities.invokeLater(() -> {
            try {
                Menu.dropTables(connection);
                JOptionPane.showMessageDialog(null, "Tables dropped successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error dropping tables!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void populateTables() {
        SwingUtilities.invokeLater(() -> {
            try {
                Menu.populateTables(connection);
                JOptionPane.showMessageDialog(null, "Tables populated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error populating tables!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void openQueryGUI() {
        SwingUtilities.invokeLater(() -> {
            QueryGUI queryApp = new QueryGUI(connection);
            queryApp.createGUI();
        });
    }    
    

    private void generateReports() {
        SwingUtilities.invokeLater(() -> {
            String[] reports = {
                "Revenue by Client", 
                "Top-Selling Items", 
                "Pending Payments", 
                "Clients with Outstanding Payments and Total Dues", 
                "Top Clients by Total Orders and Spending", 
                "Monthly Revenue Breakdown", 
                "Most Altered Gowns and Alteration Costs"
            };
            String reportChoice = (String) JOptionPane.showInputDialog(null, "Choose a report:", "Generate Reports",
                    JOptionPane.QUESTION_MESSAGE, null, reports, reports[0]);
    
            if (reportChoice != null) {
                String query = null;
                switch (reportChoice) {
                    case "Revenue by Client":
                        query = "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, SUM(p.PayAmount_CAD) AS TotalPayments " +
                                "FROM Client c JOIN Payment p ON c.ClientID = p.ClientID GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName ORDER BY TotalPayments DESC";
                        break;

                    case "Top-Selling Items":
                        query = "SELECT g.GownID, g.GownDesc.GownStyle, SUM(bi.Quantity) AS TotalSold " +
                                "FROM Gown g JOIN BuyItems bi ON g.GownID = bi.GownID GROUP BY g.GownID, g.GownDesc.GownStyle ORDER BY TotalSold DESC";
                        break;

                    case "Pending Payments":
                        query = "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, b.BuyOrderID, b.OrderStatus, p.PayStatus " +
                                "FROM Client c JOIN BuyOrder b ON c.ClientID = b.ClientID JOIN Payment p ON b.BuyOrderID = p.BuyOrderID " +
                                "WHERE b.OrderStatus = 'Processing' AND p.PayStatus = 'Pending' ORDER BY c.ClientID";
                        break;

                    case "Clients with Outstanding Payments and Total Dues":
                        query = "SELECT c.ClientID, c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, SUM(p.PayAmount_CAD) AS TotalOutstanding " +
                                "FROM Client c JOIN Payment p ON c.ClientID = p.ClientID WHERE p.PayStatus = 'Pending' " +
                                "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName ORDER BY TotalOutstanding DESC";
                        break;

                    case "Top Clients by Total Orders and Spending":
                        query = "SELECT * FROM ( " +
                                "SELECT c.ClientID, " +
                                "       c.FullName.FirstName || ' ' || c.FullName.LastName AS FullName, " +
                                "       COUNT(b.BuyOrderID) AS TotalOrders, " +
                                "       SUM(b.OrdTotalAmount_CAD) AS TotalSpent " +
                                "FROM Client c " +
                                "JOIN BuyOrder b ON c.ClientID = b.ClientID " +
                                "GROUP BY c.ClientID, c.FullName.FirstName, c.FullName.LastName " +
                                "ORDER BY TotalSpent DESC " +
                                ") WHERE ROWNUM <= 5";
                        break;
                    
                    case "Monthly Revenue Breakdown":
                        query = "SELECT TO_CHAR(b.OrderDate, 'YYYY-MM') AS Month, SUM(b.OrdTotalAmount_CAD) AS TotalRevenue " +
                                "FROM BuyOrder b WHERE b.OrderStatus = 'Completed' GROUP BY TO_CHAR(b.OrderDate, 'YYYY-MM') ORDER BY Month ASC";
                        break;

                    case "Most Altered Gowns and Alteration Costs":
                        query = "SELECT * FROM ( " +
                                "SELECT g.GownID, g.GownDesc.GownStyle AS GownStyle, COUNT(a.AlterationID) AS TotalAlterations, " +
                                "       SUM(a.Cost_CAD) AS TotalCost " +
                                "FROM Gown g " +
                                "JOIN BuyItems bi ON g.GownID = bi.GownID " +
                                "JOIN Alterations a ON bi.BuyItemID = a.BuyItemID " +
                                "GROUP BY g.GownID, g.GownDesc.GownStyle " +
                                "ORDER BY TotalAlterations DESC, TotalCost DESC " +
                                ") WHERE ROWNUM <= 5";
                        break;
                    
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid report selection.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }
    
                if (query != null) {
                    try (Statement stmt = connection.createStatement();
                         ResultSet rs = stmt.executeQuery(query)) {
    
                        // Use JTable to display results
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
    
                        // Extract column names
                        String[] columnNames = new String[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            columnNames[i - 1] = metaData.getColumnName(i);
                        }
    
                        // Extract data rows
                        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
                        while (rs.next()) {
                            Object[] rowData = new Object[columnCount];
                            for (int i = 1; i <= columnCount; i++) {
                                rowData[i - 1] = rs.getObject(i);
                            }
                            tableModel.addRow(rowData);
                        }
    
                        JTable table = new JTable(tableModel);
                        JScrollPane scrollPane = new JScrollPane(table);
                        JOptionPane.showMessageDialog(null, scrollPane, "Report Results", JOptionPane.INFORMATION_MESSAGE);
    
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, "Error executing query:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuGUI app = new MenuGUI();
            app.createGUI();
        });
    }
}
