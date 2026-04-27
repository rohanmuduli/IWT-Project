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

import dao.OptOutDAO;
import model.OptOut;

@WebServlet("/api/optout")
public class OptOutServlet extends HttpServlet {
    private OptOutDAO optOutDAO = new OptOutDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String studentId = request.getParameter("studentId");
        List<OptOut> optOuts;
        if (studentId != null) {
            optOuts = optOutDAO.getOptOutsByStudent(studentId);
        } else {
            optOuts = optOutDAO.getAllOptOuts();
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < optOuts.size(); i++) {
            json.append(optOuts.get(i).toJson());
            if (i < optOuts.size() - 1) json.append(",");
        }
        json.append("]");
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        if ("updateStatus".equals(action)) {
            int optoutId = Integer.parseInt(request.getParameter("optoutId"));
            String status = request.getParameter("status");
            if (optOutDAO.updateOptOutStatus(optoutId, status)) {
                out.print("{\"status\":\"success\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to update status\"}");
            }
        } else {
            OptOut optOut = new OptOut(
                0,
                request.getParameter("studentId"),
                Date.valueOf(request.getParameter("optoutDate")),
                request.getParameter("mealType"),
                "Pending"
            );
            
            if (optOutDAO.submitOptOut(optOut)) {
                out.print("{\"status\":\"success\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Failed to submit opt-out\"}");
            }
        }
    }
}
