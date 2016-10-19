package org.hallock.images;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public final class Roots
{
	public static void insertRoot(String root, String url) throws SQLException
	{
		String table    = SqlSettings.getDbDatabaseName();
		String user     = SqlSettings.getDbUsername();
		String password = SqlSettings.getDbPassword();

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);)
		{
			insertRoot(conn, root, url);
		}
	}
	public static void updateRoot(String root, String url) throws SQLException
	{
		String table    = SqlSettings.getDbDatabaseName();
		String user     = SqlSettings.getDbUsername();
		String password = SqlSettings.getDbPassword();

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);)
		{
			insertRoot(conn, root, url);
		}
	}
	
	
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
	public static void updateRoot(Connection conn, String root, String url) throws SQLException
	{
		String updateStatement = "update ROOTS set URL=? where PATH=?;";
		try (PreparedStatement updateTotal = conn.prepareStatement(updateStatement);)
		{
			updateTotal.setString(1, url);
			updateTotal.setString(2, root);
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
	
	
	
	public static RootsMapper createRootsMapper() throws SQLException
	{
		RootsMapper mapper = new RootsMapper();
		
		String table    = SqlSettings.getDbDatabaseName();
		String user     = SqlSettings.getDbUsername();
		String password = SqlSettings.getDbPassword();

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table, user, password);)
		{
			mapper.load(conn);
		}
		
		return mapper;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static class RootsMapper
	{
		private HashMap<String, String>	path2Url	= new HashMap<>();
		
		private RootsMapper() {}
                
                public Set<Entry<String, String>> list()
                {
                    return path2Url.entrySet();
                }

		public void load(Connection conn) throws SQLException
		{
			path2Url.clear();
			String updateStatement = "select PATH, URL from ROOTS;";
			try (PreparedStatement updateTotal = conn.prepareStatement(updateStatement);
					ResultSet results = updateTotal.executeQuery();)
			{
				while (results.next())
				{
					String path = results.getString(1);
					String url = results.getString(2);
					path2Url.put(path, url);
				}
			}
		}
		
		public String get(String path)
		{
			return path2Url.get(path);
		}

		void print(PrintStream out)
		{
			for (Entry<String, String> entry : path2Url.entrySet())
			{
				out.println("        Alias /" + entry.getValue() + " " + entry.getKey());
				out.println("       ");
				out.println("        <Directory \"" + entry.getKey() + "\">");
				out.println("                Order Allow,Deny");
				out.println("                Allow From All");
				out.println("                Require all granted");
				out.println("                Options +Indexes");
				out.println("        </Directory>");
			}
		}
	}
}
