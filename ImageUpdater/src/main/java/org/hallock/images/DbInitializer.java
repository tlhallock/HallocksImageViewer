package org.hallock.images;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbInitializer
{
	public static boolean isDatabaseCreated() throws SQLException
	{
		String table    = SqlSettings.getDbDatabaseName();
		String user     = SqlSettings.getDbUsername();
		String password = SqlSettings.getDbPassword();
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table 
				+ "?" + "user=" + user + "&password=" + password);)
		{
			// TODO
			String query;

			// Check that ROOTS exists
			query = "select PATH, URL from ROOTS limit 1;";
			try (PreparedStatement updateTotal = conn.prepareStatement(query);)
			{	
				try (ResultSet results = updateTotal.executeQuery();) {}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return false;
			}
			

			// Check that IMAGES exists
			query = "select PATH, TIME_PATH from IMAGES limit 1;";
			try (PreparedStatement updateTotal = conn.prepareStatement(query);)
			{	
				try (ResultSet results = updateTotal.executeQuery();) {}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return false;
			}


			// Check that USERS exists
			query = "select NAME, PASSWORD, IS_ADMIN from USERS limit 1;";
			try (PreparedStatement updateTotal = conn.prepareStatement(query);)
			{	
				try (ResultSet results = updateTotal.executeQuery();) {}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return false;
			}

			// Check that SETTINGS exists
			query = "select NAME, VALUE, DESCRIPTION from SETTINGS limit 1;";
			try (PreparedStatement updateTotal = conn.prepareStatement(query);)
			{	
				try (ResultSet results = updateTotal.executeQuery();) {}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
	}
	
	public static boolean checkDabaseCredentials()
	{
		String table    = SqlSettings.getDbDatabaseName();
		String user     = SqlSettings.getDbUsername();
		String password = SqlSettings.getDbPassword();
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table 
				+ "?" + "user=" + user + "&password=" + password);)
		{
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static void createDatabase() throws SQLException, IOException
	{
		executeScript("/create.sql");
	}
	
	public static void destroyDatabase() throws SQLException, IOException
	{
		executeScript("/delete.sql");
	}
	
	
	
	private static void executeScript(String path) throws SQLException, IOException
	{
		StringBuilder builder = new StringBuilder();
		try (BufferedReader input = new BufferedReader(new InputStreamReader(DbInitializer.class.getResourceAsStream(path)));)
		{
			String line;
			while((line = input.readLine()) != null)
			{
				builder.append(line).append('\n');
			}
		}

		String table    = SqlSettings.getDbDatabaseName();
		String user     = SqlSettings.getDbUsername();
		String password = SqlSettings.getDbPassword();
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + table 
				+ "?" + "user=" + user + "&password=" + password);)
		{
			for (String stmt : builder.toString().split(";"))
			{
				System.out.println("Executing " + stmt);
				try (PreparedStatement updateTotal = conn.prepareStatement(stmt + ";");)
				{	
					int result = updateTotal.executeUpdate();
					if (result != 0)
					{
						
					}
				}
				catch (SQLException ex)
				{
					System.out.println("Error executing statement " + stmt);
					System.out.println("I wish there were some way to create index if not exists....");
				}
			}
		}
	}
}
