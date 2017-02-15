package org.hangout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class SimpleTranslator
{
	private static final JsonFactory factory = new JsonFactory();
	private static final boolean INCLUDE_MESSAGES = false;
	
	private static final String[] relavent = new String[]{ "zeng", "sunny"};
	
	public static void main(String[] args) throws IOException
	{
		String inputFile   = "C:\\cygwin64\\home\\trever\\Documents\\Hangouts.json";
		String outputFile1 = "C:\\cygwin64\\home\\trever\\Documents\\output\\simplified_hangouts.json";
		String outputFile2 = "C:\\cygwin64\\home\\trever\\Documents\\output\\human_readable.txt";
		String outputFile3 = "C:\\cygwin64\\home\\trever\\Documents\\output\\censored.json";
		String outputFile4 = "C:\\cygwin64\\home\\trever\\Documents\\output\\byDay.txt";
		
		System.out.println("Starting");
		
		copyEverythingButText(inputFile, outputFile3);
		
		System.out.println("Done copying");
		
		JsonObject object;
		
		try (JsonReader parser = javax.json.Json.createReader(Files.newInputStream(Paths.get(inputFile)));)
		{
			object = (JsonObject) parser.read();
		}
		
		System.out.println("Done reading");
		
		Hangout hangout = new Hangout();
		
		parseInto(object, hangout);
		
		countMessagesPerDay(hangout, outputFile4);
		
		System.out.println("Done counting");
		
		try (JsonGenerator generator = createGenerator(Files.newOutputStream(Paths.get(outputFile1)));)
		{
			hangout.write(generator);
		}
		
		System.out.println("Done writing json");
		
		try (PrintStream printStream = new PrintStream(outputFile2);)
		{
			hangout.prettyPrintRelavent(printStream);
		}
		
		System.out.println("Done writing human readable");
	}
	
	private static void countMessagesPerDay(Hangout object, String outputFile4)
	{
		TreeMap<String, Integer> counts = new TreeMap<>();
		object.count(counts);
		
		try (PrintStream output = new PrintStream(outputFile4))
		{
			for (Entry<String, Integer> entry : counts.entrySet())
				output.println("# of messages on day " + entry.getKey() + " : " + entry.getValue());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void copyEverythingButText(String inputFile, String outputFile)
	{
		try (JsonGenerator generator = createGenerator(Files.newOutputStream(Paths.get(outputFile)));
			JsonParser parser = createParser(Files.newInputStream(Paths.get(inputFile)));)
		{
			JsonCensorer censorer = new JsonCensorer(parser, generator);
			if (!INCLUDE_MESSAGES)
			{
				censorer.censor("text", "<censored>");
				censorer.censor("attachment", "<censored>");
			}
			censorer.copy();
		}
		catch (JsonParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	// The parsing code is taken from https://github.com/Jessecar96/hangouts-reader/blob/gh-pages/index.html
	
	private static void parseInto(JsonObject object, Hangout hangout)
	{
		HashMap<String, String> fallbackNames = new HashMap<>();
		
		// make one pass to get all the fallback names
		JsonArray jsonArray = object.getJsonArray("conversation_state");
		for (int i = 0; i < jsonArray.size(); i++)
		{
			JsonObject jsonObject = jsonArray.getJsonObject(i).getJsonObject("conversation_state");
			if (!jsonObject.containsKey("conversation"))
				continue;
			JsonObject conversation = jsonObject.getJsonObject("conversation");
			
			JsonArray jsonArray2 = conversation.getJsonArray("participant_data");
			for (int j = 0; j < jsonArray2.size(); j++)
			{
				JsonObject person = jsonArray2.getJsonObject(j);
				String gaia_id = person.getJsonObject("id").getString("gaia_id");
				if (!person.containsKey("fallback_name"))
					continue;
				String fallbackName = person.getJsonString("fallback_name").getString();
				fallbackNames.put(gaia_id, fallbackName);
			}
		}

		for (int i = 0; i < jsonArray.size(); i++)
		{
			JsonObject conversationState = jsonArray.getJsonObject(i).getJsonObject("conversation_state");
			String conversationId = conversationState.getJsonObject("conversation_id").getJsonString("id").getString();
			Conversation conversationObj = hangout.conversations.get(conversationId);
			if (conversationObj == null)
			{
				conversationObj = new Conversation(conversationId);
				hangout.conversations.put(conversationId, conversationObj);
			}

			if (!conversationState.containsKey("conversation"))
				continue;
			JsonObject conversation = conversationState.getJsonObject("conversation");

			JsonArray jsonArray2 = conversation.getJsonArray("participant_data");
			for (int j = 0; j < jsonArray2.size(); j++)
			{
				JsonObject person = jsonArray2.getJsonObject(j);

				String gaia_id = person.getJsonObject("id").getString("gaia_id");

				String personname = null;
				if (personname == null && person.containsKey("fallback_name"))
					personname = person.getJsonString("fallback_name").getString();
				if (personname == null && fallbackNames.containsKey(gaia_id))
					personname = fallbackNames.get(gaia_id);
				if (personname == null)
					personname = "unknown";

				conversationObj.people.put(gaia_id, personname);
			}
			
			// parse all the events...
			if (!conversationState.containsKey("event"))
				continue;
			
			JsonArray events = conversationState.getJsonArray("event");
			for (int j = 0; j < events.size(); j++)
			{
				JsonObject event = events.getJsonObject(j);
				if (!event.containsKey("chat_message"))
					continue;
				if (!event.containsKey("chat_message"))
					continue;
				
				Event newEvent = new Event();
				newEvent.timestamp = new BigDecimal(clip(event.getJsonString("timestamp").getString()));
				newEvent.gaia_id = event.getJsonObject("sender_id").getString("gaia_id");
				newEvent.sender = conversationObj.people.get(newEvent.gaia_id);
				if (event.containsKey("event_id"))
					newEvent.eventId = event.getJsonString("event_id").getString();

				JsonObject messageContent = event.getJsonObject("chat_message").getJsonObject("message_content");
				if (messageContent.containsKey("segment"))
				{
					JsonArray segments = messageContent.getJsonArray("segment");
					for (int k = 0; k < segments.size(); k++)
					{
						JsonObject segment = segments.getJsonObject(k);
						if (!segment.containsKey("text"))
							continue;
						newEvent.message.append(segment.getString("text"));
					}
				}
				
				if (messageContent.containsKey("attachment"))
				{
					JsonArray attachments = messageContent.getJsonArray("attachment");
					for (int k = 0; k < attachments.size(); k++)
					{
						JsonObject attachment = attachments.getJsonObject(k);
						newEvent.attachments.add(attachment.toString());
//						if (attachment.getJsonObject("embed_item").getJsonArray("type").getString(0)
//								.equals("PLUS_PHOTO"))
//						{
////							message += "\n<a target='blank' href='" + attachment['embed_item']['embeds.PlusPhoto.plus_photo']['url'] + "'><img class='thumb' src='" + attachment['embed_item']['embeds.PlusPhoto.plus_photo']['thumbnail']['image_url'] + "' /></a>";
//						}
					}
				}
				conversationObj.events.add(newEvent);
			}
		}
	}
	
	
	
	
	
	
	
	
	private static class Hangout
	{
		HashMap<String, Conversation> conversations = new HashMap<>();
		
		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			
			generator.writeFieldName("conversations");
			generator.writeStartArray();
			for (Conversation conversation : getSortedConversations())
				conversation.write(generator);
			generator.writeEndArray();
			
			generator.writeEndObject();
		}
		
		public void count(TreeMap<String, Integer> counts)
		{
			for (Conversation conversation : getSortedConversations())
				conversation.count(counts);
		}

		public void prettyPrintRelavent(PrintStream output)
		{
			for (Conversation conversation : getSortedConversations())
				conversation.prettyPrintRelavent(output);
		}
		
		LinkedList<Conversation> getSortedConversations()
		{
			Collection<Conversation> valuesOriginal = conversations.values();
			LinkedList<Conversation> sorted = new LinkedList<>(valuesOriginal);
			for (Conversation s : sorted)
				Collections.sort(s.events);
			Collections.sort(sorted);
			return sorted;
		}
	}

	private static class Conversation implements Comparable<Conversation>
	{
		final String conversationId;
		HashMap<String, String> people = new HashMap<>();
		LinkedList<Event> events = new LinkedList<>();

		public Conversation(String id)
		{
			this.conversationId = id;
		}
		
		public void count(TreeMap<String, Integer> counts)
		{
			if (!isRelavent())
				return;
			
			for (Event event : events)
				event.count(counts);
		}

		private boolean isRelavent()
		{
			for (String person : people.values())
				for (String r : relavent)
					if (person.toLowerCase().contains(r))
						return true;

			return false;
		}
		public void prettyPrintRelavent(PrintStream output)
		{
			if (!isRelavent())
				return;
			
			output.print("Conversation between ");
			for (String value : people.values())
				output.print(rightPad(value, 32));
			output.println();
			output.println("\tConversation Id: " + conversationId);
			output.println("\tNumber of events: " + events.size());
			output.println("\tEvents:");
			for (Event event : events)
			{
				event.prettyPrintRelavent(output);
			}
		}

		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			generator.writeStringField("id", conversationId);
			
			generator.writeFieldName("people");
			generator.writeStartObject();
			for (Entry<String, String> e : people.entrySet())
				generator.writeStringField(e.getKey(), e.getValue());
			generator.writeEndObject();
			
			generator.writeFieldName("events");
			generator.writeStartArray();
			for (Event event : events)
				event.write(generator);
			generator.writeEndArray();

			
			generator.writeEndObject();
		}

		@Override
		public int compareTo(Conversation arg0)
		{
			if (events.isEmpty())
			{
				if (arg0.events.isEmpty())
					return 0;
				return -1;
			}
			else if (arg0.events.isEmpty())
				return 1;
			
			return events.getFirst().compareTo(arg0.events.getFirst());
		}
	}
	
	static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd");
	static final SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM/yyyy 'at' hh:mm:ss a zzz");
	private static class Event implements Comparable<Event>
	{
		String eventId;
		BigDecimal timestamp;
		StringBuilder message = new StringBuilder();
		String gaia_id;
		String sender;
		LinkedList<String> attachments = new LinkedList<>();
		
		@Override
		public int compareTo(Event arg0)
		{
			return timestamp.compareTo(arg0.timestamp);
		}
		
		public void count(TreeMap<String, Integer> counts)
		{
			String day = dayFormat.format(getEpochTime());
			
			Integer integer = counts.get(day);
			if (integer == null)
				counts.put(day, 1);
			else
				counts.put(day, integer + 1);
		}

		private long getEpochTime()
		{
			String longDate = timestamp.toPlainString();
			longDate = longDate.substring(0, longDate.length() - 3);
			long epochTime = Long.valueOf(longDate);
			return epochTime;
		}
		
		public void prettyPrintRelavent(PrintStream output)
		{
			output.print(
				"\t\t[" + eventId + "] " 
				+ "At time " + timestamp.toPlainString() 
				+ " (" + format.format(new Date(getEpochTime())) + ") " 
				+ rightPad(sender, 32) + " sends ");
			
			if (!attachments.isEmpty())
			{
				output.print("an attachment");
			}
			else
			{
				output.print("a message with " + rightPad(String.valueOf(message.length()), 5) + " characters.");
			}
			output.println();
			
			if (INCLUDE_MESSAGES)
				output.println("\t\t\tThe message was: \"" + message + '"');
		}
		
		public void write(JsonGenerator generator) throws IOException
		{
			generator.writeStartObject();
			generator.writeNumberField("time", timestamp);
			generator.writeStringField("sender", sender);
			generator.writeStringField("sender_id", gaia_id);

			if (INCLUDE_MESSAGES)
			{
				generator.writeStringField("message", message.toString());

				generator.writeFieldName("attachments");
				generator.writeStartArray();
				for (String string : attachments)
					generator.writeString(string);
				generator.writeEndArray();
			}
			else
			{
				generator.writeNumberField("message length", message.length());
				generator.writeNumberField("number of attachments", attachments.size());
			}
			
			if (eventId == null)
				generator.writeNullField("eventId");
			else
				generator.writeStringField("eventId", eventId);
			
			generator.writeEndObject();
		}
	}
	
	
	
	
	
	
	
	
	private static String rightPad(String string, int size)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(string);
		while (builder.length() < size)
			builder.append(' ');
		return builder.toString();
	}
	
	
	
	private static String clip(String other)
	{
		try
		{
			int startIndex = 0;
			while (startIndex < other.length() && other.charAt(startIndex) == '"')
				startIndex++;
			if (startIndex == other.length())
				return "";

			int endIndex = other.length();
			while (other.charAt(endIndex - 1) == '"')
				endIndex--;
			return other.substring(startIndex, endIndex);
		}
		catch (Exception e)
		{
			System.out.println("Error on " + other);
			System.exit(0);
			throw new RuntimeException();
		}
	}
	
	
	
	
	
	
	static JsonParser createParser(InputStream input) throws JsonParseException, IOException
	{
		return factory.createParser(input);
	}
	static JsonGenerator createGenerator(OutputStream output) throws IOException
	{
		JsonGenerator createGenerator = factory.createGenerator(output);
		createGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		return createGenerator;
	}
	
	
	
	private static class JsonCensorer
	{
		JsonParser parser;
		JsonGenerator generator;

		HashMap<String, String> censored = new HashMap<>();

		public JsonCensorer(JsonParser parser, JsonGenerator generator)
		{
			this.parser = parser;
			this.generator = generator;
		}

		public void censor(String fieldName, String newValue)
		{
			censored.put(fieldName, newValue);
		}

		public void copy() throws IOException
		{
			JsonToken token;
			while ((token = parser.nextToken()) != null)
			{
				switch (token)
				{
				case FIELD_NAME:
					generator.writeFieldName(parser.getCurrentName());
					censorIfNecessary(parser.getCurrentName());
					break;
				case START_ARRAY:
					generator.writeStartArray();
					break;
				case START_OBJECT:
					generator.writeStartObject();
					break;
				case END_ARRAY:
					generator.writeEndArray();
					break;
				case END_OBJECT:
					generator.writeEndObject();
					break;
				case VALUE_FALSE:
					generator.writeBoolean(false);
					break;
				case VALUE_TRUE:
					generator.writeBoolean(true);
					break;
				case VALUE_NULL:
					generator.writeNull();
					break;
				case VALUE_NUMBER_FLOAT:
				case VALUE_NUMBER_INT:
					generator.writeNumber(parser.getDecimalValue());
					break;
				case VALUE_STRING:
					generator.writeString(parser.getValueAsString());
					break;

				// read errors
				case NOT_AVAILABLE:
				case VALUE_EMBEDDED_OBJECT:
				default:
					throw new RuntimeException();
				}
			}
		}

		private void censorIfNecessary(String fieldName) throws IOException
		{
			String replacementValue = censored.get(fieldName);
			if (replacementValue == null)
				return;
			generator.writeString(replacementValue);
			skipNextValue();
		}

		private void skipNextValue() throws IOException
		{
			switch (parser.nextToken())
			{
			case START_ARRAY:
				skipArray(parser);
				break;
			case START_OBJECT:
				skipObject(parser);
				break;
			case VALUE_FALSE:
			case VALUE_TRUE:
			case VALUE_NULL:
			case VALUE_NUMBER_FLOAT:
			case VALUE_NUMBER_INT:
			case VALUE_STRING:
				break;
			case FIELD_NAME:
			case NOT_AVAILABLE:
			case VALUE_EMBEDDED_OBJECT:
			case END_ARRAY:
			case END_OBJECT:
			default:
				throw new RuntimeException();
			}
		}

		private static void skipObject(JsonParser parser) throws IOException
		{
			int depth = 0;
			while (depth >= 0)
			{
				switch (parser.nextToken())
				{
				case END_OBJECT:
					depth--;
					break;
				case START_OBJECT:
					depth++;
					break;
				default:
				}
			}
		}
		
		private static void skipArray(JsonParser parser) throws IOException
		{
			int depth = 0;
			while (depth >= 0)
			{
				switch (parser.nextToken())
				{
				case END_ARRAY:
					depth--;
					break;
				case START_ARRAY:
					depth++;
					break;
				default:
				}
			}
		}
	}
}
