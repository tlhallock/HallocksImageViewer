package iv.main;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.json.JsonObject;

import org.apache.commons.imaging.ImageReadException;

import com.fasterxml.jackson.core.JsonGenerator;

public class ImageEntry implements Comparable<ImageEntry>
{
	private static final String UNKNOWN_TIME = "Unknown: this is either not a jpeg image or it has no exif data";
	private static final SimpleDateFormat format = createSimpleDateFormat();
	
	private String  rootName		;
	private String	relativePath		;
	private String	description		;
	private String	location		;
	private String	people			;
	
	private String	checksum		;
	private long	imageTakenTime	;
	private long	lastModifiedTime;
	private long	fileSize		;

	public ImageEntry(String rootName, String relativePath)
	{
		this.rootName = rootName;
		this.relativePath = relativePath;
		description = "Edit the description here";
		location = "Denver";
		checksum = "computing";
		people = "";
		fileSize = 0;
	}

	public ImageEntry(JsonObject jsonObject)
	{
		rootName		=           jsonObject.getString(         "rootName		".trim());		
		relativePath		=           jsonObject.getString(         "relativePath		".trim());		
		description		=           jsonObject.getString(         "description		".trim());		
		location		=           jsonObject.getString(         "location		".trim());
		checksum		=           jsonObject.getString(         "checksum		".trim());
		imageTakenTime		=           jsonObject.getJsonNumber(     "imageTakenTime	".trim()).longValue();	
		lastModifiedTime	=           jsonObject.getJsonNumber(     "lastModifiedTime	".trim()).longValue();
		fileSize		=           jsonObject.getJsonNumber(     "fileSize		".trim()).longValue();
		
		people			=		jsonObject.getString(	  "people");
	}
	
	public ImageEntry(ImageEntry oldFile)
	{
		rootName = oldFile.rootName;
		relativePath = oldFile.relativePath;
		description = oldFile.description;
		location = oldFile.location;
		checksum = oldFile.checksum;
		imageTakenTime = oldFile.imageTakenTime;
		lastModifiedTime = oldFile.lastModifiedTime;
		fileSize = oldFile.fileSize;
		people = oldFile.people;
	}

	public void updateCopiedInfo(
			ImageEntry oldFile,
			Path newPath) throws IOException
	{
		description = oldFile.description;
		location = oldFile.location;
		checksum = oldFile.checksum;
		imageTakenTime = oldFile.imageTakenTime;
		fileSize = oldFile.fileSize;
		people = oldFile.people;

		lastModifiedTime = ImageInfo.getLastModifiedTime(newPath);
	}

