
public class CacheTableEntry {
	
	private int location;
	private boolean modified = false;
	private char transactionId;
	
	public CacheTableEntry(){
		
	}
	
	public CacheTableEntry(int location, boolean modified, char transactionId) {
		this.location = location;
		this.modified = modified;
		this.transactionId = transactionId;
	}
	
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public boolean isModified() {
		return modified;
	}
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	public char getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(char transactionId) {
		this.transactionId = transactionId;
	}
	
	public String toString(){
		return "[location=" + this.location + " | modified=" + this.modified + " | transactionID=" + this.transactionId + "]";
	}
	
}
