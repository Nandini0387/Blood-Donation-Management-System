import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class RequestServlet extends HttpServlet {
    private static final String ACCOUNT_SID = "Your_SID"; // Replace with your SID
    private static final String AUTH_TOKEN = "4Your_token"; // Replace with your token
    private static boolean smsSent = false;

    @Override
    public void init() throws ServletException {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/blood_donation";
            String dbUser = "root";
            String dbPassword = "Nandini516";

            res.setContentType("text/html");
            PrintWriter out = res.getWriter();

            String hos_name = req.getParameter("hos_name");
            String req_date = req.getParameter("req_date");
            String req_blood_group = req.getParameter("req_blood_group");
            String req_age = req.getParameter("req_age");
            String req_location = req.getParameter("req_location");

            try (Connection con = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                 PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO Request(hos_name, req_date, req_blood_group, req_age, req_location) VALUES (?, ?, ?, ?, ?)")) {

                preparedStatement.setString(1, hos_name);
                preparedStatement.setString(2, req_date);
                preparedStatement.setString(3, req_blood_group);
                preparedStatement.setString(4, req_age);
                preparedStatement.setString(5, req_location);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    String compatibleBloodGroups = getCompatibleBloodGroups(req_blood_group);

                    String donorQuery = "SELECT don_id, don_name, age, gender, blood_group, freq, last_donation_date, location, contact " +
                            "FROM donor " +
                            "WHERE blood_group IN (" + compatibleBloodGroups + ") AND freq > (SELECT AVG(freq) FROM donor) " +
                            "ORDER BY freq ASC, last_donation_date ASC";

                    try (PreparedStatement donorStmt = con.prepareStatement(donorQuery);
                         ResultSet rs = donorStmt.executeQuery()) {

                        out.println("<html><head><title>Matching Donors</title>");
                        out.println("<style>");
                        out.println("body { font-family: Arial, sans-serif; margin: 0; padding: 0; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f9; }");
                        out.println(".container { text-align: center; width: 80%; padding: 20px; background-color: #fff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }");
                        out.println("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
                        out.println("table, th, td { border: 1px solid #ddd; }");
                        out.println("th, td { padding: 10px; text-align: left; }");
                        out.println("th { background-color: #f2f2f2; }");
                        out.println("button { background-color: #4CAF50; color: white; padding: 10px; border: none; border-radius: 5px; cursor: pointer; }");
                        out.println("button:disabled { background-color: #ccc; cursor: not-allowed; }");
                        out.println("</style>");
                        out.println("</head><body><div class='container'>");
                        out.println("<h2>Matching Donors</h2>");
                        out.println("<table><thead><tr><th>Donor ID</th><th>Name</th><th>Age</th><th>Gender</th><th>Blood Group</th><th>Frequency</th><th>Last Donation Date</th><th>Location</th><th>Contact</th></tr></thead><tbody>");

                        while (rs.next()) {
                            String donorContact = rs.getString("contact");
                            out.println("<tr><td>" + rs.getInt("don_id") + "</td><td>" + rs.getString("don_name") + "</td><td>" + rs.getInt("age") + "</td><td>" + rs.getString("gender") + "</td><td>" + rs.getString("blood_group") + "</td><td>" + rs.getInt("freq") + "</td><td>" + rs.getString("last_donation_date") + "</td><td>" + rs.getString("location") + "</td><td>" + donorContact + "</td></tr>");
                        }
                        out.println("</tbody></table>");

                        out.println("<button id='sendRequestButton' " + (smsSent ? "disabled" : "") + ">Send Request</button>");
                        if (!smsSent) {
                            out.println("<script>");
                            out.println("document.getElementById('sendRequestButton').addEventListener('click', function() {");
                            out.println("this.disabled = true;");
                            out.println("fetch(window.location.href, { method: 'POST', body: new URLSearchParams({sms: 'true'}) }).then(() => {alert('SMS sent!'); window.location.reload();});");
                            out.println("});");
                            out.println("</script>");
                        }
                        out.println("</div></body></html>");

                        if (req.getParameter("sms") != null && !smsSent) {
                            try (ResultSet rs2 = donorStmt.executeQuery()) {
                                while (rs2.next()) {
                                    String donorContact = rs2.getString("contact");
                                    sendSms(donorContact, hos_name);
                                }
                                smsSent = true;
                            }
                        }
                    }
                } else {
                    res.sendRedirect("index.html?error=1");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().println("An error occurred: " + e.getMessage());
        }
    }

    private String getCompatibleBloodGroups(String req_blood_group) {
        switch (req_blood_group) {
            case "A+": return "'A+', 'A-', 'O+', 'O-'";
            case "O+": return "'O+', 'O-'";
            case "B+": return "'B+', 'B-', 'O+', 'O-'";
            case "AB+": return "'A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'";
            case "A-": return "'A-', 'O-'";
            case "O-": return "'O-'";
            case "B-": return "'B-', 'O-'";
            case "AB-": return "'AB-', 'A-', 'B-', 'O-'";
            default: return "";
        }
    }

    private void sendSms(String donorContact, String hospitalName) {
        try {
            String hospitalContact = "Hos_contact"; 
            Message message = Message.creator(
                    new PhoneNumber(donorContact),
                    new PhoneNumber(hospitalContact),
                    "Blood request from " + hospitalName + "!")
                    .create();

            System.out.println("Message sent! SID: " + message.getSid() + " to " + donorContact);
        } catch (com.twilio.exception.ApiException e) {
            System.err.println("Twilio API Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}