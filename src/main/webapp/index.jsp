<%@ page import="ejsp.DateManager" %>
<%@ page import="ejsp.security.CoreSHA" %>
<%@ page import="ejsp.security.CoreBase64" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sample Page</title>
</head>
<body>
    <h6>Current Time: <%=DateManager.getAMPM()%> <%=DateManager.getHour12Format()%>:<%=DateManager.getMinute()%>:<%=DateManager.getSecond()%></h6>
    <h6>Current Date: <%=DateManager.getYear4Digit()%>.<%=DateManager.getMonth()%>.<%=DateManager.getDate()%></h6>

    <h6>SHA256 of "Hello World": <%=CoreSHA.hash256("Hello World")%></h6>
    <h6>SHA512 of "Hello World": <%=CoreSHA.hash512("Hello World")%></h6>
    <h6>SHA256 of "Hello World", 1111: <%=CoreSHA.hash256("Hello World", "1111")%></h6>
    <h6>SHA512 of "Hello World", 1111: <%=CoreSHA.hash512("Hello World", "1111")%></h6>

    <h6>Base64 Encode of "Hello World": <%=CoreBase64.encode("Hello World")%></h6>
    <h6>Base64 Self Inverse of "Hello World": <%=CoreBase64.decode(CoreBase64.encode("Hello World"))%></h6>

</body>
</html>