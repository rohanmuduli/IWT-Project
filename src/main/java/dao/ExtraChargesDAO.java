package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.ExtraCharge;
import util.DBConnection;

public class ExtraChargesDAO {
    
    public boolean addExtraCharge(ExtraCharge charge) {
        String sql = "INSERT INTO extra_charges (charge_date, student_id, item, qty, amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setDate(1, charge.getChargeDate());
            pstmt.setString(2, charge.getStudentId());
            pstmt.setString(3, charge.getItem());
            pstmt.setInt(4, charge.getQty());
            pstmt.setDouble(5, charge.getAmount());
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<ExtraCharge> getAllExtraCharges() {
        List<ExtraCharge> charges = new ArrayList<>();
        String sql = "SELECT * FROM extra_charges ORDER BY charge_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                charges.add(new ExtraCharge(
                    rs.getInt("charge_id"),
                    rs.getDate("charge_date"),
                    rs.getString("student_id"),
                    rs.getString("item"),
                    rs.getInt("qty"),
                    rs.getDouble("amount")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charges;
    }
}
