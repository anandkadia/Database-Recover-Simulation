import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale.Category;
import java.util.Map;
import java.util.Map.Entry;

public class DBProject
{
	int arrayrecord = 0;									//Keeping track of array record
	//int cacherecord = 0; 									//Buffer cache table count
	static int checkpointcounter = 0;						//Checkpoint count
	int[] cachememory = new int[10]; 						//Cache buffer of 10 items
	String[] linearray = new String[50]; 					//Array to store input instructions
	//String[][] cacheTable = new String[10][3];				//dataitem, diskblock(buffermemory), Modified Bit
	Map<String, CacheTableEntry> cacheTable = new HashMap<String, CacheTableEntry>();
	
	WriteLogBuffer WLF = new WriteLogBuffer();				//Object of WriteLogBuffer Class
	WriteSummaryFile WSF = new WriteSummaryFile();			//Object of WriteSummaryFile Class
	WriteTransactionTable WTT = new WriteTransactionTable();//Object of WriteTransactionTable Class
	WriteDiskBlock WDB = new WriteDiskBlock();
	FetchDiskItem FDI = new FetchDiskItem();
	
	public static void main(String args[]) throws IOException
	{		
		InitialCleanUp.main(args);
		InitiateDiskBlocks.main(args);
		DBProject obj = new DBProject();
		obj.readInputFile();
		obj.bufferintialize();
		obj.OperationDecide();
		obj.printing();
	}
	
	public void printing()
	{
		Iterator<Entry<String, CacheTableEntry>> it = cacheTable.entrySet().iterator();
		System.out.println("~~~~cachtable~~~~~");
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            CacheTableEntry cte = (CacheTableEntry) entry.getValue();
            System.out.println(entry.getKey() + " => " + cte.toString());
        }
        
