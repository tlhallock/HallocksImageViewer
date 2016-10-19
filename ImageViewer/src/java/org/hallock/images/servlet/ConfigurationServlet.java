package org.hallock.images.servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author thallock
 */
public class ConfigurationServlet extends HttpServlet
{
    public static final String DB_USER = "dbUser";
    public static final String DB_PASSWORD = "dbPassword";
    public static final String DB_TABLE = "dbname";
    public static final String MAX_VIEW_AT_ONCE = "maxView";
    public static final String HOSTNAME = "hostname";
    public static final String PORT = "port";
    
    public static final String IMAGES_HOSTNAME = "ihostname";
    public static final String IPORT = "iport";
    public static final String DEFAULT_R = "drows";
    public static final String DEFAULT_C = "dcols";
    public static final String THUMBNAIL_WIDTH = "thumbw";
    public static final String THUMBNAIL_HEIGHT = "thumbh";
    public static final String ENCODING = "encoding";
    
    public static final String DEFAULT_PATH_VIEW = "viewtype";
    public static final String DEFAULT_PATH_VIEW_TIME = "time";
    public static final String DEFAULT_PATH_VIEW_FOLDER = "folder";
    
    
    public static final String PATH_VERBOSITY = "verbosePaths";
    public static final String PATH_VERBOSITY_FULL = "withPages";
    public static final String PATH_VERBOSITY_MINIMAL = "noPages";
    
    public static final String USERNAME_PREFIX = "username";
    public static final String PASSWORD_PREFIX = "password";
    public static final String ISADMIN_PREFIX  = "admin";
    public static final String UPLOAD_PERM_PREFIX = "upload";
    public static final String DELETE_PERM_PREFIX = "delete";
    
    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            
            
            
            
            
            
            
            
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ConfigurationServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ConfigurationServlet at " + request.getContextPath() + "</h1>");
            out.println("<a href=\"" + request.getContextPath() + "/jsp/configure.jsp\"> Configure again...</a>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
