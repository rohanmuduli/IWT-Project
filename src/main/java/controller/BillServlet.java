package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.BillDAO;
import model.Bill;

@WebServlet("/api/bill")
public class BillServlet extends HttpServlet {
    private BillDAO billDAO = new BillDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String studentId = request.getParameter("studentId");
        String month = request.getParameter("month"); // e.g. "2026-04"
        
        if (studentId != null && month != null) {
            Bill bill = billDAO.getBill(studentId, month);
            if (bill != null) {
                out.print("{\"status\":\"success\", \"bill\":" + bill.toJson() + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\":\"error\", \"message\":\"Bill not found\"}");
            }
        } else if (month != null) {
            List<Bill> bills = billDAO.getAllBills(month);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bills.size(); i++) {
                json.append(bills.get(i).toJson());
                if (i < bills.size() - 1) json.append(",");
            }
            json.append("]");
            out.print(json.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Month is required\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        Bill bill = new Bill(
            0,
            request.getParameter("studentId"),
            request.getParameter("month"),
            Double.parseDouble(request.getParameter("baseAmount")),
            Double.parseDouble(request.getParameter("deductions")),
            Double.parseDouble(request.getParameter("extras")),
            Double.parseDouble(request.getParameter("total"))
        );
        
        if (billDAO.generateBill(bill)) {
            out.print("{\"status\":\"success\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Failed to generate bill\"}");
        }
    }
}
