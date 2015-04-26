import java.io.*;
 
public class InitialCleanUp
{
	private static final String SRC_FOLDER = "diskfiles/Log Files";
	public static void main(String[] args)
	{

    	try
    	{    		 
    		File file1 = new File("summaryfile.txt");
    		File file2 = new File("diskfiles/Transaction Table.csv");
    		file1.delete();
    		file2.delete();
    	}
    	catch(Exception e)
    	{ 
    		e.printStackTrace();
 
    	}
		File directory = new File(SRC_FOLDER);
		if(!directory.exists())
		{
			System.out.println("Directory does not exist.");
			System.exit(0);
		}
		else
		{
			try
			{
				delete(directory);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
		new File("diskfiles/Log Files").mkdirs();
	}
	
	public static void delete(File file) throws IOException
	{
		if(file.isDirectory())
		{
			if(file.list().length==0)
			{ 
				file.delete(); 
			}
		    else
		    {
		    	String files[] = file.list();
		    	for (String temp : files)
		    	{
		    		File fileDelete = new File(file, temp);        	    
		    		delete(fileDelete);
		    	}
		    	if(file.list().length==0)
		    	{
		    		file.delete();
		    	}
		    }
		}
		else
		{
			file.delete();
		}
	}
}