	public boolean verifyInfo(Path filePath)
	{
		if (!Files.exists(filePath))
			return false;
		
		fileSize = filePath.toFile().length();

		try
		{
			if (ImageInfo.getLastModifiedTime(filePath) <= lastModifiedTime)
				return true;
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return false;
		}

		try
		{
			readInfo(filePath);
		}
		catch (ImageReadException | NoSuchAlgorithmException | IOException | ParseException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void readInfo(Path path) throws IOException, ParseException, ImageReadException, NoSuchAlgorithmException
	{
		fileSize = path.toFile().length();
		lastModifiedTime = ImageInfo.getLastModifiedTime(path);
		checksum = ImageInfo.checksumFile(path);
		imageTakenTime = ImageInfo.getImageTakenTime(path);
		
	}

	@Override
	public int compareTo(ImageEntry arg0)
	{
		int cmp;
		cmp = rootName.compareTo(arg0.rootName);
		if (cmp != 0) return cmp;
		cmp = relativePath.compareTo(arg0.relativePath);
		if (cmp != 0) return cmp;
		return 0;
	}
	
	public String getImageTime()
	{
		if (imageTakenTime == 0)
			return "Not computed.";
		if (imageTakenTime < 0)
			return UNKNOWN_TIME;
					
		return format.format(new Date(imageTakenTime)).toString();
	}

	public String getChecksum()
	{
		return checksum;
	}

	public String getDescription()
	{
		return description;
	}

	public String getLocation()
	{
		return location;
	}

	public String getRootName()
	{
		return rootName;
	}

	public String getRelativePath()
	{
		return relativePath;
	}
	
	public String getPeople()
	{
		return people;
	}

	public void setPeople(String people2)
	{
		this.people = people2;
	}
	
//	public Path getOriginalPath()
//	{
//		return set.resolve(rootName).resolve(relativePath);
//	}

//	public BufferedImage getImage(Path path) throws IOException
//	{
//		try (InputStream input = Files.newInputStream(getOriginalPath());)
//		{
//			return ImageIO.read(input);
//		}
//	}

	public String getImageFilename()
	{
		Path originalPath = Paths.get(relativePath);
		return originalPath.getName(originalPath.getNameCount()-1).toString();
	}

	public String getLastModifed()
	{
		return new Date(lastModifiedTime).toString();
	}

	public long getFileSize()
	{
		return fileSize;
	}

	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();
		
		generator.writeStringField("rootName    		".trim(), rootName              );
		generator.writeStringField("relativePath		".trim(), relativePath          );
		generator.writeStringField("description		    	".trim(), description		);		
		generator.writeStringField("location			".trim(), location		);
		generator.writeStringField("checksum			".trim(), checksum		);
		generator.writeNumberField("imageTakenTime		".trim(), imageTakenTime	);	
		generator.writeNumberField("lastModifiedTime		".trim(), lastModifiedTime	);
		generator.writeNumberField("fileSize			".trim(), fileSize		);
		
		generator.writeStringField("people", people);

		generator.writeEndObject();
	}

	public void setLocation(String text) {
		location = text;
	}

	public void setDescription(String text) {
		description = text;
	}

	public void writeLatex(PrintStream texStream, Path path)
	{
		String imageTime = getImageTime();
		texStream.print("\\section{}"                                                             + "\n");
		try {
			if(ImageInfo.isVertical(path)){
				texStream.print("\\begin{figure}[h]\n");
				texStream.print("\\includegraphics[width=0.5\\textwidth]{images/" +    relativePath  + "}"   + "\n");
				texStream.print("\\centering\n");
				texStream.print("\\end{figure}\n");
			}else{
				texStream.print("\\includegraphics[width=\\textwidth]{images/" +    relativePath  + "}"   + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		texStream.print("\n"                                                                      + "\n");
		texStream.print("\\textbf{Image}: " + le(getImageFilename())                              + "\n");
		texStream.print("\n"                                                                      + "\n");
		texStream.print("\\textbf{Location}: " + le(getLocation())                                + "\n");
		texStream.print("\n"                                                                      + "\n");
		texStream.print("\\textbf{Description}: " + le(getDescription())                          + "\n");
		texStream.print("\n"                                                                      + "\n");
		if (!imageTime.equals(UNKNOWN_TIME)) {
		texStream.print("\\textbf{Time}: " + le(imageTime)                                        + "\n");
		texStream.print("\n"                                                                      + "\n"); }
		texStream.print("\\textbf{People}: " + le(getPeople())                                    + "\n");
		texStream.print("\n"                                                                      + "\n");
		texStream.print("\\newpage"                                                               + "\n");
	}
	
	private static final String badChars = "$\\_&";
	private static String le(String string)
	{
		StringBuilder builder = new StringBuilder(string.length());
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			if (badChars.indexOf(c) >= 0)
				builder.append('\\');
//			if (c == '.')
//				if (i > 0 && string.charAt(i-1) >= '0' && string.charAt(i-1) <= '9')
//					builder.append('\\');
			builder.append(c);
		}
		return builder.toString();
	}

	public long getComparable() {
		return imageTakenTime;
	}
	
	
	
	

	private static SimpleDateFormat createSimpleDateFormat()
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a 'GMT'");
		f.setTimeZone(TimeZone.getTimeZone("GMT"));
		return f;
	}
}
