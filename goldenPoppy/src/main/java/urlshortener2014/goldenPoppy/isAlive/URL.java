package urlshortener2014.goldenPoppy.isAlive;

public class URL {
	
	private String url;
	
	private int timeout;
	
	public URL(String u, int t){
		this.url = u;
		this.timeout = t;
	}
	public URL(){
		
	}
	
	public String getUrl(){
		return url;
	}
	
	public int getTimeout(){
		return timeout;
	}
	
}