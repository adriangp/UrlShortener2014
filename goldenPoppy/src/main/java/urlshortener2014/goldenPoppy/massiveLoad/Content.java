package urlshortener2014.goldenPoppy.massiveLoad;

public class Content {

	private int id;
	private String url;
	private String sponsor;
	
	/**
	 * Constructor
	 * 
	 * @param id Identifier of the URL.
	 * @param url URL to short.
	 * @param sponsor Sponsor associated to the URL.
	 */
	public Content (int id, String url, String sponsor){
		this.url = url;
		this.sponsor = sponsor;
	}
	
	/**
	 * Method to get the identifier of the URL.
	 * @return Identifier of the URL.
	 */
	public int getId(){
		return this.id;
	}
	
	/**
	 * Method to get the URL to short.
	 * @return URL to short.
	 */
	public String getURL(){
		return this.url;
	}
	
	/**
	 * Method to get the sponsor associated to the URL.
	 * @return Sponsor associated to the URL.
	 */
	public String getSponsor(){
		return this.sponsor;
	}
	
	/**
	 * Method to compare 2 instances of Content.
	 */
	@Override
	public boolean equals(Object c){
		if (c instanceof Content)
			return  this.id == ((Content) c).getId() &&
					this.url.equals(((Content) c).getURL()) &&
					this.sponsor.equals(((Content) c).getSponsor());
		else return false; 
	}
}
