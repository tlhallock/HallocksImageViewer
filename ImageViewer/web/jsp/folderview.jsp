<%-- 
    Document   : view
    Created on : Oct 13, 2016, 6:31:13 PM
    Author     : thallock
--%>

<%@page import="org.hallock.image.ViewingArgs"%>
<%@page import="org.hallock.image.FolderPageData"%>
<%@page import="org.hallock.image.LinksManager"%>
<%@page import="org.hallock.image.ImagePath"%>
<%@page import="org.hallock.image.ViewingArgs"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <%@ include file="/WEB-INF/jspf/style.jspf" %>

    <head>
        <title>JSP - Include Directive</title>
    </head>

    <body>
        <%@ include file="/WEB-INF/jspf/header.jspf" %>

        <%
            ViewingArgs.FolderArgs args = new ViewingArgs.FolderArgs(request);
            FolderPageData data = FolderPageData.getPageData(request, args);
        %>

        <%@ include file="/WEB-INF/jspf/pathselector.jspf" %>
        <%@ include file="/WEB-INF/jspf/pageselector.jspf" %>

        <div class="main">
            <div class="icontable">
                <% for (int i = 0; i < data.getRows(); i++) { %>
                <div class="iconrow">
                    <% for (int j = 0; j < data.getCols(i); j++) {
                        if (data.isMock(i, j)) { %>
                    <div class="iconcolumn"></div>
                        <% } else { %>
                    <div class="iconcolumn">
                        <div class="icondiv">
                            <a href="<%=data.getChildLink(i, j)%>"><img class="iconimg" src="<%=data.getImageUrl(i, j)%>" alt="<%=data.getAlt(i, j)%>" /></a>
                            <div class="label"><a href="<%=data.getChildLink(i, j)%>"><%=data.getLabel(i, j)%></a></div>
                        </div>
                    </div>
                    <% } }%>
                </div>
                <% }%>
            </div>
        </div>

        <%@ include file="/WEB-INF/jspf/footer.jspf" %>

    </body>
</html>
