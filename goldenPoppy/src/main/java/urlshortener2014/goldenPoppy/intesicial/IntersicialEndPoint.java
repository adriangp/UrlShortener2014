package urlshortener2014.goldenPoppy.intesicial;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import urlshortener2014.common.repository.ShortURLRepository;

public class IntersicialEndPoint {	
	
	public String sURL;
	public HttpServletRequest request;
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	public IntersicialEndPoint(String sURL, HttpServletRequest request){
		this.sURL = sURL;
		this.request = request;
	}
	public ResponseEntity<?> redireccionarPubli(){
		// TODO: Buscar en BD el target FINAL. Crear HTML en fichero temporal con el iframe del sponsor y el javascript
		// que redireccione al target final
		HttpHeaders h = new HttpHeaders();
		
		return new ResponseEntity<>(h,HttpStatus.OK);
	}
}
