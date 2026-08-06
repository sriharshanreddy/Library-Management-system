package dao;

import database.DBConnection;
import model.Reservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    public int save(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO Reservation (Student_ID, Book_ID, Reservation_Date, Status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, reservation.getStudentId());
            statement.setInt(2, reservation.getBookId());
            if (reservation.getReservationDate() == null) {
                statement.setNull(3, java.sql.Types.DATE);
            } else {
                statement.setDate(3, Date.valueOf(reservation.getReservationDate()));
            }
            statement.setString(4, reservation.getStatus());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateStatus(int reservationId, String status) throws SQLException {
        String sql = "UPDATE Reservation SET Status=? WHERE Reservation_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, reservationId);
            return statement.executeUpdate() > 0;
        }
    }

    public List<Reservation> findAll() throws SQLException {
        String sql = "SELECT * FROM Reservation ORDER BY Reservation_Date DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Reservation> reservations = new ArrayList<>();
            while (resultSet.next()) {
                reservations.add(mapReservation(resultSet));
            }
            return reservations;
        }
    }

    private Reservation mapReservation(ResultSet resultSet) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(resultSet.getInt("Reservation_ID"));
        reservation.setStudentId(resultSet.getInt("Student_ID"));
        reservation.setBookId(resultSet.getInt("Book_ID"));
        Date reservationDate = resultSet.getDate("Reservation_Date");
        reservation.setReservationDate(reservationDate == null ? null : reservationDate.toLocalDate());
        reservation.setStatus(resultSet.getString("Status"));
        return reservation;
    }
}
