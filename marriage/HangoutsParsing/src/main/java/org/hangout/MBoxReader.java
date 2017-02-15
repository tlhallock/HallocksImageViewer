package org.hangout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MBoxReader
{
	private static enum State
	{
		WaitingForEmail,
		HadPart,
		HadContentType,
		ReadingMessage,
	}

	public static void main(String[] args) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(Paths.get("C:\\cygwin64\\home\\trever\\Documents\\mail.mbox"), StandardCharsets.UTF_8);
			PrintStream output = new PrintStream("C:\\cygwin64\\home\\trever\\Documents\\output\\emails.txt"))
		{
			State state = State.WaitingForEmail;
			
			LinkedList<String> context = new LinkedList<>();
			
			String currentSender = null;
			String currentReceiver = null;
			String date = null;
			boolean fromSunny = false;
//			StringBuilder message = new StringBuilder();
			
			int lineNumber = 0;
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (++lineNumber % 1000000 == 0)
					System.out.println("At line " + lineNumber);
				
				context.addLast(line);
				while (context.size() > 50)
				{
					context.removeFirst();
				}
				
				switch (state)
				{
				case WaitingForEmail:
					if (line.startsWith("From: "))
					{
						currentSender = line.substring("From: ".length());
						fromSunny = line.contains("zycloudy");
						if (fromSunny)
							printContext(lineNumber, context);
					}
					else if (line.startsWith("To: "))
						currentReceiver = line.substring("To: ".length());
					else if (line.startsWith("Date: "))
						date = line.substring("Date: ".length());
					else if (line.startsWith("------=_Part_"))
						state = State.HadPart;
					else if (line.contains("X-Gmail-Labels: Chat"))
					{
						currentSender = null;
						date = null;
						currentReceiver = null;
						fromSunny = false;
						state = State.WaitingForEmail;
					}
					break;
				case HadPart:
					if (line.startsWith("Content-Type: text/plain;"))
						state = State.HadContentType;
					else
						state = State.WaitingForEmail;
					break;
				case HadContentType:
					if (!fromSunny || !line.startsWith("Content-Transfer-Encoding:"))
					{
						state = State.WaitingForEmail;
						break;
					}
					state = State.ReadingMessage;
					output.println("Sender: " + currentSender);
					output.println("Date: " + date);
					output.println("Receiver: " + currentReceiver);
					output.println("Message: ");
					break;
				case ReadingMessage:
					if (fromSunny)
					{
						System.out.println("In message");
					}
					
					if (!line.startsWith("------=_Part_"))
					{
						if (fromSunny)
							output.println("\t \"" + line + "\"");
						break;
					}
					if (fromSunny)
						output.println("===================================================================");
					currentSender = null;
					date = null;
					currentReceiver = null;
					fromSunny = false;
					state = State.HadPart;
					break;

				default:
					throw new RuntimeException();
				}
			}
		}
	}
	
	
	private static void printContext(int lineNumber, LinkedList<String> context)
	{
		System.out.println("At line " + lineNumber);
		for (String s : context)
		{
			System.out.println(s);
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
