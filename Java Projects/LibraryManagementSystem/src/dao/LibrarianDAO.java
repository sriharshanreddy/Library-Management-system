package dao;

import database.DBConnection;
import model.Librarian;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibrarianDAO {
    public List<Librarian> findAll() throws SQLException {
        String sql = "SELECT * FROM Librarians ORDER BY First_Name, Last_Name";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Librarian> librarians = new ArrayList<>();
            while (resultSet.next()) {
                Librarian l = new Librarian();
                l.setLibrarianId(resultSet.getInt("Librarian_ID"));
                l.setFirstName(resultSet.getString("First_Name"));
                l.setLastName(resultSet.getString("Last_Name"));
                l.setPhone(resultSet.getString("Phone"));
                l.setEmail(resultSet.getString("Email"));
                Date hireDate = resultSet.getDate("Hire_Date");
                l.setHireDate(hireDate == null ? null : hireDate.toLocalDate());
                librarians.add(l);
            }
            return librarians;
        }
    }
}
