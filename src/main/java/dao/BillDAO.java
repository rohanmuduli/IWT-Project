package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Bill;
import util.DBConnection;

public class BillDAO {

    public boolean generateBill(Bill bill) {
        String checkSql = "SELECT bill_id FROM bills WHERE student_id = ? AND bill_month = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
             
            checkStmt.setString(1, bill.getStudentId());
            checkStmt.setString(2, bill.getBillMonth());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String updateSql = "UPDATE bills SET base_amount = ?, deductions = ?, extras = ?, total = ? WHERE bill_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, bill.getBaseAmount());
                        updateStmt.setDouble(2, bill.getDeductions());
                        updateStmt.setDouble(3, bill.getExtras());
                        updateStmt.setDouble(4, bill.getTotal());
                        updateStmt.setInt(5, rs.getInt("bill_id"));
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO bills (student_id, bill_month, base_amount, deductions, extras, total) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, bill.getStudentId());
                        insertStmt.setString(2, bill.getBillMonth());
                        insertStmt.setDouble(3, bill.getBaseAmount());
                        insertStmt.setDouble(4, bill.getDeductions());
                        insertStmt.setDouble(5, bill.getExtras());
                        insertStmt.setDouble(6, bill.getTotal());
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Bill getBill(String studentId, String month) {
        String sql = "SELECT * FROM bills WHERE student_id = ? AND bill_month = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            pstmt.setString(2, month);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Bill(
                        rs.getInt("bill_id"),
                        rs.getString("student_id"),
                        rs.getString("bill_month"),
                        rs.getDouble("base_amount"),
                        rs.getDouble("deductions"),
                        rs.getDouble("extras"),
                        rs.getDouble("total")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Bill> getAllBills(String month) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE bill_month = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(new Bill(
                        rs.getInt("bill_id"),
                        rs.getString("student_id"),
                        rs.getString("bill_month"),
                        rs.getDouble("base_amount"),
                        rs.getDouble("deductions"),
                        rs.getDouble("extras"),
                        rs.getDouble("total")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bills;
    }
}
