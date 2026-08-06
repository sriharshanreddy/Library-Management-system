package dao;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    public Map<String, Object> getDashboardStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM Books) AS Total_Books, " +
                     "(SELECT COALESCE(SUM(Total_Copies),0) FROM Books) AS Total_Copies, " +
                     "(SELECT COALESCE(SUM(Available_Copies),0) FROM Books) AS Total_Available_Copies, " +
                     "(SELECT COUNT(*) FROM Students) AS Total_Students, " +
                     "(SELECT COUNT(*) FROM Librarians) AS Total_Librarians, " +
                     "(SELECT COUNT(*) FROM Book_Issue WHERE Status='Issued') AS Active_Issued, " +
                     "(SELECT COUNT(*) FROM Book_Return) AS Total_Returned, " +
                     "(SELECT COUNT(*) FROM Reservation WHERE Status='Reserved') AS Active_Reservations, " +
                     "(SELECT COALESCE(SUM(Fine_Amount),0) FROM Fine WHERE Paid_Status='Pending') AS Total_Pending_Fine, " +
                     "(SELECT COALESCE(SUM(Fine_Amount),0) FROM Fine WHERE Paid_Status='Paid') AS Total_Paid_Fine";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                stats.put("totalBooks", resultSet.getInt("Total_Books"));
                stats.put("totalCopies", resultSet.getInt("Total_Copies"));
                stats.put("availableCopies", resultSet.getInt("Total_Available_Copies"));
                stats.put("totalStudents", resultSet.getInt("Total_Students"));
                stats.put("totalLibrarians", resultSet.getInt("Total_Librarians"));
                stats.put("activeIssued", resultSet.getInt("Active_Issued"));
                stats.put("totalReturned", resultSet.getInt("Total_Returned"));
                stats.put("activeReservations", resultSet.getInt("Active_Reservations"));
                stats.put("pendingFine", resultSet.getDouble("Total_Pending_Fine"));
                stats.put("paidFine", resultSet.getDouble("Total_Paid_Fine"));
            }
        }
        return stats;
    }

    public List<Map<String, Object>> getBookAvailabilityView() throws SQLException {
        String sql = "SELECT * FROM Book_Availability";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("bookId", resultSet.getInt("Book_ID"));
                map.put("title", resultSet.getString("Title"));
                map.put("author", resultSet.getString("Author"));
                map.put("totalCopies", resultSet.getInt("Total_Copies"));
                map.put("availableCopies", resultSet.getInt("Available_Copies"));
                list.add(map);
            }
            return list;
        }
    }

    public List<Map<String, Object>> getStudentIssueDetailsView() throws SQLException {
        String sql = "SELECT * FROM Student_Issue_Details ORDER BY Issue_Date DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("studentId", resultSet.getInt("Student_ID"));
                map.put("studentName", resultSet.getString("Student_Name"));
                map.put("title", resultSet.getString("Title"));
                map.put("issueDate", resultSet.getDate("Issue_Date") != null ? resultSet.getDate("Issue_Date").toString() : "");
                map.put("dueDate", resultSet.getDate("Due_Date") != null ? resultSet.getDate("Due_Date").toString() : "");
                map.put("status", resultSet.getString("Status"));
                list.add(map);
            }
            return list;
        }
    }

    public List<Map<String, Object>> getFineReportView() throws SQLException {
        String sql = "SELECT * FROM Fine_Report";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("rollNo", resultSet.getString("Roll_No"));
                map.put("studentName", resultSet.getString("Student_Name"));
                map.put("fineAmount", resultSet.getDouble("Fine_Amount"));
                map.put("paidStatus", resultSet.getString("Paid_Status"));
                list.add(map);
            }
            return list;
        }
    }

    public List<Map<String, Object>> getCategoryDistribution() throws SQLException {
        String sql = "SELECT c.Category_Name, COUNT(b.Book_ID) AS Total_Books, COALESCE(SUM(b.Total_Copies),0) AS Total_Copies " +
                     "FROM Categories c LEFT JOIN Books b ON c.Category_ID = b.Category_ID " +
                     "GROUP BY c.Category_ID, c.Category_Name ORDER BY Total_Books DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("categoryName", resultSet.getString("Category_Name"));
                map.put("totalBooks", resultSet.getInt("Total_Books"));
                map.put("totalCopies", resultSet.getInt("Total_Copies"));
                list.add(map);
            }
            return list;
        }
    }
}
