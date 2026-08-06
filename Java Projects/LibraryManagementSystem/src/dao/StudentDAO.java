package dao;

import database.DBConnection;
import model.Student;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public int save(Student student) throws SQLException {
        String sql = "INSERT INTO Students (Roll_No, First_Name, Last_Name, Gender, Department, Semester, Phone, Email, Address, Join_Date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.getRollNo());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getGender());
            statement.setString(5, student.getDepartment());
            if (student.getSemester() == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, student.getSemester());
            }
            statement.setString(7, student.getPhone());
            statement.setString(8, student.getEmail());
            statement.setString(9, student.getAddress());
            if (student.getJoinDate() == null) {
                statement.setNull(10, java.sql.Types.DATE);
            } else {
                statement.setDate(10, Date.valueOf(student.getJoinDate()));
            }
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public Student findById(int studentId) throws SQLException {
        String sql = "SELECT * FROM Students WHERE Student_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapStudent(resultSet);
            }
        }
    }

    public Student findByRollNo(String rollNo) throws SQLException {
        String sql = "SELECT * FROM Students WHERE Roll_No=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rollNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapStudent(resultSet);
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT * FROM Students ORDER BY First_Name, Last_Name";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                students.add(mapStudentRow(resultSet));
            }
            return students;
        }
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        return mapStudentRow(resultSet);
    }

    private Student mapStudentRow(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        student.setStudentId(resultSet.getInt("Student_ID"));
        student.setRollNo(resultSet.getString("Roll_No"));
        student.setFirstName(resultSet.getString("First_Name"));
        student.setLastName(resultSet.getString("Last_Name"));
        student.setGender(resultSet.getString("Gender"));
        student.setDepartment(resultSet.getString("Department"));
        int semester = resultSet.getInt("Semester");
        student.setSemester(resultSet.wasNull() ? null : semester);
        student.setPhone(resultSet.getString("Phone"));
        student.setEmail(resultSet.getString("Email"));
        student.setAddress(resultSet.getString("Address"));
        Date joinDate = resultSet.getDate("Join_Date");
        student.setJoinDate(joinDate == null ? null : joinDate.toLocalDate());
        return student;
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE Students SET Roll_No=?, First_Name=?, Last_Name=?, Gender=?, Department=?, Semester=?, Phone=?, Email=?, Address=?, Join_Date=? WHERE Student_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getRollNo());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getGender());
            statement.setString(5, student.getDepartment());
            if (student.getSemester() == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, student.getSemester());
            }
            statement.setString(7, student.getPhone());
            statement.setString(8, student.getEmail());
            statement.setString(9, student.getAddress());
            if (student.getJoinDate() == null) {
                statement.setNull(10, java.sql.Types.DATE);
            } else {
                statement.setDate(10, Date.valueOf(student.getJoinDate()));
            }
            statement.setInt(11, student.getStudentId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int studentId) throws SQLException {
        String sql = "DELETE FROM Students WHERE Student_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            return statement.executeUpdate() > 0;
        }
    }
}
