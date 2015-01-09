package urlshortener2014.goldenPoppy.isAlive;
/**
 * 
 * @author Javier Tello
 * 
 * Clase que encapsula una respuesta por parte del servicio isAlive al cliente. La librería
 * de sockJS a través de Jackson se encargará de convertirlo a JSON en el lado del cliente.
 * La respuesta tiene 3 "status":
 *  1 -> OK
 *  0 -> TIMEOUT
 * -1 -> URL muerta
 *
 */
public class Response {

	// Estado la URL
	private int status;
	
	/**
	 * Constructor de objetos Response
	 * @param status
	 */
	public Response(int status){
		this.status = status;
	}
	/**
	 * 
	 * @return El estado de la URL
	 */
	public int getStatus(){
		return status;
	}
	
}
