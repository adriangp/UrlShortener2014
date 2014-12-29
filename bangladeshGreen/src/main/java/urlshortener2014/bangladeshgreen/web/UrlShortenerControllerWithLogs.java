package urlshortener2014.bangladeshgreen.web;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ClickRepository;
import urlshortener2014.common.web.UrlShortenerController;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {
	@Autowired
	private ClickRepository clickRepository;
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash "+id);
		String agent = request.getHeader("User-Agent");
		String ip=request.getRemoteAddr();
		String navegador="",SO="";
		if(agent.indexOf("Chrome")!=-1) navegador="Chrome";
		else if(agent.indexOf("Firefox")!=-1) navegador="Firefox";
		else if(agent.indexOf("Safari")!=-1) navegador="Safari";
		
		if(agent.indexOf("Windows")!=-1) SO="Windows";
		else if(agent.indexOf("Linux")!=-1) SO="Linux";
		
	
		logger.info("Requested redirection with hash "+id);
		// Guardar en un objeto la llamada al padre, guardarme en una lista la consulta
		// a los Cliks, y quedarme con el ultimo con la IP del request, modificar el
		// click con el navegador y SO, actualizar BD y return
		ResponseEntity<?> response=super.redirectTo(id,request);
		List<Click> listaClicks=clickRepository.findByHash(id);
		for(int i=listaClicks.size()-1;i>0;i--){
			Click click=listaClicks.get(i);
			if(click.getIp().equals(ip)){
				String hash=click.getHash();
				Long identificador=click.getId();
				Date fecha=click.getCreated();
				Click clickFinal=new Click(identificador,hash,fecha,null,navegador,SO,ip,null);
				clickRepository.update(clickFinal);
			}
		}
		return response;
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(
			@RequestParam MultiValueMap<String, String> form) {
		logger.info("Requested new short for uri "+form.getFirst("url"));
		return super.shortener(form);
	}
}
