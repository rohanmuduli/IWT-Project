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

import dao.MenuDAO;
import model.Menu;

@WebServlet("/api/menu")
public class MenuServlet extends HttpServlet {
    private MenuDAO menuDAO = new MenuDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String dateParam = request.getParameter("date");
        List<Menu> menus;
        if (dateParam != null) {
            menus = menuDAO.getMenuByDate(Date.valueOf(dateParam));
        } else {
            menus = menuDAO.getAllMenus();
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < menus.size(); i++) {
            json.append(menus.get(i).toJson());
            if (i < menus.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        Menu menu = new Menu(
            0,
            Date.valueOf(request.getParameter("menuDate")),
            request.getParameter("mealType"),
            request.getParameter("items")
        );
        
        if (menuDAO.addMenu(menu)) {
            out.print("{\"status\":\"success\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\", \"message\":\"Failed to add menu\"}");
        }
    }
}
