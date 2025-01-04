import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SendSMSServlet extends HttpServlet {
    public static final String ACCOUNT_SID = "Your_SID";
    public static final String AUTH_TOKEN = "Your_Token";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Initialize Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String donorContact = request.getParameter("donorContact");
        String hospitalName = request.getParameter("hospitalName");

        // Get the hospital contact from the database (you may want to fetch it from the hospital data)
        String hospitalContact = getHospitalContact(hospitalName);

        // Send SMS using Twilio API
        try {
            Message message = Message.creator(
                    new PhoneNumber(donorContact), // donor's phone number
                    new PhoneNumber("Your_number"), // Your Twilio number
                    "Request for blood donation from " + hospitalName + ". Please contact: " + hospitalContact)
                    .create();
            response.sendRedirect("success.html");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.html");
        }
    }

    private String getHospitalContact(String hospitalName) {
        // Replace with actual database lookup to get hospital contact
        return "+9876543210";  // Example static number
    }
}
