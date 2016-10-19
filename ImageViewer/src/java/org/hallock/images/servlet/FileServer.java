/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.images.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.hallock.image.DbInterface;
import org.hallock.image.DbInterface.ImageLocation;
import org.hallock.image.ViewingArgs;

/**
 *
 * @author thallock
 */
@WebServlet(name = "FileServer", urlPatterns = {"/files"})
public class FileServer extends HttpServlet
{
    private static void copyError(HttpServletRequest request, HttpServletResponse response)
    {
        
    }

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
        int id;
        String parameter = request.getParameter(ViewingArgs.IID);
        if (parameter == null) {
            copyError(request, response);
            return;
        }

        try {
            id = Integer.parseInt(parameter);
        } catch (NumberFormatException ex) {
            copyError(request, response);
            return;
        }

        ImageLocation location = null;
        try {
            location = DbInterface.getImageLocation(id);
        }
        catch (SQLException ex)
        {
            copyError(request, response);
            return;
        }
        
        
        if (location == null || location.root == null || location.path == null)
        {
            copyError(request, response);
            return;
        }

        String ext = "jpg";
        int ndx = location.path.lastIndexOf(".");
        if (ndx >= 0) {
            ext = location.path.substring(ndx);
        }

        Path imagePath = Paths.get(location.root + "/" + location.path);
        try (InputStream input = Files.newInputStream(imagePath);
             OutputStream output = response.getOutputStream();)
        {
            response.setContentType("image/" + ext);
            IOUtils.copyLarge(input, output);
        }
        catch (IOException ex)
        {
            copyError(request, response);
            return;
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
