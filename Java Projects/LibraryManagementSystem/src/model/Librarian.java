package model;

import java.time.LocalDate;

public class Librarian {
    private int librarianId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private LocalDate hireDate;

    public Librarian() {
    }

    public Librarian(int librarianId, String firstName, String lastName, String phone, String email, LocalDate hireDate) {
        this.librarianId = librarianId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.hireDate = hireDate;
    }

    public int getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(int librarianId) {
        this.librarianId = librarianId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
