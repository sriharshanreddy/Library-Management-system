package model;

import java.time.LocalDate;

public class BookIssue {
    private int issueId;
    private int studentId;
    private int bookId;
    private Integer librarianId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;

    public BookIssue() {
    }

    public BookIssue(int issueId, int studentId, int bookId, Integer librarianId, LocalDate issueDate, LocalDate dueDate, String status) {
        this.issueId = issueId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.librarianId = librarianId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
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

    public Integer getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(Integer librarianId) {
        this.librarianId = librarianId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
