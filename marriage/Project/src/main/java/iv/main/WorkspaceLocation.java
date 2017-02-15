//package iv.main;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import javax.json.JsonObject;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//
//public class WorkspaceLocation
//{
//	Path path;
//	boolean recurse;
//
//	public WorkspaceLocation(String text, boolean selected)
//	{
//		path = Paths.get(text);
//		recurse = selected;
//	}
//
//	public WorkspaceLocation(JsonObject jsonObject)
//	{
//		path = Paths.get(jsonObject.getString("path"));
//		recurse = jsonObject.getBoolean("recurse");
//	}
//
//	public void write(JsonGenerator generator) throws IOException
//	{
//		generator.writeStartObject();
//		generator.writeStringField("path", path.toString());
//		generator.writeBooleanField("recurse", recurse);
//		generator.writeEndObject();
//	}
//}
