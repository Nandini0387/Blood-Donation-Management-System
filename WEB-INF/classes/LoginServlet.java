import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

response.setContentType("text/html");
            PrintWriter out = response.getWriter();

        // Replace with your database connection details
        String dbURL = "jdbc:mysql://localhost:3306/blood_donation";
        String dbUser = "root";
        String  dbPassword = "Nandini516";

        try {
            Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Hospital WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                // Successful login, set user session
               // HttpSession session = request.getSession();
                //session.setAttribute("user", email);
                response.sendRedirect("home.html"); // Redirect to a welcome page
                //out.println("<html><body bgcolor='green'>Account Created.</body></html>");

            } else {
                // Invalid credentials, redirect back to the login page with an error message
               response.sendRedirect("index.html?error=1");
                //out.println("<html><body bgcolor='blue'>Error creating the account.</body></html>");

            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}