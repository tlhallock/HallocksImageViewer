<%-- 
    Document   : admin
    Created on : Oct 19, 2016, 5:05:55 PM
    Author     : thallock
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <a href="<%=request.getContextPath()%>/jsp/configure.jsp">Configure</a>
        <a href="<%=request.getContextPath()%>/jsp/roots.jsp">Roots</a>
        <a href="<%=request.getContextPath()%>/jsp/users.jsp">Users</a>
    </body>
</html>
