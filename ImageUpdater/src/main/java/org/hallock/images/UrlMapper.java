package org.hallock.images;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

public class UrlMapper
{
	private static final String DELIM = ":";
	
	private HashMap<String, String> path2Url = new HashMap<>();
	
	public UrlMapper() {}
	public UrlMapper(String location) throws IOException
	{
		load(location);
	}
        
        public String get(String path)
        {
            return path2Url.get(path);
        }
        
        public void map(String path, String url)
        {
            path2Url.put(path, url);
        }
	
	public void load(String location) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(location)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] split = line.split(DELIM);
				if (split == null || split.length != 2)
					continue;
				String path = split[0].trim();
				String url = split[1].trim();
				if (path.length() == 0)
				{
					continue;
				}
				
				path2Url.put(path, url);
			}
		}
	}
	
	public void save(String location) throws IOException
	{
		try (BufferedWriter newBufferedWriter = Files.newBufferedWriter(Paths.get(location));)
		{
			for (Entry<String, String> entry : path2Url.entrySet())
			{
				newBufferedWriter.write(entry.getKey() + DELIM + entry.getValue() + '\n');
			}
		}
	}
        
        public void save() throws IOException
        {
            save(Registry.getRegistry().getSettings().getUrlsLocation());
        }

    void print(PrintStream out) {
        for (Entry<String, String> entry : path2Url.entrySet())
        {
            out.println("        Alias /" + entry.getValue() + " " + entry.getKey());
            out.println("       ");
            out.println("        <Directory \"" + entry.getKey() + "\">");
            out.println("                Order Allow,Deny");
            out.println("                Allow From All");
            out.println("                Require all granted");
            out.println("                Options +Indexes");
            out.println("        </Directory>");
        }
    }
}
