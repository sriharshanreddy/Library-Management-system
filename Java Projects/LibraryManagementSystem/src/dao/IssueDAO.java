package dao;

import database.DBConnection;
import model.BookIssue;
import model.BookReturn;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {
    public int issueBook(int studentId, int bookId, Integer librarianId, LocalDate issueDate, LocalDate dueDate) throws SQLException {
        String bookSql = "SELECT Available_Copies FROM Books WHERE Book_ID=? FOR UPDATE";
        String issueSql = "INSERT INTO Book_Issue (Student_ID, Book_ID, Librarian_ID, Issue_Date, Due_Date, Status) VALUES (?, ?, ?, ?, ?, 'Issued')";
        String updateSql = "UPDATE Books SET Available_Copies = Available_Copies - 1 WHERE Book_ID=?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int availableCopies = 0;
                try (PreparedStatement bookStatement = connection.prepareStatement(bookSql)) {
                    bookStatement.setInt(1, bookId);
                    try (ResultSet resultSet = bookStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("Book not found");
                        }
                        availableCopies = resultSet.getInt(1);
                    }
                }

                if (availableCopies <= 0) {
                    throw new SQLException("No available copies for this book");
                }

                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setInt(1, bookId);
                    updateStatement.executeUpdate();
                }

                try (PreparedStatement issueStatement = connection.prepareStatement(issueSql, Statement.RETURN_GENERATED_KEYS)) {
                    issueStatement.setInt(1, studentId);
                    issueStatement.setInt(2, bookId);
                    if (librarianId == null) {
                        issueStatement.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        issueStatement.setInt(3, librarianId);
                    }
                    issueStatement.setDate(4, Date.valueOf(issueDate));
                    issueStatement.setDate(5, Date.valueOf(dueDate));
                    issueStatement.executeUpdate();
                    try (ResultSet generatedKeys = issueStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            connection.commit();
                            return generatedKeys.getInt(1);
                        }
                    }
                }
                connection.commit();
                return -1;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean returnBook(int issueId, LocalDate returnDate, String conditionOfBook) throws SQLException {
        String issueSql = "SELECT Book_ID, Due_Date, Status FROM Book_Issue WHERE Issue_ID=? FOR UPDATE";
        String insertReturnSql = "INSERT INTO Book_Return (Issue_ID, Return_Date, Condition_Of_Book) VALUES (?, ?, ?)";
        String updateIssueSql = "UPDATE Book_Issue SET Status='Returned' WHERE Issue_ID=?";
        String updateBookSql = "UPDATE Books SET Available_Copies = Available_Copies + 1 WHERE Book_ID=?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int bookId;
                String status;

                try (PreparedStatement issueStatement = connection.prepareStatement(issueSql)) {
                    issueStatement.setInt(1, issueId);
                    try (ResultSet resultSet = issueStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("Issue record not found");
                        }
                        bookId = resultSet.getInt("Book_ID");
                        status = resultSet.getString("Status");
                    }
                }

                if ("Returned".equalsIgnoreCase(status)) {
                    throw new SQLException("This issue has already been returned");
                }

                try (PreparedStatement insertReturnStatement = connection.prepareStatement(insertReturnSql)) {
                    insertReturnStatement.setInt(1, issueId);
                    insertReturnStatement.setDate(2, Date.valueOf(returnDate));
                    insertReturnStatement.setString(3, conditionOfBook);
                    insertReturnStatement.executeUpdate();
                }

                try (PreparedStatement updateIssueStatement = connection.prepareStatement(updateIssueSql)) {
                    updateIssueStatement.setInt(1, issueId);
                    updateIssueStatement.executeUpdate();
                }

                try (PreparedStatement updateBookStatement = connection.prepareStatement(updateBookSql)) {
                    updateBookStatement.setInt(1, bookId);
                    updateBookStatement.executeUpdate();
                }

                connection.commit();
                return true;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<BookIssue> findAllIssues() throws SQLException {
        String sql = "SELECT * FROM Book_Issue ORDER BY Issue_Date DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<BookIssue> issues = new ArrayList<>();
            while (resultSet.next()) {
                issues.add(mapIssue(resultSet));
            }
            return issues;
        }
    }

    public List<BookIssue> findIssuesByStudent(int studentId) throws SQLException {
        String sql = "SELECT * FROM Book_Issue WHERE Student_ID=? ORDER BY Issue_Date DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<BookIssue> issues = new ArrayList<>();
                while (resultSet.next()) {
                    issues.add(mapIssue(resultSet));
                }
                return issues;
            }
        }
    }

    public List<BookReturn> findAllReturns() throws SQLException {
        String sql = "SELECT * FROM Book_Return ORDER BY Return_Date DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<BookReturn> returns = new ArrayList<>();
            while (resultSet.next()) {
                returns.add(mapReturn(resultSet));
            }
            return returns;
        }
    }

    private BookIssue mapIssue(ResultSet resultSet) throws SQLException {
        BookIssue issue = new BookIssue();
        issue.setIssueId(resultSet.getInt("Issue_ID"));
        issue.setStudentId(resultSet.getInt("Student_ID"));
        issue.setBookId(resultSet.getInt("Book_ID"));
        int librarianId = resultSet.getInt("Librarian_ID");
        issue.setLibrarianId(resultSet.wasNull() ? null : librarianId);
        issue.setIssueDate(resultSet.getDate("Issue_Date").toLocalDate());
        issue.setDueDate(resultSet.getDate("Due_Date").toLocalDate());
        issue.setStatus(resultSet.getString("Status"));
        return issue;
    }

    private BookReturn mapReturn(ResultSet resultSet) throws SQLException {
        BookReturn bookReturn = new BookReturn();
        bookReturn.setReturnId(resultSet.getInt("Return_ID"));
        bookReturn.setIssueId(resultSet.getInt("Issue_ID"));
        Date returnDate = resultSet.getDate("Return_Date");
        bookReturn.setReturnDate(returnDate == null ? null : returnDate.toLocalDate());
        bookReturn.setConditionOfBook(resultSet.getString("Condition_Of_Book"));
        return bookReturn;
    }
}
