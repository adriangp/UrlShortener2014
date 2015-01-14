package urlshortener2014.dimgray.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Properties;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.repository.ShortURLRepositoryImpl;
import urlshortener2014.common.domain.Click;
import urlshortener2014.common.repository.ClickRepository;
import urlshortener2014.common.repository.ClickRepositoryImpl;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.dimgray.concurrent.UrlShortenerTask;
import urlshortener2014.dimgray.domain.UrlPair;
import urlshortener2014.dimgray.domain.UrlPairs;
import urlshortener2014.dimgray.domain.InfoDB;

/**
 * Clase que contiene los diferentes servicios web que se han implementado.
 * Acortador, redireccionador, CSV, QR y de bolcado de la BD.
 * @author Ivan y Paulo
 *
 */    
@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	private static final ExecutorService ex = Executors.newCachedThreadPool();
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		logger.info("Requested redirection with hash "+id);
		return super.redirectTo(id, request);
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
	}
	
	
	/**
	 * Servicio web que ejecuta llamadas de forma asíncrona gracias a un Thread Pool 
	 * y devuelve un objeto de la clase URLPairs como resultado de la operación.
	 * @param file Fichero csv con las urls a acortar. 
	 * @param sponsor Esponsor
	 * @param brand Marca
	 * @param request petición
	 * @return objeto de la URLPairs con los resultados.
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<UrlPairs> csvShortener(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new file: "+file.getName());
		try (InputStream inputStream = file.getInputStream();
			 BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));){
			List<Future<UrlPair>> futures = new ArrayList<Future<UrlPair>>();
			
			List<UrlPair> list = new ArrayList<UrlPair>();
			String line = bf.readLine();
			while(line != null){
				logger.info("Data: "+line);
				futures.add(ex.submit(new UrlShortenerTask(line.trim(),request,sponsor,brand, this)));
				line = bf.readLine();
			}
			 for(Future<UrlPair> future: futures){
				UrlPair pair = future.get();
				list.add(pair);
			}
			 UrlPairs ups = new UrlPairs();
			 ups.setUrlPairs(list);
			 
			 
			return new ResponseEntity<>(ups ,HttpStatus.OK);
		} catch (IOException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
	}
	
	/**
	 * Servicio QR que permite adquirir una imagen qr dada una url acortada.
	 * @param url Url acortada de la que hay que conseguir la imagen qr.
	 * @param request
	 * @return Un array de bytes en base64 con la imagen.
	 */
	@RequestMapping(value = "/qr", method = RequestMethod.GET)
	public ResponseEntity<byte[]> qrImage(@RequestParam("url") String url, 
			HttpServletRequest request) {
		byte[] png = null;
		
		logger.info("Requested qr for: "+url);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		RestTemplate rt = new RestTemplate();
		String imagePath = "https://chart.googleapis.com/chart?chs=150x150&cht=qr&chl="+url+"&choe=UTF-8";
		png = rt.getForObject(imagePath, byte[].class);
		png = Base64.encodeBase64(png);
		return new ResponseEntity<>(png,headers,HttpStatus.CREATED);
	}
	
		
	/**
	 * Servicio que devuelve los datos de clicks, usuarios y url de la base de datos
	 * 
	 * @param request
	 * @return Un array que contiene los datos de las dos tablas de la base de datos.
	 */
	@RequestMapping(value = "/showInfo", method = RequestMethod.POST)
	public ResponseEntity<List<InfoDB>> showData(HttpServletRequest request) {
		long limit = 10, offset = 0;
		logger.info("Requested new BD date.");
		logger.info("Datos de urls: "+shortURLRepository.list(limit,offset).size());
		logger.info("Datos de clicks: "+clickRepository.list(limit,offset).size());
		List<ShortURL> sul = shortURLRepository.list(limit,offset);
		List<InfoDB> result  = new ArrayList();
		// Los datos se añaden a una lista de InfoDB. InfoDB es la clase que contiene o un click
		// o una ShortURL
		for (int i = 0; i < sul.size(); i++){
			result.add(new InfoDB(sul.get(i).getHash(),sul.get(i).getTarget(),
					sul.get(i).getUri(),sul.get(i).getSponsor(),
					sul.get(i).getCreated(),sul.get(i).getOwner(),
					sul.get(i).getMode(),sul.get(i).getSafe(),sul.get(i).getIP(),
					sul.get(i).getCountry(),true, false));
			
			List<Click> cl = clickRepository.findByHash(sul.get(i).getHash());
			for (int j = 0; j < cl.size(); j++) {
				result.add(new InfoDB(cl.get(j).getId(),cl.get(j).getHash(),
						cl.get(j).getCreated(),cl.get(j).getReferrer(),
						cl.get(j).getBrowser(),cl.get(j).getPlatform(),
						cl.get(j).getIp(),cl.get(j).getCountry(),false, true));

			}
		}
		return new ResponseEntity<>(result ,HttpStatus.OK);		
		
	}
	/**
	 * Metodo que realiza consultas de moficacion o borrado a la base de datos.
	 * Se le pasa por parametro el tipo de consulta y las variables de donde se obtiene la 
	 * informacion 
	 * 
	 * @param modo Operacion contra la base de datos (DELETE o UPDATE)
	 * @param key1 Contiene los datos para modificar
	 * @param key2 Contiene los datos para borrar
	 * @param clave Campo a modificar en la base de datos
	 * @param valor Valor a introducir en la base de datos
	 * @param request
	 * @return HttpStatus
	 */
	@RequestMapping(value = "/modify/{parametros}", method = RequestMethod.POST)
	public ResponseEntity modify(@PathVariable("parametros") String parametros,
			HttpServletRequest request) {
		logger.info("dentro con: "+parametros);
		Scanner sc = new Scanner(parametros);
		sc.useDelimiter("[&]");
		List<String> listParameters = new ArrayList();
		while(sc.hasNext())
			listParameters.add(sc.next());
		logger.info("paso dos");
		
		// Averiguamos el modo en el que estamos
		String modo = listParameters.get(1);
		logger.info("modo = "+modo);
		for (int i = 0; i < listParameters.size(); i++)
			logger.info("pos = "+i+": "+listParameters.get(i));
		Scanner sm = new Scanner(modo);
		sm.useDelimiter("[=]");
		sm.next();
		modo = sm.next();
		if (!modo.equals("delete") && !modo.equals("modify"))  {
			modo = listParameters.get(listParameters.size()-1);
			sm = new Scanner(modo);
			sm.useDelimiter("[=]");
			sm.next();
			modo = sm.next();
		}
		logger.info("modo = "+modo);
		// ****************************************
		
		sm = new Scanner(listParameters.get(0));	
		sm.useDelimiter("[=]");
		logger.info("Pruebas = "+sm.next());
		sm.useDelimiter("[+]");
		String urlOrClick = sm.next().substring(1);
		logger.info("urlOrClick = "+urlOrClick);
		if (modo.equals("delete")){
			// Trabajamos con una url
			if (urlOrClick.equals("url")){
				sm = new Scanner(sm.next());
				sm.useDelimiter("[+]");
				String hash = sm.next();
				logger.info("hash = "+hash);
				shortURLRepository.delete(hash);
			}
			//Trabajamos con un click
			else{
				sm = new Scanner(sm.next());
				sm.useDelimiter("[+]");
				Long id = sm.nextLong();
				logger.info("id = "+id);
				clickRepository.delete(id);
			}
			
		}
		//Modificar ********************************
		else {			
			if (urlOrClick.equals("url")){
				sm = new Scanner(sm.next());
				sm.useDelimiter("[+]");
				String hash = sm.next();
				logger.info("hash = "+hash);
				ShortURL su = shortURLRepository.findByKey(hash);
				
				String  url = su.getTarget(), sponsor = su.getSponsor(), owner = su.getOwner(),
						ip = su.getIP(), country = su.getCountry();				
				int mode = su.getMode();
				URI  uri = su.getUri();
				boolean safe = su.getSafe();
				Date date = su.getCreated();	

				// Obtenemos la clave y el valor		
				sm = new Scanner(listParameters.get(2));
				sm.useDelimiter("[=]");
				sm.next();
				String clave = sm.next();
				sm = new Scanner(listParameters.get(3));
				sm.useDelimiter("[=]");
				sm.next();
				String valor = sm.next().replace("+"," ");
				
				logger.info("clave: "+clave+", valor:"+valor);
				//Modificamos
				switch(clave.toUpperCase()){
				case "SPONSOR": 
					su = new ShortURL(hash, url, uri, valor,
							date, owner, mode,safe, ip, country);					
					shortURLRepository.update(su);
					break;
				case "OWNER": 
					su = new ShortURL(hash, url, uri, sponsor,
							date, valor, mode,safe, ip, country);	
					shortURLRepository.update(su);
					break;
				case "IP":  
					su = new ShortURL(hash, url, uri, sponsor,
							date, owner, mode,safe, valor, country);	
					shortURLRepository.update(su);
					break;
				case "COUNTRY":
					su = new ShortURL(hash, url, uri, sponsor,
							date, owner, mode,safe, ip, valor);	
					shortURLRepository.update(su);
					break;
				default:
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);	
				}
			}
			//Trabajamos con un click
			else{
				Long id = sm.nextLong();
				String hash = sm.next();
				logger.info("id = "+id+". Hash :"+hash);
				List<Click> lc = clickRepository.findByHash(hash);
				Click cl = null;
				boolean encontrado = false;
				// Buscamos el click
				for (int i = 0; i < lc.size() && !encontrado; i++)	{
					logger.info("buscando");
					cl = lc.get(i);
					logger.info("2- busqueda");
					if (cl.getId() == id)
						encontrado = true;
				}				
				logger.info("despues de busqueda");
						
				String  referrer = cl.getReferrer(),browser = cl.getBrowser(), 
						platform = cl.getPlatform(), ip = cl.getIp(), 
						country = cl.getCountry();
				Date date = cl.getCreated();

				logger.info("Consiguiendo la clave y el valor");
				// Obtenemos la clave y el valor		
				sm = new Scanner(listParameters.get(2));
				sm.useDelimiter("[=]");
				sm.next();
				String clave = sm.next();
				sm = new Scanner(listParameters.get(3));
				sm.useDelimiter("[=]");
				sm.next();
				String valor = sm.next().replace("+"," ");
				logger.info("clave: "+clave+", valor:"+valor);
				
				// Comprobamos si el campo es adecuado
				// La fecha no tiene sentido modificarla, pero es campo correcto	
				switch(clave.toUpperCase()){
				case "REFERRER": 
					cl = new Click(id, hash, date, valor,
							browser, platform, ip, country);					
					clickRepository.update(cl);
					break;
				case "BROWSER": 
					cl = new Click(id, hash, date, referrer,
							valor, platform, ip, country);
					clickRepository.update(cl);
					break;
				case "PLATFORM":  
					cl = new Click(id, hash, date, referrer,
							browser, valor, ip, country);
					clickRepository.update(cl);
					break;
				case "IP":
					cl = new Click(id, hash, date, referrer,
							browser, platform, valor, country);
					clickRepository.update(cl);
					break;
				case "COUNTRY":
					cl = new Click(id, hash, date, referrer,
							browser, platform, ip, valor);
					clickRepository.update(cl);
					break;
				default:
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);	
				}		
			}
		}
		return new ResponseEntity(HttpStatus.OK);		
		
	}
}

