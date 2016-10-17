/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import java.sql.SQLException;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author thallock
 */
public class FolderPageData
{
    private PageData[][] data;
    HttpServletRequest request;
    ViewingArgs.FolderArgs args;
    int numPages;
    
    public FolderPageData(
            HttpServletRequest request,
            ViewingArgs.FolderArgs args,
            DbInterface.FolderResults results)
    {
        this.request = request;
        this.args = args;
        numPages = results.numPages;
        
        int r = Utils.roundUp(results.list.size(), args.getCols());
        int c = Math.min(results.list.size(), args.getCols());
        
        data = new PageData[r][c];
        
        Iterator<PageData> it = results.list.iterator();
        
        for (int i=0;i<data.length;i++)
        {
            for (int j=0;j<data[i].length;j++)
            {
                if (it.hasNext())
                {
                    data[i][j] = it.next();
                }
                else
                {
                    data[i][j] = PageData.MOCK_DATA;
                }
            }
        }
    }
    
    // For mock!!!!
    public FolderPageData(
            ViewingArgs.FolderArgs args)
    {
        this.args = args;
        numPages = 20;
        data = new PageData[args.getRows()][args.getCols()];
        
        for (int i=0;i<data.length;i++)
        {
            for (int j=0;i<data[i].length;j++)
            {
                    data[i][j] = PageData.MOCK_DATA;
            }
        }
    }
    
    public int getCols(int i)
    {
        return data[i].length;
    }
    public int getRows()
    {
        return data.length;
    }
    
    public int getNumberOfPages()
    {
        return numPages;
    }
    
    public String getLabel(int i, int j)
    {
        return data[i][j].getLabel();
    }
    
    public String getChildLink(int i, int j)
    {
        return data[i][j].getChildLink(request, args);
    }
    
    public String getImageUrl(int i, int j)
    {
        return data[i][j].getImageUrl();
    }
    
    public String getAlt(int i, int j)
    {
        return data[i][j].getAlt();
    }
    
    public boolean isMock(int i, int j)
    {
        return data[i][j] == PageData.MOCK_DATA;
    }
    
    

    
    
    public static FolderPageData getPageData(HttpServletRequest request, ViewingArgs.FolderArgs args) throws SQLException
    {
        ImagePath p = args.getPath();
        if (p.isTimePath())
        {
            if (p.isTimeComplete())
            {
                return new FolderPageData(request, args, DbInterface.listImages(
                    p.getLike(),
                    args.getCols() * args.getRows(),
                    args.getPage()));
            }
            else
            {
                return new FolderPageData(request, args, DbInterface.listFolders(
                    p.getLike(),
                    args.getCols() * args.getRows(),
                    args.getPage()));
            }
        }
        
        return new FolderPageData(args);
    }
}
