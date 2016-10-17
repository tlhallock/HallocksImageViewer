/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import java.sql.SQLException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author thallock
 */
public class ImagePageData
{
    HttpServletRequest request;
    ViewingArgs.ImageArgs args;
    DbInterface.ImageResults results;
    
    public ImagePageData(
            HttpServletRequest request,
            ViewingArgs.ImageArgs args,
            DbInterface.ImageResults results)
    {
        this.request = request;
        this.args = args;
        this.results = results;
    }
    
    public PageData getMainLinkData()
    {
        if (results.data == null)
        {
            return PageData.MOCK_DATA;
        }
        return results.data;
    }
    
    
    public String getTime()
    {
        return new Date(results.time).toString();
    }
    public String getName()
    {
        return results.name;
    }
    
    public boolean hasPrev()
    {
        return results.prevImagePath != null && results.prevId != null;
    }
    public String getPrev()
    {
        return LinksManager.getImageLink(request, 
                new ViewingArgs.ImageArgs(new ImagePath(args.getPath(), results.prevImagePath), results.prevId));
    }
    
    public boolean hasNext()
    {
        return results.nextImagePath != null && results.nextId != null;
    }
    
    public String getNext()
    {
        return LinksManager.getImageLink(request, 
                new ViewingArgs.ImageArgs(new ImagePath(args.getPath(), results.nextImagePath), results.nextId));
    }
    
    
    public static ImagePageData getImageData(HttpServletRequest request, ViewingArgs.ImageArgs args) throws SQLException
    {
        try
        {
            return new ImagePageData(request, args, DbInterface.getImage(args.getIid()));
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }
}
