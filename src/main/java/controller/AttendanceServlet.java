package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.AttendanceDAO;
import model.Attendance;

@WebServlet("/api/attendance")
public class AttendanceServlet extends HttpServlet {
    private AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String studentId = request.getParameter("studentId");
        if (studentId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"studentId is required\"}");
            return;
        }

        List<Attendance> records = attendanceDAO.getAttendanceByStudent(studentId);
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < records.size(); i++) {
            json.append(records.get(i).toJson());
            if (i < records.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        Attendance attendance = new Attendance(
            0,
            request.getParameter("studentId"),
            Date.valueOf(request.getParameter("attendanceDate")),
            Integer.parseInt(request.getParameter("breakfast")),
            Integer.parseInt(request.getParameter("lunch")),
            Integer.parseInt(request.getParameter("dinner"))
        );
        
        if (attendanceDAO.markAttendance(attendance)) {
            out.print("{\"status\":\"success\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Failed to mark attendance\"}");
        }
    }
}
