/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.images;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hallock.images.Initialization.InitializationResults;

/**
 *
 * @author thallock
 */
public final class SqlSettings
{
	private static final String DB_USERNAME_KEY = "db.user";
	private static final String DB_PASSWORD_KEY = "db.pass";
	private static final String DB_DATABASE_KEY = "db.table";
	
	private static final String DEFAULT_USERNAME = "imagesuser";
	private static final String DEFAULT_PASSWORD = "imagepass";
	private static final String DEFAULT_DATABASE = "images";

	private static String dbUsername = DEFAULT_USERNAME;
	private static String dbPassword = DEFAULT_PASSWORD;
	private static String dbDatabase = DEFAULT_DATABASE;
	
	private static InitializationResults results;
	
	
	
	
	
	private StringSetting hostname = new StringSetting("hostname", "localhost", "This is the fully qualified hostname.");
	private IntegerSetting hostport = new BoundedIntegerSetting("port", 80, "This is the port that this webapp runs on.", 1, Integer.MAX_VALUE);

	private StringSetting imagesHostname = new StringSetting("ihostname", "localhost", "This is the hostname of the apache webserver to serve static images.");
	private IntegerSetting imagesPort = new BoundedIntegerSetting("iport", 80, "This is the port of the apache webserver to serve static images.", 1, Integer.MAX_VALUE);
	
	private IntegerSetting defaultRows = new BoundedIntegerSetting("drows", 5, "This is the default number of rows to show up when viewing images.", 1, Integer.MAX_VALUE);
	private IntegerSetting defaultColumns = new BoundedIntegerSetting("dcols", 3, "This is the default number of columns to show up when viewing images.", 1, Integer.MAX_VALUE);
	
	private StringSetting encoding = new StringSetting("encoding", "UTF-8", "Idk what this is.");
	
	private IntegerSetting maxPages = new IntegerSetting("maxpages", 10, "If the number of pages are less than this amount, then rather than a prev/next link appearing, links are provided to each other page.");
	private BooleanSetting configured = new BooleanSetting("configured", false, "A flag for whether the user has ran the configuration script.");

	private SqlSetting<?>[] allSettings = new SqlSetting[]
	{
		hostname,
		hostport,
		imagesHostname,
		imagesPort,
		defaultRows,
		defaultColumns,
		encoding,
		maxPages,
		configured,
	};
	
	
	
	
	
	private SqlSettings() {}
	
	

	public void resetSettings()
	{
		for (SqlSetting<?> setting : allSettings)
		{
			setting.reset();
		}
	}
	public void apply(Connection conn) throws SQLException
	{
		for (SqlSetting<?> setting : allSettings)
		{
			setting.update(conn);
		}
	}
	public void insert(Connection conn) throws SQLException
	{
		for (SqlSetting<?> setting : allSettings)
		{
			setting.insert(conn);
		}
	}
	public void read(Connection conn) throws SQLException
	{
		for (SqlSetting<?> setting : allSettings)
		{
			setting.read(conn);
		}
	}
	
	
	public static String getDbUsername()
	{
		return dbUsername;
	}
	public static String getDbDatabaseName()
	{
		return dbDatabase;
	}
	public static String getDbPassword()
	{
		return dbPassword;
	}
	public static InitializationResults getInitializationResults()
	{
		return results;
	}
	static InitializationResults setInitializationResults(InitializationResults results)
	{
		return SqlSettings.results = results;
	}
	
	
	
	
	
	
	
	
	public int getDefaultRows()
        {
            return defaultRows.getValue();
        }
	public int getDefaultColumns()
        {
            return defaultColumns.getValue();
        }
	public String getImagesHostname()
	{
		return imagesHostname.getValue();
	}
	public int getImagesPort()
	{
		return imagesPort.getValue();
	}
	public String getEncoding()
	{
		return encoding.getValue();
	}
	public int getMaxPagesAtOnce()
	{
		return maxPages.getValue();
	}
	public boolean isConfigured() throws SQLException
	{
		boolean claimed = configured.getValue();
		
		// We want to ensure that there is at least one admin...
		String query = "select U_ID from USERS where IS_ADMIN=true limit 1;";
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDatabase, dbUsername, dbPassword);
			PreparedStatement updateTotal = conn.prepareStatement(query);
			ResultSet results = updateTotal.executeQuery();)
		{
			if (!results.next())
			{
				return false;
			}
		}
		
		return claimed;
	}
	public void setConfigured()
	{
		configured.setValue(true);
	}
        
        
        
        
        
        
        
	
	
	
	
	private static SqlSettings cachedSettings = null;
