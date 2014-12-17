package urlshortener2014.goldenPoppy.intesicial;

import java.util.HashMap;
import java.util.Map;


import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;

@Component
public class IntersicialEndPoint {	
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	@Autowired
	private VelocityEngine engine;
	
	public ResponseEntity<?> redireccionarPubli(String sURL){
		// TODO: Buscar en BD el target FINAL. Crear HTML en fichero temporal con el iframe del sponsor y el javascript
		// que redireccione al target final
		Map<String, Object> model = new HashMap<String, Object>();
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.TEXT_HTML);
		ShortURL s = shortURLRepository.findByKey(sURL);
		String cuerpo_html = ""; // Se crea con velocity

		model.put("sponsor", s.getSponsor());
		model.put("target", s.getTarget());
		cuerpo_html = VelocityEngineUtils.mergeTemplateIntoString(this.engine,
				"template.html", "UTF-8", model);

		return new ResponseEntity<>(cuerpo_html,h,HttpStatus.OK);
	}
}
