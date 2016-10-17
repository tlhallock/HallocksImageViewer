package org.hallock.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Registry
{
        
	private Settings settings;

        
        
        private long settingsLoadTime;
        private String settingsLocation;
	public Settings getSettings()
	{
            try {
                long lastWriteTime = Files.getLastModifiedTime(Paths.get(settingsLocation)).toMillis();
                if (settingsLoadTime < lastWriteTime)
                {
                    settings = new Settings(settingsLocation);
                    settingsLoadTime = lastWriteTime;
                }
            } catch (IOException ex) {
                Logger.getLogger(Registry.class.getName()).log(Level.SEVERE, null, ex);
            }
            return settings;
	}
        
        public UrlMapper getUrlMapper() throws IOException
        {
            String location = settings.getUrlsLocation();
            try {
                return new UrlMapper(location);
            } catch (IOException ex) {
                UrlMapper mapper = new UrlMapper();
                mapper.save(location);
                return mapper;
            }
        }
	
	// Singleton...
	private Registry() {};
	private static Registry registry;
	public static Registry getRegistry()
	{
		return registry;
	}
	
	
	public synchronized static void initialize(InitializationArgs args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		registry = new Registry();
		try
		{
                        registry.settingsLocation = args.getSettingsFile();
			registry.settings = new Settings(registry.settingsLocation);
                        registry.settingsLoadTime = Files.getLastModifiedTime(Paths.get(registry.settingsLocation)).toMillis();
		}
		catch (IOException e)
		{
			System.out.println("Creating settings file at " + args.getSettingsFile());
                        
                        File parent = Paths.get(args.getSettingsFile()).getParent().toFile();
                        parent.mkdirs();
                        
			// e.printStackTrace();
			registry.settings = new Settings();
			registry.settings.fillDefaults();
			registry.settings.save(args.getSettingsFile());
		}
                
//                registry.getUrlMapper();
	}
}
