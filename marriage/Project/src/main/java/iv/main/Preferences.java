package iv.main;

import java.io.IOException;
import java.util.Arrays;

import javax.json.JsonArray;
import javax.json.JsonObject;

import com.fasterxml.jackson.core.JsonGenerator;

public class Preferences
{
	String[] imageExtensions;
	String renamePattern;
	boolean ignoreHidden;
	
	public Preferences() {
		renamePattern = "";
//		setExtensions(".PNG, .png, .jpg, .JPG, .jpeg, .JPEG");
		setExtensions(".jpg, .JPG, .jpeg, .JPEG");
	}


	public Preferences(JsonObject jsonObject) {
		JsonArray jsonArray = jsonObject.getJsonArray("extensions");
		imageExtensions = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			imageExtensions[i] = jsonArray.getString(i);
		}

		renamePattern = jsonObject.getString("rename pattern");
		ignoreHidden = jsonObject.getBoolean("ignore hidden");
	}

	public Preferences(Preferences preferences)
	{
		imageExtensions = Arrays.copyOf(preferences.imageExtensions, preferences.imageExtensions.length);
		renamePattern = preferences.renamePattern;
		ignoreHidden = preferences.ignoreHidden;
	}


	public void setExtensions(String extensions) {
		String[] split = extensions.split(",");
		imageExtensions = new String[split.length];
		for (int i = 0; i < split.length; i++)
			imageExtensions[i] = split[i].trim();
	}

	public String[] getImageExtensions() {
		return imageExtensions;
	}

	public void write(JsonGenerator generator) throws IOException {
		generator.writeStartObject();
		generator.writeStringField("rename pattern", renamePattern);
		generator.writeBooleanField("ignore hidden", ignoreHidden);
		
		generator.writeFieldName("extensions");
		generator.writeStartArray();
		for (String str : imageExtensions)
			generator.writeString(str);
		generator.writeEndArray();
		
		generator.writeEndObject();
	}
}
