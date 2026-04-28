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

import dao.ExtraChargesDAO;
import model.ExtraCharge;

@WebServlet("/api/extras")
public class ExtraChargesServlet extends HttpServlet {
    private ExtraChargesDAO extrasDAO = new ExtraChargesDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        List<ExtraCharge> charges = extrasDAO.getAllExtraCharges();
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < charges.size(); i++) {
            json.append(charges.get(i).toJson());
            if (i < charges.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        ExtraCharge charge = new ExtraCharge(
            0,
            Date.valueOf(request.getParameter("chargeDate")),
            request.getParameter("studentId"),
            request.getParameter("item"),
            Integer.parseInt(request.getParameter("qty")),
            Double.parseDouble(request.getParameter("amount"))
        );
        
        if (extrasDAO.addExtraCharge(charge)) {
            out.print("{\"status\":\"success\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Failed to add charge\"}");
        }
    }
}
