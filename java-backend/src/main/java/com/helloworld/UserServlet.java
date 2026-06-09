package com.helloworld;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/api/user")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Get user ID from request parameter
        String userId = req.getParameter("id");

        try {
            // SQL Injection vulnerability: userId is directly concatenated into the query
            String query = "SELECT name FROM users WHERE id = " + userId;
            
            Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                String name = rs.getString("name");
                out.print("{\"name\": \"" + name + "\"}");
            } else {
                out.print("{\"error\": \"User not found\"}");
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            out.print("{\"error\": \"Database error\"}");
        }
        
        out.flush();
    }
}
