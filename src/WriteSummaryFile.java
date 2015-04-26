import java.io.*;

public class WriteSummaryFile
{
	public void summaryFile(String summaryline) throws IOException
	{
		File file = new File("summaryfile.txt");
	    FileWriter fileWriter = new FileWriter(file,true);
	    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(summaryline);
		bufferedWriter.newLine();
		bufferedWriter.close();
	}
}
