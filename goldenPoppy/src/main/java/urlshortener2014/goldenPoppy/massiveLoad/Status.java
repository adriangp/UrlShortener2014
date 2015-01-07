package urlshortener2014.goldenPoppy.massiveLoad;

public class Status {

	private double percent;
	private String status;
	private String url; 
	
	/**
	 * Constructor
	 * 
	 * @param p Percent of the load.
	 * @param s Status of the load.
	 * @param u Url that contains the file with the URLs shortened.
	 */
	public Status(double p, String s, String u){
		this.percent = p;
		this.status = s;
		this.url = u;
	}
	
	/**
	 * Method to get the percent of the load.
	 * @return Percent of the load.
	 */
	public double getPercent(){
		return this.percent;
	}
	
	/**
	 * Method to get the status of the load.
	 * @return Status of the load.
	 */
	public String getStatus(){
		return this.status;
	}
	
	/**
	 * Method to get the URL that contains the file with the URLs shortened.
	 * @return URL that contains the file with the URLs shortened.
	 */
	public String getUrl(){
		return this.url;
	}
}
