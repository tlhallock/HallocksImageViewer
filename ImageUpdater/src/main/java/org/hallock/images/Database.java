package org.hallock.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


public class Database
{
	// MODIFY THE ROOTS TABLE
	public static void insertRoot(Connection conn, String root, String url) throws SQLException
	{
		String updateStatement = "insert into ROOTS (PATH, URL)  values (?, ?);";
		try (PreparedStatement updateTotal = conn.prepareStatement(updateStatement);)
		{
			updateTotal.setString(1, root);
			updateTotal.setString(2, url);
			int executeUpdate = updateTotal.executeUpdate();
			if (executeUpdate != 0)
			{
				
			}
		}
	}
	
	public static Integer getRoot(Connection conn, String root) throws SQLException
	{
		String query = "select R_ID from ROOTS where PATH = ?;";
		try (PreparedStatement updateTotal = conn.prepareStatement(query);)
		{	
			updateTotal.setString(1, root);
			try (ResultSet results = updateTotal.executeQuery();)
			{
				if (results.next())
				{
					return results.getInt(1);
				}
				else
				{
					return null;
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// MODIFY THE IMAGES TABLE
	public static Long getFileModificationTime(Connection conn, ImageInfo info) throws SQLException
	{
		String queryStatement = 
		"select IMAGES.I_ID, IMAGES.LAST_MODIFIED            " +
		"from IMAGES                                         " +
		"inner join ROOTS                                    " +
		"on IMAGES.RID=ROOTS.R_ID                            " +
		"where IMAGES.PATH = ? and ROOTS.PATH = ?            ";
		
		try (PreparedStatement updateTotal = conn.prepareStatement(queryStatement);)
		{	
                    int ndx = 1;
			updateTotal.setString(ndx++, info.getPath());
			updateTotal.setString(ndx++, info.getRoot());
			try (ResultSet results = updateTotal.executeQuery();)
			{
				if (results.next())
				{
                                        ndx = 1;
					int id = results.getInt(ndx++);
					long timestamp = results.getTimestamp(ndx++).getTime();
					return timestamp;
				}
				else
				{
					return null;
				}
			}
		}
	}
	public static void insertImage(Connection conn, ImageInfo info) throws SQLException
	{
		String updateStatement = "insert into IMAGES (PATH, IMAGE_TIME, LAST_MODIFIED, TIME_PATH, RID) "
					+ "select ?, ?, ?, ?, R_ID from ROOTS where PATH=?;";
		try (PreparedStatement updateTotal = conn.prepareStatement(updateStatement);)
		{
                    int ndx = 1;
			updateTotal.setString(ndx++, info.getPath());
			updateTotal.setTimestamp(ndx++, new Timestamp(info.getImageDate()));
			updateTotal.setTimestamp(ndx++, new Timestamp(info.getModifiedTime()));
			updateTotal.setString(ndx++, info.getTimePath());
			updateTotal.setString(ndx++, info.getRoot());
			
			int executeUpdate = updateTotal.executeUpdate();
			if (executeUpdate != 0)
			{
				
			}
		}
	}
	public static void updateImage(Connection conn, ImageInfo info) throws SQLException
	{
		// this doesn't work yet...
		if (Math.random() > -1) return;
		
		String updateStatement =
		" update IMAGES					" +
		" set						" +
		" 	IMAGES.IMAGE_TIME=?,			" +
		" 	IMAGES.LAST_MODIFIED=?			" +
		" from						" +
		" 	IMAGES					" +
		" 	inner join ROOTS			" +
		" 		on IMAGES.RID = ROOTS.R_ID	" +
		" where						" +
		" 	IMAGES.PATH=? and ROOTS.PATH=?;		";
		
		try (PreparedStatement updateTotal = conn.prepareStatement(updateStatement);)
		{
                    int ndx = 1;
			updateTotal.setTimestamp(ndx++, new Timestamp(info.getImageDate()));
			updateTotal.setTimestamp(ndx++, new Timestamp(info.getModifiedTime()));
			updateTotal.setString(ndx++, info.getPath());
			updateTotal.setString(ndx++, info.getRoot());
			
			int executeUpdate = updateTotal.executeUpdate();
			if (executeUpdate != 0)
			{
				
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param root 		the root name as identified in the database and apache2
	 * @param url  		the url that this root is mapped to and apache2
	 * @param starting 	the rest of the path after the root that is supposed to be updated 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void updateRoot(Path root, String url, Path starting) throws SQLException, IOException
	{
		String table    = Registry.getRegistry().getSettings().getDbDatabaseName();
		String user     = Registry.getRegistry().getSettings().getDbUsername();
		String password = Registry.getRegistry().getSettings().getDbPassword();
                
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);)
		{
			addAllFiles(conn, root, url, root.resolve(starting));
			removeExtraImages(conn, root, starting);
		}
	}
	private static void addAllFiles(Connection conn, Path root, String url, Path start) throws IOException, SQLException
	{
		Integer rId = getRoot(conn, root.toString());
		if (rId == null)
		{
			insertRoot(conn, root.toString(), url);
		}
		Files.walkFileTree(start, new AddFileVisitor(conn, root));
	}

	private static void removeExtraImages(Connection conn, Path root, Path subdirectory) throws SQLException
	{
		String query =    " select IMAGES.I_ID, IMAGES.PATH as path, ROOTS.PATH as root		"
				+ " from IMAGES 							"
				+ " 	inner join ROOTS on		    				"
				+ " 		IMAGES.RID = ROOTS.R_ID 			        "
				+ " where ROOTS.PATH=? and IMAGES.PATH like ?                          ;";
		
		StringBuilder newStmt = new StringBuilder();
		newStmt.append("delete from IMAGES where IMAGES.I_ID in (");
		boolean first = true;
		
		try (PreparedStatement updateTotal = conn.prepareStatement(query);)
		{
                        int ndx = 1;
			updateTotal.setString(ndx++, root.toString());
			updateTotal.setString(ndx++, subdirectory.toString() + "%");
			try (ResultSet results = updateTotal.executeQuery();)
			{
				while (results.next())
				{
					// Don't need to get the root: it was passed here...
                                        ndx = 1;
					int    id      = results.getInt(ndx++);
					String relStr  = results.getString(ndx++);
					String rootStr = results.getString(ndx++);
					
					Path p = Paths.get(rootStr + File.separator + relStr);
					System.out.print("Checking " + p.toString() + "... ");
					
					if (Files.exists(p) && Files.isRegularFile(p))
					{
						System.out.println("Good.");
						continue;
					}
					if (first)
					{
						first = false;
					}
					else
					{
						System.out.println("Removing.");
						newStmt.append(", ");
					}
					newStmt.append(id);
				}
			}
		}
		
		// no files should be deleted...
		if (first) return;

		newStmt.append(");");
		
		try (PreparedStatement deleteStmt = conn.prepareStatement(newStmt.toString());)
		{
			int executeUpdate = deleteStmt.executeUpdate();
			if (executeUpdate != 0)
			{
				
			}
		}
	}
	
	
	
	
	
	
	
	public static void createDatabase() throws SQLException
	{
		String table    = Registry.getRegistry().getSettings().getDbDatabaseName();
		String user     = Registry.getRegistry().getSettings().getDbUsername();
		String password = Registry.getRegistry().getSettings().getDbUsername();
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table 
				+ "?" + "user=" + user + "&password=" + password);)
		{
			// TODO...
		}
	}
	
	

	
	
	/*
	// TO BE MOVED:
	public void fillYears(Connection conn, LinkedList<Integer> years) throws SQLException
	{
		// TODO: This should limit the number of results....
		String query = "select distinct(YEAR(IMAGE_TIME)) as y from IMAGES order by y;";
		try (PreparedStatement updateTotal = conn.prepareStatement(query);)
		{	
			try (ResultSet results = updateTotal.executeQuery();)
			{
				while (results.next())
				{
					years.add(results.getInt(1));
				}
			}
		}
	}

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
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static final class ImageData
	{
		// will eventually have rating
		long timestamp;
		String root;
		String path;
		LinkedList<String> comments = new LinkedList<>();
		LinkedList<String> tags = new LinkedList<>();
		LinkedList<String> people = new LinkedList<>();
		
	}
	public void fillDays(Connection conn, int year, int month, int day, LinkedList<ImageData> images)
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
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	*/
}











