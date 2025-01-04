import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class AddDonor extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/blood_donation";
            String dbUser = "root";
            String dbPassword = "Nandini516";

            res.setContentType("text/html");
            PrintWriter out = res.getWriter();

            String don_name = req.getParameter("don_name");
            String address = req.getParameter("address");
            int age = Integer.parseInt(req.getParameter("age"));
            String gender = req.getParameter("gender");
            String blood_group = req.getParameter("blood_group");
           
            String contact = req.getParameter("contact");


            try (Connection con = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                 PreparedStatement preparedStatement = con.prepareStatement(
                         "INSERT INTO donor (don_name, age, gender, blood_group,  location, contact) VALUES (?, ?, ?, ?, ?, ?)")) {

                preparedStatement.setString(1, don_name);
                preparedStatement.setInt(2, age);
                preparedStatement.setString(3, gender);
                preparedStatement.setString(4, blood_group);
                
                preparedStatement.setString(5, address); // Use address here
                preparedStatement.setString(6, contact);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    res.sendRedirect("home.html");
                } else {
                    res.sendRedirect("index.html?error=1"); // Or handle the error differently
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
}
    }
}