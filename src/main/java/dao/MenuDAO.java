package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Menu;
import util.DBConnection;

public class MenuDAO {
    
    public boolean addMenu(Menu menu) {
        String sql = "INSERT INTO menu (menu_date, meal_type, items) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setDate(1, menu.getMenuDate());
            pstmt.setString(2, menu.getMealType());
            pstmt.setString(3, menu.getItems());
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Menu> getMenuByDate(java.sql.Date date) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE menu_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setDate(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    menus.add(new Menu(
                        rs.getInt("menu_id"),
                        rs.getDate("menu_date"),
                        rs.getString("meal_type"),
                        rs.getString("items")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menus;
    }
    
    public List<Menu> getAllMenus() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM menu ORDER BY menu_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                menus.add(new Menu(
                    rs.getInt("menu_id"),
                    rs.getDate("menu_date"),
                    rs.getString("meal_type"),
                    rs.getString("items")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menus;
    }
}
