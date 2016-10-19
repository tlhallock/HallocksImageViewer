/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import org.hallock.images.SqlSettings;

/**
 *
 * @author thallock
 */
public class DbInterface
{   
    public static class FolderResults {
        int numPages;
        LinkedList<PageData> list = new LinkedList<>();
    }

    public static FolderResults listFolders(
            String prefix,
            int pageSize,
            int pageNumber) throws SQLException {
        FolderResults qresults = new FolderResults();
        
	String table    = SqlSettings.getDbDatabaseName();
	String user     = SqlSettings.getDbUsername();
	String password = SqlSettings.getDbPassword();
                
        // Change this to the apache commons connection pool
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);) {
            int numSlashes = Utils.count(prefix, '/');

            // Get the number of results...
            String query =
                                  "select count(distinct("
                		+ "SUBSTRING_INDEX(SUBSTRING_INDEX(TIME_PATH, '/', ?), '/', -1)"
                		+ ")) "
                		+ "from IMAGES                           "
                		+ "where TIME_PATH like ?;";
            try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                int ndx = 1;
                updateTotal.setInt(ndx++, numSlashes + 1);
                updateTotal.setString(ndx++, prefix + "%");
                try (ResultSet results = updateTotal.executeQuery();) {
                    if (results.next()) {
                        qresults.numPages = Utils.roundUp(results.getInt(1), pageSize);
                    } else {
                        return qresults;
                    }
                }
            }

            // Get the years...
            LinkedList<String> children = new LinkedList<>();
            query =
                                  "select distinct("
                		+ "SUBSTRING_INDEX(SUBSTRING_INDEX(TIME_PATH, '/', ?), '/', -1)) "
                		+ "from IMAGES                                                   "
                		+ "where TIME_PATH like ?                                        "
                		+ "order by IMAGE_TIME                                           "
                		+ "limit ?,?;                                                    ";
            try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                int ndx = 1;
                updateTotal.setInt(ndx++, numSlashes + 1);
                updateTotal.setString(ndx++, prefix + "%");
                updateTotal.setInt(ndx++, pageSize * pageNumber);
                updateTotal.setInt(ndx++, pageSize);

                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("whereIsThis.txt"));) {
                    writer.write(updateTotal.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try (ResultSet results = updateTotal.executeQuery();) {
                    while (results.next()) {
                        children.add(results.getString(1));
                    }
                }
            }

            for (final String child : children) {
                String subquery =
                                  " select IMAGE_TIME, IMAGES.PATH, ROOTS.URL        	                "
				+ " from IMAGES 							"
				+ " 	inner join ROOTS on		    				"
				+ " 		IMAGES.RID = ROOTS.R_ID 				"
				+ " where TIME_PATH like ? 						"
				+ " 		order by rand() 					"
				+ " 		limit 1;          					";

                try (PreparedStatement updateTotal = conn.prepareStatement(subquery);) {
                    updateTotal.setString(1, prefix + child + "/%");
                    try (ResultSet results = updateTotal.executeQuery();) {
                        if (!results.next()) {
                            continue;
                        }

                        int ndx = 1;
                        final long time = results.getTimestamp(ndx++).getTime();
                        final String imagePath = results.getString(ndx++);
                        final String url = results.getString(ndx++);

                        qresults.list.add(PageData.createChildLink(prefix, child, imagePath, url));
                    }
                }
            }
        }

        return qresults;
    }
        
    // TODO: combine this with above...
    public static FolderResults listImages(
            String prefix,
            int pageSize, int pageNumber) throws SQLException {
        FolderResults qresults = new FolderResults();

	String table    = SqlSettings.getDbDatabaseName();
	String user     = SqlSettings.getDbUsername();
	String password = SqlSettings.getDbPassword();
                
        // Change this to the apache commons connection pool
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);) {
            // Get the number of results...
                        String query = "select count(I_ID)                                       "
                		+ "from IMAGES                                                   "
                		+ "where TIME_PATH like ?                                       ;";
			         try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                updateTotal.setString(1, prefix + "%");
                try (ResultSet results = updateTotal.executeQuery();) {
                    if (results.next()) {
                        qresults.numPages = Utils.roundUp(results.getInt(1), pageSize);
                    } else {
                        return qresults;
                    }
                }
            }
                
                    String subquery =
                                  " select I_ID, IMAGE_TIME, IMAGES.PATH, ROOTS.URL        	        "
				+ " from IMAGES 							"
				+ " 	inner join ROOTS on		    				"
				+ " 		IMAGES.RID = ROOTS.R_ID 				"
				+ " where TIME_PATH like ? 						"
				+ " 		order by IMAGE_TIME 					"
				+ " 		limit ?, ?;          					";

            try (PreparedStatement updateTotal = conn.prepareStatement(subquery);) {
                int ndx = 1;
                updateTotal.setString(ndx++, prefix + "%");
                updateTotal.setInt(ndx++, pageSize * pageNumber);
                updateTotal.setInt(ndx++, pageSize);
                try (ResultSet results = updateTotal.executeQuery();) {
                    while (results.next()) {
                        ndx = 1;
                        final int iid = results.getInt(ndx++);
                        final long time = results.getTimestamp(ndx++).getTime();
                        final String imagePath = results.getString(ndx++);
                        final String url = results.getString(ndx++);
                        
                        qresults.list.add(PageData.createImageLink(iid, time, prefix, url, imagePath));
                    }
                }
            }
        }

        return qresults;
    }


        
    public static final class ImageResults
    {
        PageData data;
        long time;
        String name;
        Integer nextId;
        Integer prevId;
        String prevImagePath;
        String nextImagePath;
        
        // TODO comments, people
//        LinkedList<String> comments = new LinkedList<>();
        
    }
        
    public static ImageResults getImage(int iid) throws SQLException
    {
        ImageResults qresults = new ImageResults();
        
	String table    = SqlSettings.getDbDatabaseName();
	String user     = SqlSettings.getDbUsername();
	String password = SqlSettings.getDbPassword();
                
        // Change this to the apache commons connection pool
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);) {
            Timestamp stamp = null;
            String query =
                                  " select IMAGE_TIME, IMAGES.PATH, ROOTS.URL        	                "
				+ " from IMAGES 							"
				+ " 	inner join ROOTS on		    				"
				+ " 		IMAGES.RID = ROOTS.R_ID 				"
				+ " where I_ID = ?;               					";
            try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                updateTotal.setInt(1, iid);
                try (ResultSet results = updateTotal.executeQuery();) {
                    if (!results.next())
                        return qresults;
                    
                    int ndx = 1;
                    
                    stamp = results.getTimestamp(ndx++);
                    final long time = stamp.getTime();
                    final String imagePath = results.getString(ndx++);
                    final String url = results.getString(ndx++);

                    qresults.data = PageData.createStaticImageLink(iid, time, url, imagePath);
                    qresults.time = time;
                    qresults.name = imagePath;
                }
            }
            
            // Get the next image:
            query =
                                  " select I_ID, TIME_PATH, IMAGE_TIME                 	                "
				+ " from IMAGES 							"
				+ " where IMAGE_TIME > ?                                                "
                                + " order by IMAGE_TIME 				                "
                                + " limit 1           					                ";
            try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                updateTotal.setTimestamp(1, stamp);
                try (ResultSet results = updateTotal.executeQuery();) {
                    if (results.next())
                    {
                        int ndx = 1;
                        int id = results.getInt(ndx++);
                        String timePath = results.getString(ndx++);
                        String name = String.valueOf(results.getTimestamp(ndx++).getTime());
                        qresults.nextId = id;
                        qresults.nextImagePath = timePath + name;
                    }
                }
            }
            
            // Get the previous image:
            query =
                                  " select I_ID, TIME_PATH, IMAGE_TIME                	                "
				+ " from IMAGES 							"
				+ " where IMAGE_TIME < ?                                                "
                                + " order by IMAGE_TIME desc				                "
                                + " limit 1           					                ";
            try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                updateTotal.setTimestamp(1, stamp);
                try (ResultSet results = updateTotal.executeQuery();) {
                    if (results.next())
                    {
                        int ndx = 1;
                        int id = results.getInt(ndx++);
                        String timePath = results.getString(ndx++);
                        String name = String.valueOf(results.getTimestamp(ndx++).getTime());
                        qresults.prevId = id;
                        qresults.prevImagePath = timePath + name;
                    }
                }
            }
        }
        
        return qresults;
    }
        
        
        
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static class ImageLocation
    {
        public final String root;
        public final String path;
        
        public ImageLocation(String root, String path)
        {
            this.path = path;
            this.root = root;
        }
    }
    
    public static ImageLocation getImageLocation(int iid) throws SQLException
    {
	String table    = SqlSettings.getDbDatabaseName();
	String user     = SqlSettings.getDbUsername();
	String password = SqlSettings.getDbPassword();
                
        // Change this to the apache commons connection pool
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);)
        {
            String query =
                                  " select ROOTS.ROOT, IMAGES.PATH                                      "
				+ " from IMAGES 							"
				+ " 	inner join ROOTS on		    				"
				+ " 		IMAGES.RID = ROOTS.R_ID 				"
				+ " where I_ID = ?;               					";
            try (PreparedStatement updateTotal = conn.prepareStatement(query);) {
                updateTotal.setInt(1, iid);
                try (ResultSet results = updateTotal.executeQuery();) {
                    if (!results.next())
                        return null;
                    
                    int ndx = 1;
                    String root = results.getString(ndx++);
                    String path = results.getString(ndx++);
                    
                    return new ImageLocation(root, path);
                }
            }
        }
    }
        
        
        /*
        
        
        
        
        
        
        TOOK TOO LONG:
        

SELECT r.PATH, (SELECT r1.I_ID FROM IMAGES AS r1 WHERE YEAR(r.IMAGE_TIME)=YEAR(r1.IMAGE_TIME) ORDER BY rand() LIMIT 1) AS id FROM IMAGES AS r GROUP BY YEAR(r.IMAGE_TIME);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

	public void fillMonths(Connection conn, int year, LinkedList<Integer> months) throws SQLException
	{
		// TODO: This should limit the number of results....
		String query = "select distinct(MONTH(IMAGE_TIME)) as m from IMAGES where YEAR(IMAGE_TIME) = ? order by m;";
		try (PreparedStatement updateTotal = conn.prepareStatement(query);)
		{	
			updateTotal.setInt(1, year);
			try (ResultSet results = updateTotal.executeQuery();)
			{
				while (results.next())
				{
					months.add(results.getInt(1));
				}
			}
		}
	}

	public void fillDays(Connection conn, int year, int month, LinkedList<Integer> days)
	{
		// TODO: This should limit the number of results....
		String query = "select distinct(DAY(IMAGE_TIME)) as d from IMAGES where YEAR(IMAGE_TIME)=? and MONTH(IMAGE_TIME)=? order by d;";
		try (PreparedStatement updateTotal = conn.prepareStatement(query);)
		{	
			updateTotal.setInt(1, year);
			updateTotal.setInt(2, month);
			try (ResultSet results = updateTotal.executeQuery();)
			{
				while (results.next())
				{
					days.add(results.getInt(1));
				}
			}
		}
	}

	public void fillDays(Connection conn, int year, int month, int day, LinkedList<ImageData> images) throws SQLException
	{
		// TODO: This should limit the number of results....
		String query =    " select IMAGE_TIME as t, IMAGES.PATH as path, ROOTS.PATH as root	"
				+ " from IMAGES 							"
				+ " 	inner join ROOTS on		    				"
				+ " 		IMAGE_TIME.RID = ROOTS.R_ID 				"
				+ " where YEAR(IMAGE_TIME)=? 						"
				+ " 	and MONTH(IMAGE_TIME)=?						"
				+ " 	and DAY(IMAGE_TIME)=?						"
				+ " 		order by t;						";
		try (PreparedStatement updateTotal = conn.prepareStatement(query);)
		{	
			updateTotal.setInt(1, year);
			updateTotal.setInt(2, month);
			updateTotal.setInt(3, day);
			
			try (ResultSet results = updateTotal.executeQuery();)
			{
				while (results.next())
				{
					ImageData data = new ImageData();
					
					data.timestamp = results.getTimestamp(1).getTime();
					data.path = results.getString(2);
					data.root = results.getString(3);
					
					images.add(data);
				}
			}
		}
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private static final class ChildInfo
    {
        // will eventually have rating

        long timestamp;
        String root;
        String path;
        LinkedList<String> comments = new LinkedList<>();
        LinkedList<String> tags = new LinkedList<>();
        LinkedList<String> people = new LinkedList<>();

        public String getAlt() {
            return "alt";
        }

        public String getImageSrc() {
            return "imgSrc";
        }

        public String getName() {
            return "name";
        }
    }
        */
}
