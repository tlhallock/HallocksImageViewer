<%-- 
    Document   : users
    Created on : Oct 19, 2016, 3:52:00 PM
    Author     : thallock
--%>

<%@page import="org.hallock.images.Users"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <div class="main">
            <%
                for (Users.User user : Users.listUsers()) {
            %>
            <div> <%=user.name%>        </div>
            <div> <%=user.password%>    </div>
            <div> <%=user.isAdmin%>     </div>
            <div> <%=user.uploadPerm%>  </div>
            <div> <%=user.deletePerm%>  </div>
            <%
                }
            %>
        </div>
        
        
        
        
        
        
    </body>
</html>
