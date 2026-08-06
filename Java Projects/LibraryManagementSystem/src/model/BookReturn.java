package model;

import java.time.LocalDate;

public class BookReturn {
    private int returnId;
    private int issueId;
    private LocalDate returnDate;
    private String conditionOfBook;

    public BookReturn() {
    }

    public BookReturn(int returnId, int issueId, LocalDate returnDate, String conditionOfBook) {
        this.returnId = returnId;
        this.issueId = issueId;
        this.returnDate = returnDate;
        this.conditionOfBook = conditionOfBook;
    }

    public int getReturnId() {
        return returnId;
    }

    public void setReturnId(int returnId) {
        this.returnId = returnId;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getConditionOfBook() {
        return conditionOfBook;
    }

    public void setConditionOfBook(String conditionOfBook) {
        this.conditionOfBook = conditionOfBook;
    }
}
