<%@ page contentType="text/html;charset=UTF-8" language="java" %>
 <%@ page import="java.util.List" %> 
<%@ page import="RequestServlet.Donor" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Matching Donors</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f4f4f9;
        }

        .container {
            text-align: center;
            width: 80%;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }

        table, th, td {
            border: 1px solid #ddd;
        }

        th, td {
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        button:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
    </style>
    <script>
        function disableButton(button) {
            button.disabled = true;
            button.textContent = "Request Sent";
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>Matching Donors</h2>
        <table>
            <thead>
                <tr>
                    <th>Donor ID</th>
                    <th>Name</th>
                    <th>Age</th>
                    <th>Gender</th>
                    <th>Blood Group</th>
                    <th>Frequency</th>
                    <th>Last Donation Date</th>
                    <th>Location</th>
                    <th>Contact</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Donor> donorRecords = (List<Donor>) request.getAttribute("donorRecords");
                    if (donorRecords != null) {
                        for (Donor donor : donorRecords) {
                            out.println("<tr><td>" + donor.getDon_id() + "</td><td>" + donor.getDon_name() + "</td><td>" + donor.getAge() + "</td><td>" + donor.getGender() + "</td><td>" + donor.getBlood_group() + "</td><td>" + donor.getFreq() + "</td><td>" + donor.getLast_donation_date() + "</td><td>" + donor.getLocation() + "</td><td>" + donor.getContact() + "</td><td><button onclick='disableButton(this)'>Send Request</button></td></tr>");
                        }
                    }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>
