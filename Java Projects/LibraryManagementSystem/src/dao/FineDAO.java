package dao;

import database.DBConnection;
import model.Fine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {
    public int save(Fine fine) throws SQLException {
        String sql = "INSERT INTO Fine (Issue_ID, Fine_Amount, Paid_Status) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, fine.getIssueId());
            statement.setDouble(2, fine.getFineAmount());
            statement.setString(3, fine.getPaidStatus());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean markAsPaid(int fineId) throws SQLException {
        String sql = "UPDATE Fine SET Paid_Status='Paid' WHERE Fine_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, fineId);
            return statement.executeUpdate() > 0;
        }
    }

    public List<Fine> findAll() throws SQLException {
        String sql = "SELECT * FROM Fine ORDER BY Fine_ID DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Fine> fines = new ArrayList<>();
            while (resultSet.next()) {
                fines.add(mapFine(resultSet));
            }
            return fines;
        }
    }

    public List<Fine> findPending() throws SQLException {
        String sql = "SELECT * FROM Fine WHERE Paid_Status='Pending' ORDER BY Fine_ID DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Fine> fines = new ArrayList<>();
            while (resultSet.next()) {
                fines.add(mapFine(resultSet));
            }
            return fines;
        }
    }

    private Fine mapFine(ResultSet resultSet) throws SQLException {
        Fine fine = new Fine();
        fine.setFineId(resultSet.getInt("Fine_ID"));
        fine.setIssueId(resultSet.getInt("Issue_ID"));
        fine.setFineAmount(resultSet.getDouble("Fine_Amount"));
        fine.setPaidStatus(resultSet.getString("Paid_Status"));
        return fine;
    }
}
