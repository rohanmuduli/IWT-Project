package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Student;
import util.DBConnection;

public class StudentDAO {
    
    public boolean registerStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, room_no, password_hash, subscription_type, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getRoomNo());
            pstmt.setString(4, student.getPasswordHash());
            pstmt.setString(5, student.getSubscriptionType());
            pstmt.setString(6, student.getRole() != null ? student.getRole() : "student");
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student authenticate(String studentId, String passwordHash) {
        String sql = "SELECT * FROM students WHERE student_id = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            pstmt.setString(2, passwordHash);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("room_no"),
                        rs.getString("password_hash"),
                        rs.getString("subscription_type"),
                        rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Student getStudent(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("room_no"),
                        rs.getString("password_hash"),
                        rs.getString("subscription_type"),
                        rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE role = 'student'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                students.add(new Student(
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("room_no"),
                    "", // Don't send password hash back
                    rs.getString("subscription_type"),
                    rs.getString("role")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
}
