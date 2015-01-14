package urlshortener2014.goldenPoppy.massiveLoad;

public class Status {

	private int percent;
	private String status;
	private String url; 
	
	/**
	 * Constructor
	 * 
	 * @param p Percent of the load.
	 * @param s Status of the load.
	 * @param u Url that contains the file with the URLs shortened.
	 */
	public Status(int p, String s, String u){
		this.percent = p;
		this.status = s;
		this.url = u;
	}
	
	/**
	 * Generic Constructor
	 */
	public Status(){
		
	}
	
	/**
	 * Method to get the percent of the load.
	 * @return Percent of the load.
	 */
	public int getPercent(){
		return this.percent;
	}
	
	/**
	 * Method to set the percent of the load
	 * @param percent New percent
	 */
	public void setPercent(int percent){
		this.percent = percent;
	}
	
	/**
	 * Method to get the status of the load.
	 * @return Status of the load.
	 */
	public String getStatus(){
		return this.status;
	}
	
	/**
	 * Method to set the status of the load
	 * @param percent New status
	 */
	public void setStatus(String status){
		this.status = status;
	}
	
	/**
	 * Method to get the URL that contains the file with the URLs shortened.
	 * @return URL that contains the file with the URLs shortened.
	 */
	public String getUrl(){
		return this.url;
	}
	
	/**
	 * Method to set the URL of the file of the load
	 * @param percent New URL of the file
	 */
	public void setURL(String url){
		this.url = url;
	}
}
