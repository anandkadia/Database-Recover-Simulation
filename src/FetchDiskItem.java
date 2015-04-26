import java.io.*;

public class FetchDiskItem
{
	public int readInputFile(String filename) throws IOException
	{
		String file = "diskfiles/Data Files/"+filename+".txt";
		BufferedReader br = new BufferedReader(new FileReader(file));
		int datavalue= Integer.parseInt(br.readLine());
		br.close();
		return datavalue;
	}
}
