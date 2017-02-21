package iv.main;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonGenerator;

public class ImageSet
{
	private BigDecimal numberOfBytes = BigDecimal.ZERO;
	private ArrayList<ImageEntry> images = new ArrayList<>();
	private HashMap<String, String> rootNames = new HashMap<>();
	private HashSet<String> checksums = new HashSet<>();

	public ImageSet() {}
	
	public ImageSet(ImageSet other)
	{
		images = new ArrayList<>(other.images.size());
		for (ImageEntry entry : other.images)
			add(new ImageEntry(entry));
		this.rootNames = new HashMap<String, String>(other.rootNames);
	}

	public ImageSet(JsonObject jsonObject)
	{
		JsonObject roots = jsonObject.getJsonObject("roots");
		for (Entry<String, JsonValue> entry : roots.entrySet())
			rootNames.put(entry.getKey(), ((JsonString) entry.getValue()).getString());
		
		JsonArray jsonArray = jsonObject.getJsonArray("images");
		for (int i = 0; i < jsonArray.size(); i++)
			add(new ImageEntry(jsonArray.getJsonObject(i)));
	}

	public void remove(ImageEntry currentEntry)
	{
		int index = -1;
		for (int i = 0; i < images.size() && index < 0; i++)
			if (images.get(i).equals(currentEntry))
				index = i;
		if (index < 0)
			return;
		images.remove(index);
		numberOfBytes = numberOfBytes.subtract(new BigDecimal(currentEntry.getFileSize()));
		checksums.remove(currentEntry.getChecksum());
	}

	TreeSet<String> collectAllLocations()
	{
		TreeSet<String> retVal = new TreeSet<>();

		for (ImageEntry entry : images)
			retVal.add(entry.getLocation());

		return retVal;
	}

	public void addAll(ImageSet images2)
	{
		for (ImageEntry entry : images2.images)
			add(entry);

		Collections.sort(images);
	}

	public void add(ImageEntry images2)
	{
		if (checksums.contains(images2.getChecksum()))
			return;
		numberOfBytes = numberOfBytes.add(new BigDecimal(images2.getFileSize()));
		images.add(images2);
	}

	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		generator.writeNumberField("numbytes", numberOfBytes);
		
		generator.writeFieldName("roots");
		generator.writeStartObject();
		for (Entry<String, String> e : rootNames.entrySet())
			generator.writeStringField(e.getKey(), e.getValue());
		generator.writeEndObject();
		
		generator.writeFieldName("images");
		generator.writeStartArray();
		for (ImageEntry entry : images)
			entry.write(generator);
		generator.writeEndArray();
		
