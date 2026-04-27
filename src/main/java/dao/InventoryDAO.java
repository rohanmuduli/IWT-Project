package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Inventory;
import util.DBConnection;

public class InventoryDAO {

    public boolean addOrUpdateInventory(Inventory item) {
        String checkSql = "SELECT inventory_id FROM inventory WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
             
            checkStmt.setString(1, item.getName());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String updateSql = "UPDATE inventory SET quantity = ?, unit = ?, threshold = ? WHERE inventory_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, item.getQuantity());
                        updateStmt.setString(2, item.getUnit());
                        updateStmt.setDouble(3, item.getThreshold());
                        updateStmt.setInt(4, rs.getInt("inventory_id"));
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO inventory (name, quantity, unit, threshold) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, item.getName());
                        insertStmt.setDouble(2, item.getQuantity());
                        insertStmt.setString(3, item.getUnit());
                        insertStmt.setDouble(4, item.getThreshold());
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                inventoryList.add(new Inventory(
                    rs.getInt("inventory_id"),
                    rs.getString("name"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getDouble("threshold")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
}
