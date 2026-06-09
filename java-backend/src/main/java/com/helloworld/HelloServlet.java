package com.helloworld;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet({"/api/hello", "/api/reflect"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        
        String path = req.getRequestURI();
        if (path.endsWith("/reflect")) {
            handleReflection(req, resp, out);
        } else {
            out.print("{\"message\": \"Hello World\"}");
        }
        out.flush();
    }
    
    private void handleReflection(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        // Get className from request parameter (CWE-95: Improper Neutralization of Directives in Dynamically Evaluated Code)
        String className = req.getParameter("class");
        try {
            // Unsafe reflection: user-controlled class name directly used in Class.forName()
            Class<?> cls = Class.forName(className);
            out.print("{\"class\": \"" + cls.getName() + "\"}");
        } catch (ClassNotFoundException e) {
            out.print("{\"error\": \"Class not found\"}");
        } catch (Exception e) {
            out.print("{\"error\": \"Reflection error\"}");
        }
    }
}
