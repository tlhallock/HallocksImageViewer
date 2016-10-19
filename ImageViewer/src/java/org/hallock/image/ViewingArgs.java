/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.hallock.images.SqlSettings;

/**
 *
 * @author thallock
 */
public interface ViewingArgs
{

    public static final String PATH = "p";
    public static final String IID = "iid";
    public static final String ROWS = "r";
    public static final String COLS = "c";

    public String getParamString();

    
    
    
    
    
    
    public static abstract class PathedArgs implements ViewingArgs {

        protected ImagePath path;

        private PathedArgs(ImagePath path) {
            this.path = path;
        }

        public final String getParamString() {
            HashMap<String, String> map = new HashMap<>();
            map.put(PATH, path.append(new StringBuilder()).toString());
            dumpRemainingArgs(map);
            return "?" + Utils.urlEncodeUTF8(map);
        }

        public ImagePath getPath() {
            return path;
        }

        public void setPath(ImagePath p) {
            this.path = p;
        }

        public int getPage() {
            return path.getPage();
        }

        protected abstract void dumpRemainingArgs(Map<String, String> map);
    }

    public static class ImageArgs extends PathedArgs {

        private int iid = -1;

        public ImageArgs(ImagePath path, int iid) {
            super(path);
            this.iid = iid;
        }
        
        public ImageArgs(PathedArgs args, int iid) {
            this(new ImagePath(args.path), iid);
        }

        public ImageArgs(HttpServletRequest request) {
            super(new ImagePath(Helper.getParameter(request, PATH, "null", false)));

            String iidStr = Helper.getParameter(request, IID, "null", false);
            try {
                iid = Integer.parseInt(iidStr);
            } catch (NumberFormatException ex) {
            }
        }

        @Override
        protected void dumpRemainingArgs(Map<String, String> map) {
            map.put(IID, String.valueOf(iid));
        }

        public int getIid() {
            return iid;
        }
    }

    public class FolderArgs extends PathedArgs {

        private int rows;
        private int cols;

        public FolderArgs(PathedArgs args) {
            super(new ImagePath(args.path));
            if (args instanceof FolderArgs) {
                this.rows = ((FolderArgs) args).rows;
                this.cols = ((FolderArgs) args).cols;
            } else {
                rows = -1;
                cols = -1;
            }
        }

        public FolderArgs(HttpServletRequest request) {
            super(new ImagePath(Helper.getParameter(request, PATH, "t:0/", true)));
            
            try
            {
            SqlSettings settings = SqlSettings.createSettings();

            String rowsStr = Helper.getParameter(request, ROWS,
                    String.valueOf(settings.getDefaultRows()), true);
            String colsStr = Helper.getParameter(request, COLS,
                    String.valueOf(settings.getDefaultColumns()), true);

            try {
                rows = Integer.parseInt(rowsStr);
            } catch (NumberFormatException ex) {
            }
            try {
                cols = Integer.parseInt(colsStr);
            } catch (NumberFormatException ex) {
            }
            } catch (SQLException ex) { /*TODO: remove this catch...*/ }
        }

        protected void dumpRemainingArgs(Map<String, String> map) {
            if (rows >= 0) {
                map.put(ROWS, String.valueOf(rows));
            }
            if (cols >= 0) {
                map.put(COLS, String.valueOf(cols));
            }

//        StringBuilder builder = new StringBuilder();
//        builder.append('?');
//        path.append(builder.append(PATH).append('='));
//        builder.append('&').append(ROWS).append('=').append(rows);
//        builder.append('&').append(COLS).append('=').append(cols);
//        return builder.toString();
    }

        public FolderArgs getNewPageArgs(int newPage) {
            FolderArgs newArgs = new FolderArgs(this);
            newArgs.getPath().setPage(newPage);
            return newArgs;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * awkward...
     */
    
    public static final class Helper
    {
        private static String getParameter(
                HttpServletRequest request,
                String param,
                String defaultValue,
                boolean add) {
            String value = request.getParameter(param);
            HttpSession session = request.getSession();

            if (value != null) {
                if (add && session != null) {
                    session.setAttribute(param, value);
                }
                return value;
            }

            if (session == null) {
                return defaultValue;
            }

            Object attribute = session.getAttribute(param);
            if (attribute != null && attribute instanceof String) {
                return (String) attribute;
            }

            if (add) {
                session.setAttribute(param, defaultValue);
            }
            return defaultValue;
        }
    }
}
