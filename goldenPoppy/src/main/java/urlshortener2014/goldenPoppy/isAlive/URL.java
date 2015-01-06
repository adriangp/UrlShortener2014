package urlshortener2014.goldenPoppy.isAlive;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * 
 * @author Javier Tello
 *
 * Encapsula un objeto URL. El JSON que viene desde el cliente se convierte 
 * a un objeto URL. Todo de manera transparente, de ello se encarga 
 * la librería de Spring mediante Jackson.
 * Encapsula la url y su timeout asociado. 
 * 
 */
public class URL {
	
	// URL
	private String url;
	
	// Timeout asociado
	private int timeout;
	
	/**
	 * 
	 * @return La url
	 */
	public String getUrl(){
		return url;
	}
	
	/**
	 * 
	 * @return El timeout asociado
	 */
	public int getTimeout(){
		return timeout;
	}
	
	/**
	 * Comprueba que tanto la URL como el timeout es válido. Los criterios para validar la
	 * URL es que sea o "http" o "https" y la sintaxis la valida la librería UrlValidator.
	 * Para validar el timeout se comprueba que no es nulo y comprendido entre 2 y 30.
	 * 
	 * @return true si es válida, false en caso contrario
	 */
	public boolean isValid(){
		if (url == null){
			return false;
		}
		String[] schemes = {"http","https"};
		UrlValidator validator = new UrlValidator(schemes);
		if (validator.isValid(url)){
			if (timeout >= 2 && timeout <= 30){
				return true;
			} else{
				return false;
			}
		} else{
			return false;
		}
		
	}
	
}