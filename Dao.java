package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
    static Connection connect = null;
    Statement statement = null;

    public Dao() {
        
    }

    public Connection getConnection() {
        try {
        	connect = DriverManager.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false&user=fp411&password=411&zeroDateTimeBehavior=CONVERT_TO_NULL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connect;
    }

    public void createTables() {
        final String createTicketsTable = "CREATE TABLE IF NOT EXISTS ptroj_tickets1(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), open_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, close_date TIMESTAMP)";
        final String createUsersTable = "CREATE TABLE IF NOT EXISTS ptroj_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

        try {
            statement = getConnection().createStatement();
            statement.executeUpdate(createTicketsTable);
            statement.executeUpdate(createUsersTable);
            System.out.println("Created tables in the database...");

            statement.close();
            connect.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        addUsers();
    }

    public void addUsers() {
        String sql;
        Statement statement;
        BufferedReader br;
        List<List<String>> array = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(new File("./userlist.csv")));
            String line;
            while ((line = br.readLine()) != null) {
                array.add(Arrays.asList(line.split(",")));
            }
        } catch (Exception e) {
            System.out.println("There was a problem loading the file");
        }

        try {
            statement = getConnection().createStatement();
            for (List<String> rowData : array) {
                sql = "INSERT INTO ptroj_users(uname, upass, admin) VALUES (?, ?, ?)";
                PreparedStatement pstmt = connect.prepareStatement(sql);
                pstmt.setString(1, rowData.get(0));
                pstmt.setString(2, rowData.get(1));
                pstmt.setInt(3, Integer.parseInt(rowData.get(2)));
                pstmt.executeUpdate();
            }
            System.out.println("Inserts completed in the database...");

            statement.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int insertTicket(String ticketName, String ticketDesc) {
        int id = 0;
        try {
            String sql = "INSERT INTO ptroj_tickets1(ticket_issuer, ticket_description, open_date) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, ticketName);
            pstmt.setString(2, ticketDesc);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Set the current date and time
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public ResultSet readTickets() {
        ResultSet results = null;
        try {
            statement = connect.createStatement();
            results = statement.executeQuery("SELECT * FROM ptroj_tickets1");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return results;
    }

    public ResultSet readTicket(int ticketId) {
        ResultSet result = null;
        String query = "SELECT * FROM ptroj_tickets1 WHERE ticket_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, ticketId);
            result = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void updateTicket(int ticketId, String newDescription) {
        String query = "UPDATE ptroj_tickets1 SET ticket_description = ? WHERE ticket_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, newDescription);
            pstmt.setInt(2, ticketId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTicket(int ticketId) {
        String query = "DELETE FROM ptroj_tickets1 WHERE ticket_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, ticketId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ResultSet readUserTickets(String userName) {
        ResultSet results = null;
        try {
            String query = "SELECT * FROM ptroj_tickets1 WHERE ticket_issuer = ?";
            PreparedStatement pstmt = connect.prepareStatement(query);
            pstmt.setString(1, userName);
            results = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void closeTicket(int ticketId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            String sql = "UPDATE ptroj_tickets1 SET close_date = ? WHERE ticket_id = ?";
            PreparedStatement pstmt = connect.prepareStatement(sql);
            pstmt.setString(1, formattedDateTime);
            pstmt.setInt(2, ticketId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connect != null) {
                connect.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
