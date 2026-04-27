package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Attendance;
import util.DBConnection;

public class AttendanceDAO {

    public boolean markAttendance(Attendance attendance) {
        // Check if attendance exists for that student and date
        String checkSql = "SELECT attendance_id FROM attendance WHERE student_id = ? AND attendance_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
             
            checkStmt.setString(1, attendance.getStudentId());
            checkStmt.setDate(2, attendance.getAttendanceDate());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update
                    String updateSql = "UPDATE attendance SET breakfast = ?, lunch = ?, dinner = ? WHERE attendance_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, attendance.getBreakfast());
                        updateStmt.setInt(2, attendance.getLunch());
                        updateStmt.setInt(3, attendance.getDinner());
                        updateStmt.setInt(4, rs.getInt("attendance_id"));
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Insert
                    String insertSql = "INSERT INTO attendance (student_id, attendance_date, breakfast, lunch, dinner) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, attendance.getStudentId());
                        insertStmt.setDate(2, attendance.getAttendanceDate());
                        insertStmt.setInt(3, attendance.getBreakfast());
                        insertStmt.setInt(4, attendance.getLunch());
                        insertStmt.setInt(5, attendance.getDinner());
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> getAttendanceByStudent(String studentId) {
        List<Attendance> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id = ? ORDER BY attendance_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(new Attendance(
                        rs.getInt("attendance_id"),
                        rs.getString("student_id"),
                        rs.getDate("attendance_date"),
                        rs.getInt("breakfast"),
                        rs.getInt("lunch"),
                        rs.getInt("dinner")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }
}
