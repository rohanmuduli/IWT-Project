package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.StudentDAO;
import model.Student;

@WebServlet("/api/students")
public class StudentServlet extends HttpServlet {
    private StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        List<Student> students = studentDAO.getAllStudents();
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < students.size(); i++) {
            json.append(students.get(i).toJson());
            if (i < students.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
    }
}
