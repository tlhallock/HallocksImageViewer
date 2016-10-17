package org.hallock.images;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.LinkedList;



/**
 * Hello world!
 *
 */
public class App
{
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
            if (false)
            {
                args = 
                        new String[] {
                    InitializationArgs.SETTINGS_FILE_ARG,
                    "/home/thallock/Documents/Source/hosting/res/settings.props",
                        
                    InitializationArgs.ACTION,
                    "update",
                    
                    InitializationArgs.ROOT_ARG,
                    "/home/thallock/Pictures/toUp",
                    
                    InitializationArgs.URL_ARG,
                    "static",
                        
                    };
            }
            
		InitializationArgs iniargs = new InitializationArgs(args);
		Registry.initialize(iniargs);
		
                if (iniargs.getAction().equals("update"))
                {
                    String root = iniargs.getRoot();
                    if (root == null) throw new NullPointerException("No root given.");
                    
                    UrlMapper mapper = Registry.getRegistry().getUrlMapper();
                        
                    String url = null;
                    if (iniargs.getUrl() != null)
                    {
                        url = iniargs.getUrl();
                        mapper.map(root, url);
                        mapper.save();
                    }
                    else
                    {
                        url = mapper.get(root);
                        if (url == null)
                            throw new NullPointerException("no url found, and not given.");
                    }
                    
                    String start = null;
                    if (iniargs.getStart() == null)
                    {
                        start = "";
                    }
                    else
                    {
                        start = iniargs.getStart();
                    }
                    
                    Database.updateRoot(Paths.get(root), url, Paths.get(start));
                }
                else if (iniargs.getAction().equals("map"))
                {
                    UrlMapper mapper = Registry.getRegistry().getUrlMapper();
                        
                    String url = iniargs.getUrl();
                    String root = iniargs.getRoot();
                    if (url == null || root == null)
                    {
                        throw new NullPointerException("Must have both a root and a url");
                    }
                    mapper.map(root, url);
                    mapper.save();
                }
                else if (iniargs.getAction().equals("print"))
                {
                    UrlMapper mapper = Registry.getRegistry().getUrlMapper();
                    mapper.print(System.out);
                }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	    
	    public interface ImageData
	    {
	        public String getLabel();
	        public String getChildLink();
	        public String getImageUrl();
	        public String getAlt();
	    }
	    
	    
	    
	    
	    
	    
	
	

        
        
        
        public static class ImageResults
        {
                    int numPages;
                    LinkedList<ImageData> list = new LinkedList<>();
                    
                    @Override
		public String toString()
                    {
                	    StringBuilder builder = new StringBuilder();
                	    
                	    builder.append("numPages=").append(numPages).append(',');
                	    
                	    for (ImageData data : list)
                	    {
                		    builder.append(data).append(",");
                	    }
                	    
                	    return builder.toString();
                    }
        }
        
        

}
