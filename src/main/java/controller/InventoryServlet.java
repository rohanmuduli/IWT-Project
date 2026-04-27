package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.InventoryDAO;
import model.Inventory;

@WebServlet("/api/inventory")
public class InventoryServlet extends HttpServlet {
    private InventoryDAO inventoryDAO = new InventoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        List<Inventory> inventoryList = inventoryDAO.getAllInventory();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < inventoryList.size(); i++) {
            json.append(inventoryList.get(i).toJson());
            if (i < inventoryList.size() - 1) json.append(",");
        }
        json.append("]");
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        Inventory item = new Inventory(
            0,
            request.getParameter("name"),
            Double.parseDouble(request.getParameter("quantity")),
            request.getParameter("unit"),
            Double.parseDouble(request.getParameter("threshold"))
        );
        
        if (inventoryDAO.addOrUpdateInventory(item)) {
            out.print("{\"status\":\"success\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Failed to update inventory\"}");
        }
    }
}
