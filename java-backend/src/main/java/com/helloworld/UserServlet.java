package com.helloworld;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/user")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Get className from request parameter
        String className = req.getParameter("class");

        try {
            // Unsafe reflection: className is user-controlled and used directly in Class.forName()
            // This is a classic Java deserialization/reflection vulnerability (CWE-95)
            Class<?> cls = Class.forName(className);
            Object instance = cls.getDeclaredConstructor().newInstance();
            out.print("{\"loaded\": \"" + cls.getName() + "\"}");
        } catch (Exception e) {
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
        
        out.flush();
    }
}
