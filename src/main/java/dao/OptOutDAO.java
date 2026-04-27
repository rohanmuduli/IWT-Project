package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.OptOut;
import util.DBConnection;

public class OptOutDAO {

    public boolean submitOptOut(OptOut optOut) {
        String sql = "INSERT INTO optouts (student_id, optout_date, meal_type, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, optOut.getStudentId());
            pstmt.setDate(2, optOut.getOptoutDate());
            pstmt.setString(3, optOut.getMealType());
            pstmt.setString(4, "Pending"); // Default status
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OptOut> getOptOutsByStudent(String studentId) {
        List<OptOut> optOuts = new ArrayList<>();
        String sql = "SELECT * FROM optouts WHERE student_id = ? ORDER BY optout_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    optOuts.add(new OptOut(
                        rs.getInt("optout_id"),
                        rs.getString("student_id"),
                        rs.getDate("optout_date"),
                        rs.getString("meal_type"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return optOuts;
    }
    
    public List<OptOut> getAllOptOuts() {
        List<OptOut> optOuts = new ArrayList<>();
        String sql = "SELECT * FROM optouts ORDER BY optout_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                optOuts.add(new OptOut(
                    rs.getInt("optout_id"),
                    rs.getString("student_id"),
                    rs.getDate("optout_date"),
                    rs.getString("meal_type"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return optOuts;
    }

    public boolean updateOptOutStatus(int optoutId, String status) {
        String sql = "UPDATE optouts SET status = ? WHERE optout_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            pstmt.setInt(2, optoutId);
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
