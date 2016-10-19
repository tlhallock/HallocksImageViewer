<%-- 
    Document   : configure
    Created on : Oct 18, 2016, 8:38:12 AM
    Author     : thallock
--%>


<%@page import="org.hallock.images.Roots.RootsMapper"%>
<%@page import="org.hallock.images.Roots"%>
<%@page import="java.sql.SQLException"%>
<%@page import="org.hallock.images.SqlSettings"%>
<%@page import="org.hallock.images.servlet.ConfigurationServlet"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <%@ include file="/WEB-INF/jspf/style.jspf" %>
    <head>
        <title>Page Title</title>
    </head>
    <body>
        <h1>This is a Heading</h1>
        <p>This is a paragraph.</p>
        
        
        <%
            try
            {
                SqlSettings settings = SqlSettings.createSettings();
                RootsMapper mapper = Roots.createRootsMapper();
                
                %>


        <div class="main">
                    need to set the port as well.
                <div class="settingsgroup">
                    <div class="settingsrow">/the/path/to/root -> http://<input type="text" name="<%=ConfigurationServlet.IMAGES_HOSTNAME%>" value="<%=settings.getImagesHostname()%>"/>:<input type="number" name="<%=ConfigurationServlet.IPORT%>" min="-1" max="256" value="<%=settings.getImagesPort()%>"/>/path/from/root</div>
                </div>
                <div class="settingsgroup">
                    <div class="settingsrow">
                        <div class="settingdescription">default path type</div>
                        <div class="settingvalue">
                            <select name="cars">
                                <option value="<%=ConfigurationServlet.DEFAULT_PATH_VIEW_TIME%>"     <% if (true)  { %> selected="selected" <% } %> >View by time</option>
                                <option value="<%=ConfigurationServlet.DEFAULT_PATH_VIEW_FOLDER%>"   <% if (false) { %> selected="selected" <% } %> >View by path in root folder</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="settingsgroup">
                    <div class="settingsrow">
                        <div class="settingdescription">path verbosity</div>
                        <div class="settingvalue">
                            <select name="<%=ConfigurationServlet.PATH_VERBOSITY%>">
                                <option value="<%=ConfigurationServlet.PATH_VERBOSITY_MINIMAL%>"   <% if (true)  { %> selected="selected" <% } %> >Minimal paths</option>
                                <option value="<%=ConfigurationServlet.PATH_VERBOSITY_FULL%>"      <% if (false) { %> selected="selected" <% } %> >Full paths</option>
                            </select>
                        </div>
                    </div>
                </div>
                Static images server:
                <div class="settingsgroup">
                    <div class="settingsrow">
                        <div class="settingsrow">/the/path/to/root -> http://<input type="text" name="<%=ConfigurationServlet.IMAGES_HOSTNAME%>" value="<%=settings.getImagesHostname()%>"/>:<input type="number" name="<%=ConfigurationServlet.IPORT%>" min="-1" max="256" value="<%=settings.getImagesPort()%>"/>/path/from/root</div>
                    </div>
                    for root in table
                    <div class="settingsrow">
                        <div class="settingdescription">/the/path/to/root -> </div>
                        <div class="settingvalue">http://<input type="text" name="lname"/>:<%=settings.getImagesPort()%>/path/from/root<br/> delete, sync</div>
                    </div>
                    <div class="settingsrow">
                        <div class="settingdescription">add root <input type="text" name="rootpath">-></div>
                        <div class="settingvalue">http://<input type="text" name="lname"/>:<%=settings.getImagesPort()%>/path/from/root  delete </div>
                        <div class="settingoptions"> delete </div>
                    </div>
                </div>
            </form>
        </div>
                    
        <%
            } catch(SQLException ex)
            {
%>
            There was an error connecting to the database.
<%
            }
        %>
    </body>
</html>
