package model;

public class Fine {
    private int fineId;
    private int issueId;
    private double fineAmount;
    private String paidStatus;

    public Fine() {
    }

    public Fine(int fineId, int issueId, double fineAmount, String paidStatus) {
        this.fineId = fineId;
        this.issueId = issueId;
        this.fineAmount = fineAmount;
        this.paidStatus = paidStatus;
    }

    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(String paidStatus) {
        this.paidStatus = paidStatus;
    }
}
