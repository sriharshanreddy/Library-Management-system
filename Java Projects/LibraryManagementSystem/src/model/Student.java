package model;

import java.time.LocalDate;

public class Student {
    private int studentId;
    private String rollNo;
    private String firstName;
    private String lastName;
    private String gender;
    private String department;
    private Integer semester;
    private String phone;
    private String email;
    private String address;
    private LocalDate joinDate;

    public Student() {
    }

    public Student(int studentId, String rollNo, String firstName, String lastName, String gender, String department,
                   Integer semester, String phone, String email, String address, LocalDate joinDate) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.department = department;
        this.semester = semester;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.joinDate = joinDate;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", rollNo='" + rollNo + '\'' +
                ", name='" + firstName + ' ' + lastName + '\'' +
                '}';
    }
}
