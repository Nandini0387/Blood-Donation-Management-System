import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class RegisterServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            // Load the MySQL JDBC driver (replace with the appropriate driver)
            // Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Set your database connection details
            String jdbcUrl = "jdbc:mysql://localhost:3306/blood_donation";
            String dbUser = "root";
            String dbPassword = "Nandini516";

            res.setContentType("text/html");
            PrintWriter out = res.getWriter();

	    // Get user data from the form
            String hos_name = req.getParameter("hos_name");
            String location = req.getParameter("location");
            String contact = req.getParameter("contact");
            String username = req.getParameter("username");
            String password = req.getParameter("password");


            // Establish a database connection
            Connection con = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            Statement stmt = con.createStatement();

             // Insert user data into the database using prepared statements to prevent SQL injection
            String sqlQuery = "INSERT INTO hospital (hos_name, location, contact, username, password) VALUES ( ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
            preparedStatement.setString(1, hos_name);
            preparedStatement.setString(2, location);
            preparedStatement.setString(3, contact);
            preparedStatement.setString(4, username);
            preparedStatement.setString(5, password);


            int rowsAffected = preparedStatement.executeUpdate();
            out.println("<body bgcolor='red'>Successfully created the account.</body>");

            if (rowsAffected > 0) {
                // Account created successfully, redirect to home.html
                res.sendRedirect("home.html");
                // out.println("<body bgcolor='lightcoral'>Successfully created the account.</body>");
            } else {
                res.sendRedirect("index.html?error=1");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
