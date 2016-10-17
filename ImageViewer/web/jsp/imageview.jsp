<%-- 
    Document   : view
    Created on : Oct 13, 2016, 6:31:13 PM
    Author     : thallock
--%>

<%@page import="org.hallock.image.ViewingArgs"%>
<%@page import="org.hallock.image.PageData"%>
<%@page import="org.hallock.image.ImagePath"%>
<%@page import="org.hallock.image.ViewingArgs"%>
<%@page import="org.hallock.image.ImagePageData"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css"/>

    <head>
        <title>JSP - Include Directive</title>
    </head>

    <body>
        <%@ include file="/WEB-INF/jspf/header.jspf" %>

        <%
            ViewingArgs.ImageArgs args = new ViewingArgs.ImageArgs(request);
            ImagePageData data = ImagePageData.getImageData(request, args);
            PageData image = data.getMainLinkData();
            if (data == null || image == null)
            {
        %>
        <div class="main">
            Image not found!
        </div>
        <% } else {%>
        
        <%@ include file="/WEB-INF/jspf/pathselector.jspf" %>
        
        <div class="main">
            <div class="pageselector">
                <!-- prev -->
                <div class="pagelink">
                    <% if (data.hasPrev()) {%>
                    <a href="<%=data.getPrev()%>">prev</a>
                    <% } else { %>
                    prev
                    <% } %>
                </div>
                
                
                
                <!-- next -->
                <div class="pagelink">
                    <% if (data.hasNext()) {%>
                    <a href="<%=data.getNext()%>">next</a>
                    <% } else { %>
                    next
                    <% } %>
                </div>
                <span class="pageend"></span>
            </div>
        </div>
                
        
        <div class="main">
            <a href="<%=image.getImageUrl()%>"><img class="imgview" src="<%=image.getImageUrl()%>" alt="<%=image.getAlt()%>"/></a>
        </div>
        
        <%@ include file="/WEB-INF/jspf/delete.jspf" %>
        
        <div class="main">
            Comments go here...
        </div>
        
        
        <%}%>

        <%@ include file="/WEB-INF/jspf/footer.jspf" %>

    </body>
</html>
