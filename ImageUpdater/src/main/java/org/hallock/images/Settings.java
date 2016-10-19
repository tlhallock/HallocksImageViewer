//package org.hallock.images;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Properties;
//
//public class Settings
//{
//	private static final String DB_USERNAME_KEY = "db.user";
//	private static final String DB_PASSWORD_KEY = "db.pass";
//	private static final String DB_TABLE_KEY = "db.table";
//
//	private static final String URL_LOCATION_KEY = "url.location";
//	
//	private static final String HOST_NAME_KEY = "hostname";
//	private static final String HOST_PORT_KEY = "port";
//	
//	private static final String DEFAULT_COLUMNS_KEY = "default.cols";
//	private static final String DEFAULT_ROWS_KEY = "default.rows";
//	
//	
//	
//	
//	
//
//	private static final String DEFAULT_USERNAME = "imagesuser";
//	private static final String DEFAULT_PASSWORD = "imagepass";
//	private static final String DEFAULT_DATABASE = "images";
//
//	private static final String DEFAULT_URLS_LOCATION = "urls.props";
//	
//	private static final String DEFAULT_HOSTNAME = "localhost";
//	private static final String DEFAULT_PORT = "80";
//	
//	private static final String DEFAULT_COLS = "3";
//	private static final String DEFAULT_ROWS = "10";
//	
//	
//	
//	private Properties properties;
//	
//	
//	public Settings() { properties = new Properties(); }
//	public Settings(String location) throws IOException
//	{
//		load(location);
//	}
//	
//	public void load(String location) throws IOException
//	{
//		properties = new Properties();
//		try (InputStream newInputStream = Files.newInputStream(Paths.get(location));)
//		{
//			properties.load(newInputStream);
//		}
//	}
//	
//	public Settings save(String location) throws IOException
//	{
//		try (OutputStream newOutputStream = Files.newOutputStream(Paths.get(location));)
//		{
//			properties.store(newOutputStream, "here is a comment");
//		}
//		return this;
//	}
//	public void fillDefaults()
//	{
//		if (properties.getProperty(DB_USERNAME_KEY	) == null) properties.setProperty(DB_USERNAME_KEY	, DEFAULT_USERNAME);
//		if (properties.getProperty(DB_PASSWORD_KEY	) == null) properties.setProperty(DB_PASSWORD_KEY	, DEFAULT_PASSWORD);
//		if (properties.getProperty(DB_TABLE_KEY		) == null) properties.setProperty(DB_TABLE_KEY		, DEFAULT_DATABASE);
//		if (properties.getProperty(URL_LOCATION_KEY	) == null) properties.setProperty(URL_LOCATION_KEY	, DEFAULT_URLS_LOCATION);
//		if (properties.getProperty(HOST_NAME_KEY	) == null) properties.setProperty(HOST_NAME_KEY		, DEFAULT_HOSTNAME);
//		if (properties.getProperty(HOST_PORT_KEY	) == null) properties.setProperty(HOST_PORT_KEY		, DEFAULT_PORT);
//		if (properties.getProperty(DEFAULT_COLUMNS_KEY	) == null) properties.setProperty(DEFAULT_COLUMNS_KEY	, DEFAULT_COLS);
//		if (properties.getProperty(DEFAULT_ROWS_KEY	) == null) properties.setProperty(DEFAULT_ROWS_KEY	, DEFAULT_ROWS);
//	}
//	public void setDefaults()
//	{
//		properties.setProperty(DB_USERNAME_KEY		, DEFAULT_USERNAME);
//		properties.setProperty(DB_PASSWORD_KEY		, DEFAULT_PASSWORD);
//		properties.setProperty(DB_TABLE_KEY		, DEFAULT_DATABASE);
//		properties.setProperty(URL_LOCATION_KEY		, DEFAULT_URLS_LOCATION);
//		properties.setProperty(HOST_NAME_KEY		, DEFAULT_HOSTNAME);
//		properties.setProperty(HOST_PORT_KEY		, DEFAULT_PORT);
//		properties.setProperty(DEFAULT_COLUMNS_KEY	, DEFAULT_COLS);
//		properties.setProperty(DEFAULT_ROWS_KEY		, DEFAULT_ROWS);
//	}
//	
//	public String getDbUsername()
//	{
//		return properties.getProperty(DB_USERNAME_KEY, DEFAULT_USERNAME);
//	}
//	public String getDbPassword()
//	{
//		return properties.getProperty(DB_PASSWORD_KEY, DEFAULT_PASSWORD);
//	}
//	public String getDbDatabaseName()
//	{
//		return properties.getProperty(DB_TABLE_KEY, DEFAULT_DATABASE);
//	}
//	public String getUrlsLocation()
//	{
//		return properties.getProperty(URL_LOCATION_KEY, DEFAULT_URLS_LOCATION);
//	}
//	public String getHostName()
//	{
//		return properties.getProperty(HOST_NAME_KEY, DEFAULT_HOSTNAME);
//	}
//	public int getPort()
//	{
//		String port = properties.getProperty(HOST_PORT_KEY, DEFAULT_PORT);
//		try
//		{
//			return Integer.parseInt(port);
//		}
//		catch (NumberFormatException ex)
//		{
//			ex.printStackTrace();
//		}
//		return Integer.parseInt(DEFAULT_PORT);
//	}
//	public int getDefaultColumns()
//	{
//		String port = properties.getProperty(DEFAULT_COLUMNS_KEY, DEFAULT_COLS);
//		try
//		{
//			return Integer.parseInt(port);
//		}
//		catch (NumberFormatException ex)
//		{
//			ex.printStackTrace();
//		}
//		return Integer.parseInt(DEFAULT_COLS);
//	}
//	public int getDefaultRows()
//	{
//		String port = properties.getProperty(DEFAULT_ROWS_KEY, DEFAULT_ROWS);
//		try
//		{
//			return Integer.parseInt(port);
//		}
//		catch (NumberFormatException ex)
//		{
//			ex.printStackTrace();
//		}
//		return Integer.parseInt(DEFAULT_ROWS);
//	}
//        public String getEncoding()
//        {
//            return "UTF-8";
//        }
//        public int getMaxPagesAtOnce()
//        {
//            return -1;
//        }
//}
