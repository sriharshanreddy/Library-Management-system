package model;

import java.time.LocalDate;

public class Reservation {
    private int reservationId;
    private int studentId;
    private int bookId;
    private LocalDate reservationDate;
    private String status;

    public Reservation() {
    }

    public Reservation(int reservationId, int studentId, int bookId, LocalDate reservationDate, String status) {
        this.reservationId = reservationId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
