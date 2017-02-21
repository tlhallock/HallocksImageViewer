package iv.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class Workspace
{
	ImageSet images;
	Preferences preferences;
	TreeSet<String> locationHistory;
	
	public Workspace()
	{
		images = new ImageSet();
		preferences = new Preferences();
		locationHistory = new TreeSet<>();
	}

	public Workspace(
			ImageSet images,
			Preferences preferences,
			TreeSet<String> locationHistory)
	{
		this.images = new ImageSet(images);
		this.preferences = new Preferences(preferences);
		this.locationHistory = new TreeSet<>(locationHistory);
		addAllLocations();
	}

	public Workspace(JsonObject object)
	{
		images = new ImageSet(object.getJsonObject("images"));
		preferences = new Preferences(object.getJsonObject("preferences"));
		locationHistory = new TreeSet<>();
		JsonArray jsonArray = object.getJsonArray("location history");
		if (jsonArray != null)
		{
			for (int i = 0; i < jsonArray.size(); i++)
				locationHistory.add(jsonArray.getString(i));
		}
	}

	void addAllLocations()
	{
		locationHistory.addAll(images.collectAllLocations());
	}

	public void write(JsonGenerator generator) throws IOException
	{
		generator.writeStartObject();

		generator.writeFieldName("preferences");
		preferences.write(generator);

		generator.writeFieldName("images");
		images.write(generator);

		generator.writeFieldName("location history");
		generator.writeStartArray();
		for (String string : locationHistory)
			generator.writeString(string);
		generator.writeEndArray();

		generator.writeEndObject();
	}

	public static Workspace parse(File selectedFile, Logger logger) throws IOException
	{
		try (JsonReader createParser = javax.json.Json.createReader(
				Files.newInputStream(Paths.get(selectedFile.toString())));)
		{
			return new Workspace(createParser.readObject());
		}
	}

	private static final JsonFactory JSON_FACTORY = new JsonFactory();
	public static void save(Workspace workspace, File selectedFile) throws IOException
	{
		try (com.fasterxml.jackson.core.JsonGenerator generator = JSON_FACTORY.createGenerator(
				Files.newOutputStream(Paths.get(selectedFile.toString())));)
		{
			generator.setPrettyPrinter(new DefaultPrettyPrinter());

			workspace.write(generator);
		}
	}

	public static void exportToPdf(Workspace workspace, File selectedFolder, Logger logger) throws IOException
	{
		Path path = Paths.get(selectedFolder.toString());
		Workspace exportedWorkspace = new Workspace(
				workspace.images.copyImagesToDirectory(path.resolve("images"), logger),
				workspace.preferences, workspace.locationHistory);

		save(exportedWorkspace, path.resolve("workspace.json").toFile());
		exportedWorkspace.images.writeLatex(path.resolve("workspace_").toString(), 60, logger);
	}
	
	public static Workspace importFromPdfFolder(File pdfFolder, Logger logger) throws IOException
	{
		Path folderPath = Paths.get(pdfFolder.toString());
		Path imagesDirectory = folderPath.resolve("images");
		if (!Files.exists(imagesDirectory))
			return null;
		Workspace returnValue = parse(folderPath.resolve("workspace.json").toFile(), logger);
		returnValue.images.setTheOnlyPath(imagesDirectory);
		return returnValue;
		
	}

	public void verifyImages(Logger logger)
	{
		images.verify(logger);
	}
}
