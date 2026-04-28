package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.DBConnection;

@WebServlet("/api/stats")
public class DashboardStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        int totalStudents = 0;
        int expectedCount = 0;
        String nextMeal = "Breakfast";
        
        // Determine Next Meal based on time
        java.time.LocalTime now = java.time.LocalTime.now();
        if (now.isBefore(java.time.LocalTime.of(9, 30))) {
            nextMeal = "Breakfast";
        } else if (now.isBefore(java.time.LocalTime.of(14, 30))) {
            nextMeal = "Lunch";
        } else if (now.isBefore(java.time.LocalTime.of(21, 30))) {
            nextMeal = "Dinner";
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            // Get total subscribed students
            String sql1 = "SELECT COUNT(*) FROM students WHERE role = 'student'";
            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1);
                 ResultSet rs1 = pstmt1.executeQuery()) {
                if (rs1.next()) totalStudents = rs1.getInt(1);
            }
            
            // Expected for next meal is total students minus opt-outs for that meal today
            int optOutCount = 0;
            String sql2 = "SELECT COUNT(*) FROM optouts WHERE optout_date = TRUNC(SYSDATE) AND (meal_type = ? OR meal_type = 'Full Day') AND status != 'Cancelled'";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setString(1, nextMeal);
                try (ResultSet rs2 = pstmt2.executeQuery()) {
                    if (rs2.next()) optOutCount = rs2.getInt(1);
                }
            }
            
            expectedCount = totalStudents - optOutCount;
            if (expectedCount < 0) expectedCount = 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.print("{\"totalStudents\":" + totalStudents + ",\"expectedCount\":" + expectedCount + ",\"nextMeal\":\"" + nextMeal + "\"}");
    }
}
