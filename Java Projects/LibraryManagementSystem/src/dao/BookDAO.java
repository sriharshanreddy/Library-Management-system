package dao;

import database.DBConnection;
import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public int save(Book book) throws SQLException {
        String sql = "INSERT INTO Books (ISBN, Title, Author, Publisher, Edition, Publish_Year, Category_ID, Total_Copies, Available_Copies, Shelf_No) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setString(4, book.getPublisher());
            statement.setString(5, book.getEdition());
            if (book.getPublishYear() == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, book.getPublishYear());
            }
            if (book.getCategoryId() == null) {
                statement.setNull(7, java.sql.Types.INTEGER);
            } else {
                statement.setInt(7, book.getCategoryId());
            }
            statement.setInt(8, book.getTotalCopies());
            statement.setInt(9, book.getAvailableCopies());
            statement.setString(10, book.getShelfNo());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean update(Book book) throws SQLException {
        String sql = "UPDATE Books SET ISBN=?, Title=?, Author=?, Publisher=?, Edition=?, Publish_Year=?, Category_ID=?, Total_Copies=?, Available_Copies=?, Shelf_No=? WHERE Book_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setString(4, book.getPublisher());
            statement.setString(5, book.getEdition());
            if (book.getPublishYear() == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, book.getPublishYear());
            }
            if (book.getCategoryId() == null) {
                statement.setNull(7, java.sql.Types.INTEGER);
            } else {
                statement.setInt(7, book.getCategoryId());
            }
            statement.setInt(8, book.getTotalCopies());
            statement.setInt(9, book.getAvailableCopies());
            statement.setString(10, book.getShelfNo());
            statement.setInt(11, book.getBookId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateCopies(int bookId, int availableCopies) throws SQLException {
        String sql = "UPDATE Books SET Available_Copies=? WHERE Book_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, availableCopies);
            statement.setInt(2, bookId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int bookId) throws SQLException {
        String sql = "DELETE FROM Books WHERE Book_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            return statement.executeUpdate() > 0;
        }
    }

    public Book findById(int bookId) throws SQLException {
        String sql = "SELECT * FROM Books WHERE Book_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBook(resultSet);
                }
                return null;
            }
        }
    }

    public List<Book> findAll() throws SQLException {
        String sql = "SELECT * FROM Books ORDER BY Title";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapBooks(resultSet);
        }
    }

    public List<Book> searchByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM Books WHERE Title LIKE ? ORDER BY Title";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + title + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapBooks(resultSet);
            }
        }
    }

    public List<Book> findByCategory(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Books WHERE Category_ID=? ORDER BY Title";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapBooks(resultSet);
            }
        }
    }

    private List<Book> mapBooks(ResultSet resultSet) throws SQLException {
        List<Book> books = new ArrayList<>();
        while (resultSet.next()) {
            books.add(mapBook(resultSet));
        }
        return books;
    }

    private Book mapBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setBookId(resultSet.getInt("Book_ID"));
        book.setIsbn(resultSet.getString("ISBN"));
        book.setTitle(resultSet.getString("Title"));
        book.setAuthor(resultSet.getString("Author"));
        book.setPublisher(resultSet.getString("Publisher"));
        book.setEdition(resultSet.getString("Edition"));
        int publishYear = resultSet.getInt("Publish_Year");
        book.setPublishYear(resultSet.wasNull() ? null : publishYear);
        int categoryId = resultSet.getInt("Category_ID");
        book.setCategoryId(resultSet.wasNull() ? null : categoryId);
        book.setTotalCopies(resultSet.getInt("Total_Copies"));
        book.setAvailableCopies(resultSet.getInt("Available_Copies"));
        book.setShelfNo(resultSet.getString("Shelf_No"));
        return book;
    }
}
