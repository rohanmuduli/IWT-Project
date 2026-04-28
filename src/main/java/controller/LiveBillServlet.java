package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.DBConnection;

@WebServlet("/api/bill/live")
public class LiveBillServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String studentId = request.getParameter("studentId");
        if (studentId == null || studentId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"studentId is required\"}");
            return;
        }

        double mealCost = 50.0;
        int attendedMeals = 0;
        double extras = 0.0;

        // Determine current month in YYYY-MM format
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        try (Connection conn = DBConnection.getConnection()) {
            // Calculate total meals attended for the current month
            String attSql = "SELECT SUM(breakfast + lunch + dinner) FROM attendance WHERE student_id = ? AND TO_CHAR(attendance_date, 'YYYY-MM') = ?";
            try (PreparedStatement pstmt1 = conn.prepareStatement(attSql)) {
                pstmt1.setString(1, studentId);
                pstmt1.setString(2, currentMonth);
                try (ResultSet rs1 = pstmt1.executeQuery()) {
                    if (rs1.next()) {
                        attendedMeals = rs1.getInt(1);
                    }
                }
            }

            // Calculate Extra Charges for the current month
            String extraSql = "SELECT SUM(amount) FROM extra_charges WHERE student_id = ? AND TO_CHAR(charge_date, 'YYYY-MM') = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(extraSql)) {
                pstmt2.setString(1, studentId);
                pstmt2.setString(2, currentMonth);
                try (ResultSet rs2 = pstmt2.executeQuery()) {
                    if (rs2.next()) {
                        extras = rs2.getDouble(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\", \"message\":\"Database error\"}");
            return;
        }

        double baseAmount = attendedMeals * mealCost;
        double total = baseAmount + extras;

        out.print("{");
        out.print("\"status\":\"success\",");
        out.print("\"month\":\"" + currentMonth + "\",");
        out.print("\"baseAmount\":" + baseAmount + ",");
        out.print("\"attendedMeals\":" + attendedMeals + ",");
        out.print("\"extras\":" + extras + ",");
        out.print("\"total\":" + total);
        out.print("}");
    }
}
