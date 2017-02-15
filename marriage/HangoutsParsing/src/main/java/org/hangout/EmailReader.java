package org.hangout;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class EmailReader
{
	// first the mbox is converted to json with the code on 
	// https://github.com/ptwobrussell/Mining-the-Social-Web/blob/master/python_code/mailboxes__jsonify_mbox.py
	
	private static HashMap<String, Integer> monthToIndex = new HashMap<>();
	
	public static boolean PRINT_MESSAGE = false;
	public static boolean PRINT_SUBJECT = false;
	public static boolean PRINT_SORTABL = false;
	public static boolean PRINT_LABEL   = false;

	public static void main(String[] args) throws JsonParseException, IOException
	{
		int ndx = 0;
		
		monthToIndex.put("Jan", ++ndx);
		monthToIndex.put("Feb", ++ndx);
		monthToIndex.put("Mar", ++ndx);
		monthToIndex.put("Apr", ++ndx);
		monthToIndex.put("May", ++ndx);
		monthToIndex.put("Jun", ++ndx);
		monthToIndex.put("Jul", ++ndx);
		monthToIndex.put("Aug", ++ndx);
		monthToIndex.put("Sep", ++ndx);
		monthToIndex.put("Oct", ++ndx);
		monthToIndex.put("Nov", ++ndx);
		monthToIndex.put("Dec", ++ndx);

		LinkedList<EmailThing> list = new LinkedList<>();
		
		
		try (JsonParser parser = SimpleTranslator.createParser(Files.newInputStream(Paths.get("C:\\cygwin64\\home\\trever\\Documents\\mail.json")));)
		{
			JsonToken token;
			
			token = parser.nextToken();
			if (!token.equals(JsonToken.START_ARRAY))
				throw new RuntimeException();
			
			EmailThing thing = new EmailThing();
			StringBuilder builder = new StringBuilder();

			int depth = 0;
			String currentKey = null;
			while (!(token = parser.nextToken()).equals(JsonToken.END_ARRAY) || depth != 0)
			{
				switch (token)
				{
				case START_ARRAY:
				case START_OBJECT:
					depth++;
					break;
					
				case END_OBJECT:
					depth--;
					
					if (depth != 0)
						break;
					
					if (thing.isSunny() && !thing.isChat())
						list.add(thing);
					
					thing = new EmailThing();

					break;
				case END_ARRAY:
					depth--;
					break;
				case FIELD_NAME:
					currentKey = parser.getCurrentName();
					break;
				case VALUE_FALSE:
				case VALUE_NULL:
				case VALUE_NUMBER_FLOAT:
				case VALUE_NUMBER_INT:
				case VALUE_STRING:
					switch (currentKey)
					{
					case "To":
						if (depth != 2)
							break;
						thing.receivers.add(parser.getValueAsString());
						break;
					case "From":
						if (depth != 1)
							break;
						thing.currentSender = parser.getValueAsString();
						break;
					case "Date":
						if (depth != 1)
							break;
						thing.currentDate = parser.getValueAsString();
						break;
					case "Subject":
						if (depth != 1)
							break;
						thing.currentSubject = parser.getValueAsString();
						break;
					case "X-Gmail-Labels":
						if (depth != 1)
							break;
						thing.currentLabels = parser.getValueAsString();
						break;
					case "content":
						if (depth != 3)
							break;
						builder.setLength(0);
						builder.append(parser.getValueAsString());
						break;
					case "contentType":
						if (parser.getValueAsString().contains("text/plain"))
							thing.message.append(builder);
						break;
					default:
					}
				case VALUE_TRUE:
					break;
				case NOT_AVAILABLE:
				case VALUE_EMBEDDED_OBJECT:
				}
			}
		}
		
		for (EmailThing thing : list)
			thing.parseDate();
		
		Collections.sort(list);
		
		try (PrintStream output = new PrintStream("C:\\cygwin64\\home\\trever\\Documents\\mail.txt");)
		{
			for (EmailThing thing : list)
			{
				thing.print(output);
			}
		}
	}
	
	
//	static final Pattern DATE = Pattern.compile("(Sun|Mon|Tue|Wed|Thu|Fri|Sat), ([0-9][0-9]?) (...) (20[0-9][0-9]) [0-9][0-9]:[0-9][0-9]:[0-9][0-9] (.*)"); 
	static final Pattern DATE = Pattern.compile("([0-9][0-9]?) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) (20[0-9][0-9]) ([0-9][0-9]:[0-9][0-9]:[0-9][0-9]) (.*)");
	private static class EmailThing implements Comparable<EmailThing>
	{
		HashSet<String> receivers = new HashSet<>();
		String currentSender = null;
		String currentDate = null;
		String currentSubject = null;
		String currentLabels = null;
		String sortableDate = null;
		StringBuilder message = new StringBuilder();
		
		boolean isSunny()
		{
			for (String str : receivers)
				if (stringIsSunny(str))
					return true;
			return currentSender != null && stringIsSunny(currentSender);
		}
		
		boolean isChat()
		{
			return currentLabels.contains("Chat");
		}
		
		void parseDate()
		{
			Matcher matcher = DATE.matcher(currentDate);
			if (!matcher.find())
				throw new RuntimeException(currentDate);
			String day	= matcher.group(1);
			String month	= matcher.group(2);
			String year	= matcher.group(3);
			String time	= matcher.group(4);
			String offset	= matcher.group(5);
			
			sortableDate = 
				year + " " + 
				String.format("%02d ", monthToIndex.get(month)) + 
				String.format("%02d ", Integer.valueOf(day)) +  
				time + " " + 
				offset;
		}
		
		public void print(PrintStream output)
		{
			output.println("Email sender        :  "  + currentSender	      );
			output.print  ("Email receivers	    : ");
			for (String receiver : receivers)
				output.print(receiver + "\t");
			output.println();
			output.println("Email date          :  "  + currentDate		      );
			if (PRINT_SUBJECT)
			output.println("Email subject       :  "  + currentSubject	      );
			if (PRINT_LABEL)
			output.println("Email labels        :  "  + currentLabels	      );
			if (PRINT_SORTABL)
			output.println("Email sortable date :  "  + sortableDate	      );
			if (PRINT_MESSAGE)
			output.println("Email message       : \"" + message		+ "\"");
			output.println("=====================================================");
		}

		@Override
		public int compareTo(EmailThing arg0)
		{
			return sortableDate.compareTo(arg0.sortableDate);
		}
	}

	private static boolean stringIsSunny(String str)
	{
		return str.contains("zycloudy@gmail.com") || str.contains("zyyuyu945@hotmail.com") || str.contains("zeng.yu@uwlax.edu");
	}
}
