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

public class AuditDAO {
    public List<Map<String, Object>> findAllAuditLogs() throws SQLException {
        String sql = "SELECT a.Audit_ID, a.Book_ID, b.Title, a.Old_Copies, a.New_Copies, a.Updated_On " +
                     "FROM Audit_Log a " +
                     "LEFT JOIN Books b ON a.Book_ID = b.Book_ID " +
                     "ORDER BY a.Updated_On DESC LIMIT 50";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Map<String, Object>> logs = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("auditId", resultSet.getInt("Audit_ID"));
                map.put("bookId", resultSet.getInt("Book_ID"));
                map.put("title", resultSet.getString("Title") != null ? resultSet.getString("Title") : "Unknown");
                map.put("oldCopies", resultSet.getInt("Old_Copies"));
                map.put("newCopies", resultSet.getInt("New_Copies"));
                map.put("updatedOn", resultSet.getTimestamp("Updated_On").toString());
                logs.add(map);
            }
            return logs;
        }
    }
}
