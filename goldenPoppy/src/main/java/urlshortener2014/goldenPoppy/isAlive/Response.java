package urlshortener2014.goldenPoppy.isAlive;

public class Response {

	/*
	 * OK -> 1
	 * TIMEOUT -> 0
	 * MUERTA -> -1
	 */
	private int status;
	
	private String sessionId;
	
	public Response(int status, String sessionId){
		this.status = status;
		this.sessionId = sessionId;
	}
	
	public int getStatus(){
		return status;
	}
	
	public String getSessionId(){
		return sessionId;
	}
}