//	public synchronized static SqlSettings getCachedSettings() throws SQLException
//	{
//		if (cachedSettings == null)
//		{
//			return createSettings();
//		}
//		else
//		{
//			return cachedSettings;
//		}
//	}
	public synchronized static SqlSettings createSettings() throws SQLException
	{
		SqlSettings settings = new SqlSettings();
                
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDatabase, dbUsername, dbPassword);)
		{
			settings.read(conn);
		}
		
		return cachedSettings = settings;
	}
	public synchronized static void applySettings(SqlSettings settings) throws SQLException
	{
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDatabase, dbUsername, dbPassword);)
		{
			settings.apply(conn);
		}
		cachedSettings = null;
	}
	public static void loadDefaultDbInfo(String path) throws IOException
	{
		Properties properties = new Properties();
		try (InputStream newInputStream = SqlSettings.class.getResourceAsStream(path);)
		{
			properties.load(newInputStream);
		}

		dbUsername = properties.getProperty(DB_USERNAME_KEY, DEFAULT_USERNAME);
		dbPassword = properties.getProperty(DB_PASSWORD_KEY, DEFAULT_PASSWORD);
		dbDatabase = properties.getProperty(DB_DATABASE_KEY, DEFAULT_DATABASE);
	}
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        private static abstract class SqlSetting<T>
        {
        	private static final String insertString = "insert into SETTINGS (name, value)  values (?, ?);";
        	private static final String updateString = "update SETTINGS SET value=? where name=?;";
        	private static final String readString   = "select value from SETTINGS where name=?;";
        	
        	protected String name;
        	protected T defaultValue;
        	protected String description;
        	protected T value;
        	
		public SqlSetting(String name, T defaultValue, String description)
		{
			this.name = name;
			this.defaultValue = defaultValue;
			this.description = description;
		}
		
        	protected abstract String toString(T t);
        	protected abstract T parse(String string) throws Exception;
        	
        	public String getDescription()
        	{
        		return description;
        	}
        	
        	public void reset()
        	{
        		setValue(defaultValue);
        	}
        	public void setValue(T value)
        	{
        		this.value = value;
        		checkValue();
        	}
		public T getValue()
		{
			return value;
		}
		
		protected void checkValue() {}
        	

		// Insert and update should be the same method...
		public void insert(Connection conn) throws SQLException
		{
			try (PreparedStatement updateTotal = conn.prepareStatement(insertString);)
			{
				updateTotal.setString(1, name);
				updateTotal.setString(2, toString(value));
				int executeUpdate = updateTotal.executeUpdate();
				if (executeUpdate != 0)
				{
					
				}
			}
		}
		public void update(Connection conn) throws SQLException
		{
			try (PreparedStatement updateTotal = conn.prepareStatement(updateString);)
			{
				updateTotal.setString(1, toString(value));
				updateTotal.setString(2, name);
				int executeUpdate = updateTotal.executeUpdate();
				if (executeUpdate != 0)
				{
					
				}
			}
		}
		
		public void read(Connection conn) throws SQLException
		{
			try (PreparedStatement updateTotal = conn.prepareStatement(readString);)
			{	
				updateTotal.setString(1, name);
				try (ResultSet results = updateTotal.executeQuery();)
				{
					if (results.next())
					{
						try
						{
							setValue(parse(results.getString(1)));
						}
						catch (Exception e)
						{
							setValue(defaultValue);
							e.printStackTrace();
						}
					}
					else
					{
						reset();
					}
				}
			}
		}
        }
        private static class StringSetting extends SqlSetting<String>
        {
		public StringSetting(String name, String defaultValue, String description)
		{
			super(name, defaultValue, description);
		}

		@Override
		protected String toString(String t)
		{
			return t;
		}

		@Override
		protected String parse(String string)
		{
			return string;
		}
        }
        private static class IntegerSetting extends SqlSetting<Integer>
        {
		public IntegerSetting(String name, Integer defaultValue, String description)
		{
			super(name, defaultValue, description);
		}

		@Override
		protected String toString(Integer t)
		{
			return String.valueOf(t);
		}

		@Override
		protected Integer parse(String string)
		{
			return Integer.parseInt(string);
		}
        }
        private static class BoundedIntegerSetting extends IntegerSetting
        {
        	int min;
        	int max;

		public BoundedIntegerSetting(String name, Integer defaultValue, String description, int min, int max)
		{
			super(name, defaultValue, description);
			this.min = min;
			this.max = max;
		}
		
		@Override
		protected void checkValue()
		{
			if (value < min)
				value = min;
			if (value > max)
				value = max;
		}
        }
        private static class BooleanSetting extends SqlSetting<Boolean>
        {
		public BooleanSetting(String name, Boolean defaultValue, String description)
		{
			super(name, defaultValue, description);
		}

		@Override
		protected String toString(Boolean t)
		{
			return String.valueOf(t);
		}

		@Override
		protected Boolean parse(String string)
		{
			return Boolean.parseBoolean(string);
		}
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//
//        void print(PrintStream out) {
//            for (Entry<String, String> entry : path2Url.entrySet())
//            {
//                out.println("        Alias /" + entry.getValue() + " " + entry.getKey());
//                out.println("       ");
//                out.println("        <Directory \"" + entry.getKey() + "\">");
//                out.println("                Order Allow,Deny");
//                out.println("                Allow From All");
//                out.println("                Require all granted");
//                out.println("                Options +Indexes");
//                out.println("        </Directory>");
//            }
//        }
}
