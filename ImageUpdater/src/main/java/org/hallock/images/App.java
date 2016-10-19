package org.hallock.images;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.hallock.images.Roots.RootsMapper;



/**
 * Hello world!
 *
 */
public class App
{
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		if (true)
		{
			args = new String[]
			{
//					InitializationArgs.SETTINGS_FILE_ARG,
//					"/home/thallock/Documents/Source/hosting/res/settings.props",
					
					InitializationArgs.ACTION,
					"update",
					
					InitializationArgs.ROOT_ARG,
					"/home/thallock/Pictures/toUp",
					
					InitializationArgs.URL_ARG,
					"static",
			};
		}
		
		InitializationArgs iniargs = new InitializationArgs(args);
		switch (Initialization.attemptToInitializeApp())
		{
			case AllGood:
			case NeedsToBeConfigured:
				break;
			case DatabaseError:
			case InvalidDbCredentials:
			case MissingClasses:
			case MissingFiles:
				System.exit(-1);
		}
		
		if (iniargs.getAction().equals("update"))
		{
			update(iniargs);
		}
		else if (iniargs.getAction().equals("map"))
		{
			RootsMapper mapper = Roots.createRootsMapper();
			
			String url = iniargs.getUrl();
			String root = iniargs.getRoot();
			
			map(mapper, root, url);
		}
		else if (iniargs.getAction().equals("print"))
		{
			print();
		}
	}

	private static void update(InitializationArgs iniargs) throws SQLException, IOException
	{
		String root = iniargs.getRoot();
		if (root == null) throw new NullPointerException("No root given.");
		
		RootsMapper mapper = Roots.createRootsMapper();
		
		String url = null;
		if (iniargs.getUrl() != null)
		{
			url = iniargs.getUrl();
			map(mapper, root, url);
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
		
		ImageSynchronization.updateRoot(Paths.get(root), url, Paths.get(start));
	}
	
	private static void map(RootsMapper mapper, String root, String url) throws SQLException
	{
		if (url == null || root == null)
		{
			throw new NullPointerException("Must have both a root and a url");
		}
		
		String current = mapper.get(root);
		if (current == null)
		{
			Roots.insertRoot(root, url);
		}
		else if (!current.equals(url))
		{
			Roots.updateRoot(root, url);
		}
	}

	private static void print() throws SQLException
	{
		RootsMapper mapper = Roots.createRootsMapper();
		mapper.print(System.out);
	}
}
