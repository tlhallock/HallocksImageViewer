/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import javax.servlet.http.HttpServletRequest;
import org.hallock.images.Registry;

/**
 *
 * @author thallock
 */
public class LinksManager
{
    private static String getRoot(HttpServletRequest request)
    {
        // TODO: I should not have to do this!!!!!
        String url = request.getRequestURL().toString();
        String cut = request.getContextPath();
        int index = url.indexOf(cut);
        return url.substring(0, index + cut.length());
    }
    private static String getFolderViewer(HttpServletRequest request)
    {
        return getRoot(request) + "/jsp/folderview.jsp";
    }
    private static String getImageViewer(HttpServletRequest request)
    {
        return getRoot(request) + "/jsp/imageview.jsp";
    }
    
    
    
    
    
    public static String getFolderLink(
            HttpServletRequest request,
            ViewingArgs.FolderArgs args)
    {
        return getFolderViewer(request) + args.getParamString();
    }
    
    public static String getImageLink(
            HttpServletRequest request,
            ViewingArgs.ImageArgs args)
    {
        return getImageViewer(request) + args.getParamString();
    }
    
    
    
    
    
    
    public static String getStaticImageLink(
            String rootUrl,
            String path)
    {
        return "http://" + Registry.getRegistry().getSettings().getHostName() + "/" + rootUrl + "/" + path;
    }
}
