package org.hallock.images;

import java.io.IOException;
import java.sql.SQLException;

public class Initialization
{
	public enum InitializationResults
	{
		MissingClasses("Some classes failed to load."),
		MissingFiles("Some files were missing in the installation."),
		InvalidDbCredentials("Unable to connect to the database."),
		DatabaseError("Unable to create database."),
		NeedsToBeConfigured("Initialization success, but not configured."),
		AllGood("Initialization success."),
		
		;
		String desc;
		
		InitializationResults(String desc)
		{
			this.desc = desc;
		}
		
		
	}
	
	public static InitializationResults attemptToInitializeApp()
	{
		try
		{
//			JpegImageMetadata.class.getName();
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			e.printStackTrace();
			return SqlSettings.setInitializationResults(InitializationResults.MissingClasses);
		}
		
		try
		{
			SqlSettings.loadDefaultDbInfo("/dbinfo.props");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return SqlSettings.setInitializationResults(InitializationResults.MissingFiles);
		}
		
		if (!DbInitializer.checkDabaseCredentials())
		{
			return SqlSettings.setInitializationResults(InitializationResults.InvalidDbCredentials);
		}
		

		boolean dbCreated = false;
		try
		{
			dbCreated = DbInitializer.isDatabaseCreated();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
			return SqlSettings.setInitializationResults(InitializationResults.InvalidDbCredentials);
		}
		
		if (!dbCreated)
		{
			try
			{
				DbInitializer.createDatabase();
			}
			catch (SQLException | IOException e)
			{
				return SqlSettings.setInitializationResults(InitializationResults.DatabaseError);
			}
		}

		SqlSettings settings = null;
		try
		{
			settings = SqlSettings.createSettings();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return SqlSettings.setInitializationResults(InitializationResults.InvalidDbCredentials);
		}
		
		boolean configured = false;
		try
		{
			configured = settings.isConfigured();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return SqlSettings.setInitializationResults(InitializationResults.InvalidDbCredentials);
		}
		
		if (!configured)
		{
			return SqlSettings.setInitializationResults(InitializationResults.NeedsToBeConfigured);
		}
		
		
		return SqlSettings.setInitializationResults(InitializationResults.AllGood);
	}
}
