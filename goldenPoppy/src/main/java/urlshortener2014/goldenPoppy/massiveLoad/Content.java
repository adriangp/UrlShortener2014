package urlshortener2014.goldenPoppy.massiveLoad;

public class Content {

	private int id;
	private String url;
	private String sponsor;
	
	public Content (int id, String url, String sponsor){
		this.url = url;
		this.sponsor = sponsor;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getURL(){
		return this.url;
	}
	
	public String getSponsor(){
		return this.sponsor;
	}
}
