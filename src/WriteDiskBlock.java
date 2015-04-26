import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class WriteDiskBlock
{
	public void diskBlock(String dataitem,int datavalue) throws IOException
	{
		File file = new File("diskfiles/Data Files/"+dataitem+".txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(""+datavalue);
        bufferedWriter.close();
	}

}