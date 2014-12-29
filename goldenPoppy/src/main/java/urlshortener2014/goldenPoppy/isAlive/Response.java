package urlshortener2014.goldenPoppy.isAlive;

public class Response {

	/*
	 * OK -> 1
	 * TIMEOUT -> 0
	 * MUERTA -> -1
	 */
	private int status;
	
	public Response(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return status;
	}
	
}
