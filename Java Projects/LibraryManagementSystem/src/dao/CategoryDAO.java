package dao;

import database.DBConnection;
import model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public int save(Category category) throws SQLException {
        String sql = "INSERT INTO Categories (Category_Name, Description) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Category> findAll() throws SQLException {
        String sql = "SELECT * FROM Categories ORDER BY Category_Name";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Category> categories = new ArrayList<>();
            while (resultSet.next()) {
                Category c = new Category();
                c.setCategoryId(resultSet.getInt("Category_ID"));
                c.setCategoryName(resultSet.getString("Category_Name"));
                c.setDescription(resultSet.getString("Description"));
                categories.add(c);
            }
            return categories;
        }
    }
}
