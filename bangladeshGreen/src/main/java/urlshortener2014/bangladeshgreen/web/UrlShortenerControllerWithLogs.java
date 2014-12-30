package urlshortener2014.bangladeshgreen.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ClickRepository;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	@Autowired
	private ClickRepository clickRepository;
	private ShortURLRepository SURLR;
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
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+"url");
		ResponseEntity<ShortURL>su=super.shortener(url, brand, brand, request);
		//comprobar si es segura
		Client c = ClientBuilder.newClient();
		url=parse(url);
		Response response = c
				.target("https://sb-ssl.google.com/safebrowsing/api/lookup?client=Roberto&key=AIzaSyBbjDCPwK13dOYioVf6Cp9_lrFZ_MOEFbU&appver=1.5.2&pver=3.1&url="+url)
				.request(MediaType.TEXT_HTML)
				.get();
		
		if(response.getStatus()==200)
			SURLR.mark(su.getBody(), false);//marcar como no segura

		return su;
	}

	@SuppressWarnings("resource")
	public void recibirCSV(File csv,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(csv));
		List<ShortURL> listaUrlAcortadas=new ArrayList<ShortURL>();
		String linea="";
		while((linea = br.readLine()) != null){
			String []listaURL=linea.split(",");
			for(String url: listaURL){
				ResponseEntity<ShortURL> urlAcortada=shortener(url,sponsor,brand,request);
				listaUrlAcortadas.add(urlAcortada.getBody());
			}
		}
		File csvAcortado=new File("temporal.csv");
		PrintWriter fileResul=new PrintWriter(csvAcortado);
		for(ShortURL url: listaUrlAcortadas){
			//Obtener la URI y copiarla recortada
			fileResul.write(url.getUri().toString()+",\n");
		}
		fileResul.close();
	}
	
	
	
	private static String parse(String a) {
		String res="";
		for(int i=0;i<a.length();i++){
			switch(a.charAt(i)){
				case ':':
					res=res+"%3A";
					break;
				case '/':
					res=res+"%2F";
					break;
				case ' ':
					res=res+"%20";
					break;
				case '?':
					res=res+"%3F";
					break;
				case '<':
					res=res+"%3C";
					break;
				case '>':
					res=res+"%3E";
					break;
				case '%':
					res=res+"%25";
					break;
				case '#':
					res=res+"%23";
					break;
				case ';':
					res=res+"%3B";
					break;
				case '|':
					res=res+"%7C";
					break;
				case '&':
					res=res+"%26";
					break;
				default:
					res=res+a.charAt(i);
					break;
			}
		}
		return res;
	}
}
