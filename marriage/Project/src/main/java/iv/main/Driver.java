package iv.main;

import java.io.File;
import java.io.IOException;

public class Driver {
	public static void main(String[] args) throws IOException
	{
		MainApplicationFrame mainApplicationFrame = new MainApplicationFrame();
		mainApplicationFrame.setVisible(true);
		
//		Workspace parse = Workspace.importFromPdfFolder(new File("/home/thallock/Documents/marriage/imagetest/exported"), Logger.EMPTY_LOGGER);
		
		String currentWorkspace = "C:\\cygwin64\\home\\zy\\export\\export.2\\workspace.json"; 
		Workspace parse = Workspace.parse(new File(currentWorkspace), Logger.EMPTY_LOGGER);
		mainApplicationFrame.setWorkspace(parse, currentWorkspace);
		
		
//		mainApplicationFrame.exportToPdf(new File("/home/thallock/test"));
		
		
//		System.exit(0);
	}
}