		System.out.println("~~~~cachememory~~~~~");
		for(int j=0;j<10;j++)
		{
			System.out.print(cachememory[j]+"    ");
		}
	}
	
	public void readInputFile() throws IOException
	{
		String file = "inputfilehere/inputfile.txt";
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null)
		{
			arraystore(line);
		}
		br.close();
	}
	
	public void OperationDecide() throws IOException
	{
		for(int i=0;i<arrayrecord;i++){
			if(linearray[i].charAt(0)  >= 'a' && linearray[i].charAt(0)  <= 'z')
				Transaction_Operation(i);
			else if(linearray[i].charAt(0)  >= 'A' && linearray[i].charAt(0)  <= 'Z')
				System_Operation(i);
			else
				System.out.println("INPUT FILE LINE NO."+(i+1)+" HAS INCORRECT SYNTAX !");
		}
	}
	
	public void Transaction_Operation(int i) throws IOException
	{
		if(linearray[i].charAt(0)  == 'b')
			beginTransaction(i);
		else if(linearray[i].charAt(0)  == 'r')
			readTransaction(i);
		else if(linearray[i].charAt(0)  == 'w')
			writeTransaction(i);
		else if(linearray[i].charAt(0)  == 'e')
			endTransaction(i);
		else if(linearray[i].charAt(0)  == 'c')
			commitTransaction(i);
		else if(linearray[i].charAt(0)  == 'a')
			abortTransaction(i);
		else
			System.out.println("Transaction is to be defined.");
	}
	
	public void System_Operation(int i) throws IOException
	{
		if(linearray[i].charAt(0)  == 'C')
			checkpointTransaction(i);
		else if(linearray[i].charAt(0)  == 'F')
			failureTransaction(i);
		else if(linearray[i].charAt(0)  == 'W')
			writebufferTransaction(i);
		else if(linearray[i].charAt(0)  == 'L')
			forcewriteTransaction(i);
	}
	
	public void beginTransaction(int i) throws IOException
	{
		WLF.logBuffer("T"+linearray[i].charAt(1),"BEGIN","","","",0);
		WSF.summaryFile("T"+linearray[i].charAt(1)+" BEGINS and set ACTIVE.");
		WTT.transactionTable("T"+linearray[i].charAt(1),"ACTIVE",checkpointcounter);
	}
	
	public void readTransaction(int i) throws IOException
	{
		String dataitem = ""+linearray[i].charAt(3);
		WLF.logBuffer("T"+linearray[i].charAt(1),"READ",""+dataitem,"","",0);
		dataBuffer("READ",dataitem,0,0,i);
	}
	
	public void writeTransaction(int i) throws IOException
	{
		
		String dataitem = ""+linearray[i].charAt(3);
		int newvalue = Integer.parseInt(""+linearray[i].charAt(5));
		int datavalue = Integer.parseInt(""+linearray[i].charAt(7));
		WLF.logBuffer("T"+linearray[i].charAt(1),"WRITE",""+dataitem,""+linearray[i].charAt(7),""+linearray[i].charAt(5),0);
		WSF.summaryFile("T"+linearray[i].charAt(1)+" WRITES Data-Item '"+dataitem+"' from "+datavalue+" to "+newvalue);
		dataBuffer("WRITE",dataitem,datavalue,newvalue,i);
	}

	public void dataBuffer(String op, String dataitem, int datavalue,int newvalue,int i) throws IOException
	{
		if(op.equals("READ"))
		{
			CacheTableEntry cte = cacheTable.get(dataitem);
			if(cte == null)
			{
				WSF.summaryFile("T"+linearray[i].charAt(1)+" READS Data-Item '"+dataitem+"' from Disk block.");
				datavalue = FDI.readInputFile(dataitem);
				cte = new CacheTableEntry();
				cte.setLocation(putInCacheMemory(datavalue));
				cte.setTransactionId(linearray[i].charAt(1));
				cacheTable.put(dataitem, cte);
			}
			else
			{
				WSF.summaryFile("T"+linearray[i].charAt(1)+" READS Data-Item '"+dataitem+"' from cache memory");
				//System.out.println(dataitem+" was found in cachememory at "+x);
			}
			
		}
		else if(op.equals("WRITE"))
		{
			CacheTableEntry cte = cacheTable.get(dataitem);
			if(cte == null)
			{
				WSF.summaryFile("T"+linearray[i].charAt(1)+" READS Data-Item '"+dataitem+"' from Disk block.");
				datavalue = FDI.readInputFile(dataitem);
				cte = new CacheTableEntry();
				cte.setLocation(putInCacheMemory(datavalue));
				cte.setTransactionId(linearray[i].charAt(1));
				cacheTable.put(dataitem, cte);
			}
			cachememory[cte.getLocation()] = newvalue;
			cte.setModified(true);
		}
	}

	public int putInCacheMemory(int datavalue)
	{
		int retIndex=0;
		for(int i=0; i<cachememory.length; i++)
		{
			if(cachememory[i] == -9999)
			{
				cachememory[i] = datavalue;
				retIndex = i;
				break;
			}
		}
		return retIndex;
	}
	
	public void clearCacheMemory(int index)
	{
		cachememory[index] = -9999;
	}

	public void  commitTransaction(int i) throws IOException
	{
		WLF.logBuffer("T"+linearray[i].charAt(1),"COMMIT","","","",0);
        WSF.summaryFile("T"+linearray[i].charAt(1)+" COMMMITS AND FREES BUFFER MEMORY.");
        WTT.transactionTable("T"+linearray[i].charAt(1),"COMMIT",checkpointcounter);
        
        Iterator<Entry<String, CacheTableEntry>> it = cacheTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            CacheTableEntry cte = (CacheTableEntry) entry.getValue();
            if(cte.getTransactionId() == linearray[i].charAt(1) && cte.isModified())
            {
            	int diskvalue = cachememory[cte.getLocation()];
    			WDB.diskBlock((String)entry.getKey(), diskvalue);
    			clearCacheMemory(cte.getLocation());
    			it.remove();
            }
        }
	}

	public void abortTransaction(int i) throws IOException
	{	
		WLF.logBuffer("T"+linearray[i].charAt(1),"ABORT","","","",0);
        WSF.summaryFile("T"+linearray[i].charAt(1)+" GETS ABORTED.");
		WTT.transactionTable("T"+linearray[i].charAt(1),"ABORT",checkpointcounter);
	}

	public void endTransaction(int i) throws IOException
	{
		WLF.logBuffer("T"+linearray[i].charAt(1),"END","","","",0);
        WSF.summaryFile("T"+linearray[i].charAt(1)+" ENDS.");
		WTT.transactionTable("T"+linearray[i].charAt(1),"END",checkpointcounter);
	}
	public void forcewriteTransaction(int i)
	{
		
	}

	public void writebufferTransaction(int i)
	{
		
	}

	public void failureTransaction(int i) throws IOException
	{
        WSF.summaryFile("~~~~~SYSTEM FAILURE~~~~~");
		WLF.logBuffer("SYSFAIL","","","","",1);
		int chkpntrec = 0;
		arrayrecord--;
		for(int p=arrayrecord;p>=0;p--)
		{
			if(linearray[p].charAt(0) == 'C')
			{
				chkpntrec = p;
				break;
			}
		}
		callredooperation(chkpntrec);
		callundooperation();
	}

	public void callundooperation() throws IOException
	{
        WSF.summaryFile("~~~~~UNDO OPERATIONS~~~~~");	
        
        Map<String, String> transactionMap  = readTransactionFile();
        
        Iterator<Entry<String, String>> it = transactionMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            char txId = ((String)entry.getKey()).charAt(1);
            String txStatus = (String)entry.getValue();
            
            if(txStatus.equals("ACTIVE"))
            {
            	for(int i=arrayrecord-1;i>0;i--)
        		{
        			if(linearray[i].charAt(0)  == 'w' && linearray[i].charAt(1)  ==  txId)
        			{
        				String dataitem = ""+linearray[i].charAt(3);
        				int newvalue = Integer.parseInt(""+linearray[i].charAt(5));
        				int datavalue = Integer.parseInt(""+linearray[i].charAt(7));
        				WSF.summaryFile("T"+linearray[i].charAt(1)+" WRITES Data-Item '"+dataitem+"' from "+newvalue+" to "+datavalue);
        				WDB.diskBlock(dataitem, datavalue);
        			}
        		}
            }
        }
	}
	
	public void callredooperation(int chkpntrec) throws IOException
	{		
		WSF.summaryFile("~~~~~REDO OPERATIONS~~~~~");
		
		Map<String, String> transactionMap  = readTransactionFile();
		
		for(int i=(chkpntrec+1);i<arrayrecord;i++)
		{
			String txStatus = transactionMap.get("T"+linearray[i].charAt(1));
			
			if(linearray[i].charAt(0)  == 'w' && txStatus.equals("COMMIT"))
			{
				String dataitem = ""+linearray[i].charAt(3);
				int newvalue = Integer.parseInt(""+linearray[i].charAt(5));
				int datavalue = Integer.parseInt(""+linearray[i].charAt(7));
				WLF.logBuffer("T"+linearray[i].charAt(1),"WRITE",""+dataitem,""+linearray[i].charAt(7),""+linearray[i].charAt(5),0);
				WSF.summaryFile("T"+linearray[i].charAt(1)+" WRITES Data-Item '"+dataitem+"' from "+datavalue+" to "+newvalue);
				WDB.diskBlock(dataitem, newvalue);
			}
		}
	}
	
	public Map<String, String> readTransactionFile(){
		String file = "diskfiles/Transaction Table.csv";
		Map<String, String> transactionMap = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null)
			{
				String[] items = line.split(",");
				transactionMap.put(items[0], items[2]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return transactionMap;
	}
	
	public void checkpointTransaction(int i) throws IOException
	{
        WSF.summaryFile("~~~~~CHECKPOINT CREATED~~~~~");
		WLF.logBuffer("CHKPNT","","","","",1);
		checkpointcounter++;
	}
	
	public void arraystore(String line) {

		line = line.replaceAll(";| ","");
		linearray[arrayrecord] = line;
		arrayrecord++;
	}
	
	public void bufferintialize()
	{
		for(int i=0;i<10;i++)
		{
			cachememory[i] = -9999; //for simplicity of program
		}
	}
}