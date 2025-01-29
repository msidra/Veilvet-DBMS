import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomQueryGUI {
    private Connection connection;

    // Constructor to accept a database connection
    public CustomQueryGUI(Connection connection) {
        this.connection = connection;
    }

    public void createGUI() {
        // Main frame
        JFrame frame = new JFrame("Custom SQL Query Executor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        // Panel for user input and button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel inputLabel = new JLabel("Enter your SQL query:");
        JTextField queryInputField = new JTextField(50);
        JButton executeButton = new JButton("Execute Query");

        // Text area for results
        JTextArea resultArea = new JTextArea(20, 70);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add components to input panel
        inputPanel.add(inputLabel);
        inputPanel.add(queryInputField);
        inputPanel.add(executeButton);

        // Add panel and scroll area to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add event listener for the execute button
        executeButton.addActionListener(e -> {
            String customQuery = queryInputField.getText().trim();
            if (!customQuery.isEmpty()) {
                executeSQLQuery(customQuery, resultArea);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a valid SQL query!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private void executeSQLQuery(String query, JTextArea resultArea) {
        try {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Database connection is not established.");
            }

            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                StringBuilder results = new StringBuilder();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Display column headers
                for (int i = 1; i <= columnCount; i++) {
                    results.append(String.format("%-20s", metaData.getColumnName(i))); // Format for better alignment
                }
                results.append("\n");

                // Display rows
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        results.append(String.format("%-20s", rs.getString(i)));
                    }
                    results.append("\n");
                }

                resultArea.setText(results.toString());
            }
        } catch (SQLException e) {
            resultArea.setText("Error executing query:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String JDBC_URL = "jdbc:oracle:thin:@oracle.scs.ryerson.ca:1521:ORCL";
        String USER = "smusheer"; 
        String PASSWORD = "12162840"; 

        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            SwingUtilities.invokeLater(() -> {
                CustomQueryGUI app = new CustomQueryGUI(connection);
                app.createGUI();
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
