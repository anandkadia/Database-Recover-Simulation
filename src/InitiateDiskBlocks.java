import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class InitiateDiskBlocks {

	public static void main(String[] args) throws IOException
	{
		for(char temp='A';temp<='Z';temp++)
		{
			File file = new File("diskfiles/Data Files/"+temp+".txt");
	        FileWriter fileWriter = new FileWriter(file);
	        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	        bufferedWriter.write("0");
	        bufferedWriter.close();
		}
	}
}
