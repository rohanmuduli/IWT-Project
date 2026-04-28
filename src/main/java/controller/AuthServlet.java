package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.StudentDAO;
import model.Student;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if ("/login".equals(pathInfo)) {
            String studentId = request.getParameter("studentId");
            String passwordHash = request.getParameter("password");

            Student student = studentDAO.authenticate(studentId, passwordHash);
            if (student != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", student);
                out.print("{\"status\":\"success\", \"user\":" + student.toJson() + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"status\":\"error\", \"message\":\"Invalid credentials\"}");
            }
        } else if ("/register".equals(pathInfo)) {
            Student student = new Student(
                request.getParameter("studentId"),
                request.getParameter("name"),
                request.getParameter("roomNo"),
                request.getParameter("password"),
                request.getParameter("subscriptionType"),
                "student"
            );
            if (studentDAO.registerStudent(student)) {
                out.print("{\"status\":\"success\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\", \"message\":\"Registration failed\"}");
            }
        } else if ("/logout".equals(pathInfo)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            out.print("{\"status\":\"success\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if ("/me".equals(pathInfo)) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                Student user = (Student) session.getAttribute("user");
                // Refresh data
                user = studentDAO.getStudent(user.getStudentId());
                out.print("{\"status\":\"success\", \"user\":" + user.toJson() + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"status\":\"error\", \"message\":\"Not logged in\"}");
            }
        }
    }
}
