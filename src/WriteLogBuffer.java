import java.io.*;

public class WriteLogBuffer
{	
	String[][] logBuffer = new String[4][5]; 			//log buffer in main memory
	int logrecord = 0; 									//buffer log count
	int logint = 1;										//log file count
	
	public void logBuffer(String tid,String operation,String dataitem,String bfim,String afim,int check) throws IOException
	{
		if(check==1)
		{
			logBuffer[logrecord][0]=tid;
			logBuffer[logrecord][1]=operation;
			logBuffer[logrecord][2]=dataitem;
			logBuffer[logrecord][3]=bfim;
			logBuffer[logrecord][4]=afim;
			logrecord++;
			flushLogBuffer();
		}
		else
		{
			
			if(logrecord == 3)
			{
				logBuffer[logrecord][0]=tid;
				logBuffer[logrecord][1]=operation;
				logBuffer[logrecord][2]=dataitem;
				logBuffer[logrecord][3]=bfim;
				logBuffer[logrecord][4]=afim;
				logrecord++;
				flushLogBuffer();
			}
			else 
			{
				logBuffer[logrecord][0]=tid;
				logBuffer[logrecord][1]=operation;
				logBuffer[logrecord][2]=dataitem;
				logBuffer[logrecord][3]=bfim;
				logBuffer[logrecord][4]=afim;
				logrecord++;
			}
		}
	}
	
	public void flushLogBuffer() throws IOException
	{
		File file = new File("diskfiles/Log Files/LOG"+logint+".csv");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for(int x=0;x<logrecord;x++)
        {
			for(int y=0;y<5;y++)
			{
				bufferedWriter.write(logBuffer[x][y]+",");	
			}
	        bufferedWriter.newLine();	
        }	        
        bufferedWriter.close();
		logint++;
		logrecord=0;		
	}

}