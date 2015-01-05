package urlshortener2014.goldenPoppy.isAlive;

import org.apache.commons.validator.routines.UrlValidator;

public class URL {
	
	private String url;
	
	private int timeout;
	
	public String getUrl(){
		return url;
	}
	
	public int getTimeout(){
		return timeout;
	}
	
	public boolean isValid(){
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