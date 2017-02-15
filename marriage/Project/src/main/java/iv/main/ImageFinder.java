package iv.main;

import iv.main.MainApplicationFrame.SearchStopper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.imaging.ImageReadException;

public class ImageFinder
{
	public static void dumpAllImages(
			ImageSet entries,
			Path rootLocation,
			boolean recurse,
			Preferences preferences, 
			SearchStopper stopper,
			Logger logger) throws IOException, InterruptedException
	{
		findAllImages(
			entries.getRootName(rootLocation),
			rootLocation.toString(),
			Paths.get(""),
			rootLocation,
			preferences,
			recurse,
			entries,
			stopper,
			logger);
		entries.sort();
	}
	
	private static void findAllImages(
			String rootName,
			String rootPath,
			Path pathFromRoot,
			Path currentPathObject,
			Preferences pref, 
			boolean recurse,
			ImageSet soFar,
			SearchStopper stopper,
			Logger logger) throws IOException
	{
		logger.log("Scanning " + currentPathObject);
		
		if (stopper.shouldStop())
			return;
		
		if (pref.ignoreHidden && Files.isHidden(currentPathObject))
			logger.log("Ignoring hidden directory: " + currentPathObject);
		
		if (Files.isRegularFile(currentPathObject))
		{
			if (!matchesExtensions(pref, currentPathObject.toString()))
					return;

			ImageEntry newEntry = new ImageEntry(rootName, pathFromRoot.toString());
			try
			{
				newEntry.readInfo(currentPathObject);
			}
			catch (ImageReadException | NoSuchAlgorithmException | ParseException e)
			{
				e.printStackTrace();
				return;
			}
			soFar.add(newEntry);
			return;
		}
		
		if (!Files.isDirectory(currentPathObject))
			return;
		
		if (!recurse)
			return;
		
		// Close the directory stream before recursing
		LinkedList<Path> directories = new LinkedList<>();
		try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(currentPathObject);)
		{
			Iterator<Path> iterator = newDirectoryStream.iterator();
			while (iterator.hasNext())
			{
				if (stopper.shouldStop())
					return;

				directories.add(iterator.next());
			}
		}
		catch (java.nio.file.AccessDeniedException ex)
		{
			logger.log("Access denied " + currentPathObject);
			return;
		}
		
		for (Path p : directories)
		{
			if (stopper.shouldStop())
				return;
			
			findAllImages(
				rootName,
				rootPath,
				pathFromRoot.resolve(p.getName(p.getNameCount() - 1)),
				p,
				pref,
				recurse,
				soFar,
				stopper,
				logger);
		}
	}


	private static boolean matchesExtensions(Preferences pref, String string)
	{
		for (String ext : pref.getImageExtensions())
			if (string.endsWith(ext))
				return true;
		return false;
	}
}
