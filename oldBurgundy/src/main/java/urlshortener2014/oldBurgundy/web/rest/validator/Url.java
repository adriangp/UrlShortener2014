package urlshortener2014.oldBurgundy.web.rest.validator;

public class Url {
	
	private String url;
	private String sponsor;
	
	public Url(){
		
	}
	
	public Url(String url, String sponsor){
		this.url = url;
		this.sponsor = sponsor;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getSponsor() {
		return sponsor;
	}
	
}
