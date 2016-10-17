package org.hallock.images;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.imaging.ImageReadException;

public class AddFileVisitor extends SimpleFileVisitor<Path>
{
	private Connection conn;
	private Path root;

	public AddFileVisitor(Connection conn, Path root)
	{
		this.conn = conn;
		this.root = root;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
	{
		if (!file.toString().toLowerCase().endsWith(".jpg"))
		{
			return FileVisitResult.CONTINUE;
		}
		try
		{
			System.out.println("Visiting " + file);
			ImageInfo info = ImageInfo.readInitialImageInfo(root, file);
			Long lastModified = Database.getFileModificationTime(conn, info);
			if (lastModified == null)
			{
				ImageInfo.readRemainingImageInfo(root, file, info);
				Database.insertImage(conn, info);
			}
			else if (lastModified < info.getModifiedTime())
			{
				System.out.println("db modified: " + new Date(lastModified));
				System.out.println("fs modified: " + new Date(info.getModifiedTime()));
				ImageInfo.readRemainingImageInfo(root, file, info);
				Database.updateImage(conn, info);
			}
		} catch (ImageReadException e)
		{
			e.printStackTrace();
		} catch (ParseException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(10);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
	{
		if (e == null)
		{
			return FileVisitResult.CONTINUE;
		}
		else
		{
			// directory iteration failed
			throw e;
		}
	}
}