		generator.writeEndObject();
	}

	public int size()
	{
		return images.size();
	}

	public ImageEntry get(int currentIndex)
	{
		return images.get(currentIndex);
	}

	public void writeLatex(String prefix, int maxImages, Logger logger) throws FileNotFoundException
	{
		int index = 0;
		
		Collections.sort(images, new Comparator<ImageEntry>() {
			@Override
			public int compare(ImageEntry o1, ImageEntry o2) {
				return Long.compare(o1.getComparable(), o2.getComparable());
			}});
		
		for (int outerCount = 0; index < images.size(); outerCount++)
		{
			String name = prefix + String.format("%04d", outerCount) + ".tex";
			logger.log("Writing text file.");
			
			try (PrintStream texStream = new PrintStream(name))
			{
				texStream.print("\\documentclass{article}"                                           + "\n");
				texStream.print("\\title{Trever and Yu's Photo Album}"     + "\n");
				texStream.print("\\author{Trever and Yu}"                                          + "\n");
				texStream.print("\\usepackage{graphicx}"                                             + "\n");
				texStream.print("\\begin{document}"                                                  + "\n");
				texStream.print("\\maketitle"                                                        + "\n");
				texStream.print("\\pagenumbering{gobble}"                                            + "\n");
				texStream.print("\\newpage"                                                          + "\n");
				
				for (int innerCount = 0; index < images.size() && innerCount < maxImages; innerCount++)
				{
					ImageEntry imageEntry = images.get(index++);
					imageEntry.writeLatex(texStream, resolve(imageEntry));
					logger.setProgress(index, images.size());
				}
				
				texStream.print("\\end{document}"                                                   + "\n");
			}
		}
	}

	public void clear()
	{
		images.clear();
		checksums.clear();
	}
	
	public ImageSet copyImagesToDirectory(Path imageDirectory, Logger logger) throws IOException
	{
		ImageSet newImageSet = new ImageSet();
		String rootName = newImageSet.getRootName(imageDirectory);
		
		if (Files.exists(imageDirectory))
		{
			FileUtils.deleteDirectory(imageDirectory.toFile());
		}
		Files.createDirectory(imageDirectory);
		
		int count = 0;
		for (ImageEntry entry : images)
		{
			logger.log("copying " + entry.getRelativePath());
			
			Path currentPath = resolve(entry);
			String[] filename = splitExtension(entry.getImageFilename());
			String newFileName = getNewPath(imageDirectory, filename);
			Path newPath = imageDirectory.resolve(Paths.get(newFileName));
			
			Files.copy(currentPath, newPath);
			ImageEntry newEntry = new ImageEntry(rootName, newFileName);
			try
			{
				newEntry.updateCopiedInfo(entry, newPath);
				newImageSet.add(newEntry);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			logger.setProgress(++count, images.size());
		}
		
		logger.setProgress(1, 1);
		
		return newImageSet;
	}
	
	private static String getNewPath(Path newDirectory, String[] filename)
	{
		for (int i = 0; i < filename.length; i++)
			filename[i] = filename[i].replace(' ', '_');
		filename[0] = filename[0].replace('.', '_');
		
		String newFileName;
		StringBuilder nameBuilder = new StringBuilder();
		
		for (String s : filename)
			nameBuilder.append(s);
		newFileName = nameBuilder.toString();
		Path resolve = newDirectory.resolve(newFileName);
		if (!Files.exists(resolve))
			return newFileName;

		int index = 0;
		do
		{
			nameBuilder.setLength(0);
			nameBuilder.append(filename[0]).append(String.format("_copy_%04d", index++));

			switch (filename.length)
			{
			case 1:
				break;
			case 2:
				nameBuilder.append(filename[1]);
				break;
			default:
				throw new RuntimeException();
			}
			newFileName = nameBuilder.toString();
			
			resolve = newDirectory.resolve(newFileName);
		} while (Files.exists(resolve));
		
		return newFileName;
	}

	public void sort()
	{
		Collections.sort(images);
	}

	public Path resolve(ImageEntry entry)
	{
		return Paths.get(rootNames.get(entry.getRootName())).resolve(entry.getRelativePath());
	}

	public BufferedImage getImage(ImageEntry entry) throws IOException
	{
		try (InputStream input = Files.newInputStream(resolve(entry));)
		{
			return ImageIO.read(input);
		}
	}

	private static String[] splitExtension(String filename)
	{
		int lastIndexOf = filename.lastIndexOf('.');
		if (lastIndexOf < 0)
			return new String[] { filename };
		return new String[] {
			filename.substring(0, lastIndexOf),
			filename.substring(lastIndexOf, filename.length())};
	}

	private static final Random random = new Random(1776);
	private static final int rootNameLength = 10;
	private static final String rootNameCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public String getRootName(Path rootLocation)
	{
		String loc = rootLocation.toString();
		for (Entry<String, String> entry : rootNames.entrySet())
			if (entry.getValue().equals(loc))
				return entry.getKey();

		String newRootName;
		do
		{
			StringBuilder builder = new StringBuilder(rootNameLength);
			builder.setLength(0);
			for (int i = 0; i < rootNameLength; i++)
				builder.append(rootNameCharacters.charAt(random.nextInt(rootNameCharacters.length())));
			newRootName = builder.toString();
		} while (rootNames.containsKey(newRootName));
		rootNames.put(newRootName, loc);
		return newRootName;
	}

	public void verify(Logger logger)
	{
		LinkedList<ImageEntry> toRemove = new LinkedList<>();
		int count = 0; // make this be the total bytes again...
		for (ImageEntry entry : images)
		{
			Path fsFile = resolve(entry);
			logger.log("Verifying " + fsFile);
			
			if (!entry.verifyInfo(fsFile))
				toRemove.add(entry);
			
			logger.setProgress(++count, size());
		}

		// not very efficient...
		for (ImageEntry entry : toRemove)
			remove(entry);

		logger.setProgress(1, 1);
		
		
//		LinkedList<ImageEntry> toRemove = new LinkedList<>();
//		
//		BigDecimal count = BigDecimal.ZERO;
//		for (ImageEntry entry : entries.images)
//		{
//			logger.log("Reading image info " + entry.getImageFile());
//			logger.setProgress((int) (PROGRESS_TICKS * count.divide(imageSet.numberOfBytes, new MathContext(2 + (int) Math.log10(PROGRESS_TICKS))).doubleValue()),
//					PROGRESS_TICKS);
//			
//			try {
//				entry.readInfo();
//			} catch (Exception e) {
//				e.printStackTrace();
//				toRemove.add(entry);
//			}
//
//			count = count.add(new BigDecimal(entry.getFileSize()));
//		}
//
//		imageSet.images.removeAll(toRemove);
//		// TODO Auto-generated method stub
//		
	}

	public void setTheOnlyPath(Path imagesDirectory)
	{
		if (rootNames.size() != 1)
			throw new RuntimeException();
		
		Entry<String, String> next = rootNames.entrySet().iterator().next();
		rootNames.put(next.getKey(), imagesDirectory.toString());
	}
}
