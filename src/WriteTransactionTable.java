import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteTransactionTable
{
	int tranrecord = 0; 								//buffer transaction table count
	String[][] tempTransaction = new String[10][3]; 	//TID, Status, Status after last Checkpoint
	
	public void transactionTable(String tid,String status,int checkpointcounter) throws IOException
	{		
		if(checkpointcounter > 1)
		{
			for(int i=0;i<tranrecord;i++)
			{
				tempTransaction[i][2] = null;
			}
			DBProject.checkpointcounter = 1;
		}
		
		if(checkpointcounter==0)
		{
			if(status.equals("ACTIVE"))
			{
				tempTransaction[tranrecord][0] = tid;
				tempTransaction[tranrecord][1] = status;
				//System.out.println(tid+" is set Active.");
				tranrecord++;
			}
			
			else
			{
				for(int i=0;i<tranrecord;i++)
				{
					if(tempTransaction[i][0].equals(tid))
					{
						//System.out.println(tid+" is set Commit.");
						tempTransaction[i][1] = status;
					}
				}
			}
		}
		else
		{
			if(status.equals("ACTIVE"))
			{
				//System.out.println(tid+" is set Active after CHKPNT.");
				tempTransaction[tranrecord][0] = tid;
				tempTransaction[tranrecord][1] = status;
				tempTransaction[tranrecord][2] = status;
				tranrecord++;
			}
			
			else
			{
				for(int i=0;i<tranrecord;i++)
				{
					if(tempTransaction[i][0].equals(tid))
					{
						//System.out.println(tid+" is set Commit after CHKPNT.");
						tempTransaction[i][1] = status;
						tempTransaction[i][2] = status;
					}
					if(tempTransaction[i][1].equals("ACTIVE"))
					{
						//System.out.println(tid+" is set Active after CHKPNT.");
						tempTransaction[i][2] = "ACTIVE";
					}
				}
			}
		}
		
		File file = new File("diskfiles/Transaction Table.csv");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for(int i=0;i<tranrecord;i++)
        {
			for(int j=0;j<3;j++)
			{
				bufferedWriter.write(tempTransaction[i][j]+",");	
			}
	        bufferedWriter.newLine();	
        }	        
        bufferedWriter.close();
	}
}
