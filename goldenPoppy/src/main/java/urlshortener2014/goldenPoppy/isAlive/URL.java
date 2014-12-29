package urlshortener2014.goldenPoppy.isAlive;

public class URL {
	
	private String url;
	
	private int timeout;
	
	/*
	 * Para distinguir entre sesiones. Meter Math.random() y
	 * comprobar a la vuelta que sea el mismo.
	 */
	private String sessionId;
	
	public String getUrl(){
		return url;
	}
	
	public int getTimeout(){
		return timeout;
	}
	
	public String getSessionId(){
		return sessionId;
	}
}