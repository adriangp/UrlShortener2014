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
	 */
	@RequestMapping(value = "/showInfo", method = RequestMethod.POST)
	public ResponseEntity<List<InfoDB>> showData(HttpServletRequest request) {
		long limit = 10, offset = 0;
		logger.info("Requested new BD date.");
		logger.info("Datos de urls: "+shortURLRepository.list(limit,offset).size());
		logger.info("Datos de clicks: "+clickRepository.list(limit,offset).size());
		List<ShortURL> sul = shortURLRepository.list(limit,offset);
		List<InfoDB> result  = new ArrayList();
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
	
	@RequestMapping(value = "/modify", method = RequestMethod.GET)
	public ResponseEntity modify(@RequestParam("modOrDel") String modo,
			@RequestParam(value = "radMod", required = false) String key1,
			@RequestParam(value = "radDel", required = false) String key2,
			@RequestParam(value = "campo", required = false) String clave,
			@RequestParam(value = "valor", required = false) String valor,
			HttpServletRequest request) {
		/*
		 * Descubrir si es click o url
		 */
		boolean click;
		Scanner s;
		Long id = null;
		String hash = null;
		logger.info("key1: "+key1+"____key2: "+key2);
		if (key1!=null){
			s = new Scanner(key1);
			if (s.next().equals("click")){
				click=true;	id = s.nextLong();
			}
			else{
				click = false;hash = s.next();
			}
		}
		else{
			s = new Scanner(key2);
			if (s.next().equals("click")){
				click=true;	id = s.nextLong();
			}
			else{
				click = false;hash = s.next();
			}			
		}
		if (modo.equals("delete")){
			// Borra la url de la base de datos
			if (!click){
				logger.info("Borrando url con hash: "+hash);
				shortURLRepository.delete(hash);
			}
			// Borra el click de la base de datos
			else {
				// Convertir String a Long
				logger.info("Borrando click con id: "+id);
				clickRepository.delete(id);
			}
		}
		else {
			//modificar
			if (!click){
				
			}else{
				hash = s.next();
				String  created = s.next(), referrer = s.next(),
						browser = s.next(), platform = s.next(), 
						ip = s.next(), country = s.next();
				// Se convierte la fecha
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
				Date date = new Date(Integer.parseInt(created.substring(1,4)),
						Integer.parseInt(created.substring(6,7)),
						Integer.parseInt(created.substring(9,10)));	
				// Comprobamos si el campo es adecuado
					
				if (clave.toUpperCase().equals("CREATED")){
					
				}else if (clave.toUpperCase().equals("REFERRER")){
					Click cl = new Click(id, hash, date, valor,
							browser, platform, ip, country);
					
					logger.info("id:"+cl.getId()+". hash: "+cl.getHash()+". created: "
					+cl.getCreated().toString()+"referrer"+cl.getReferrer());
					
					clickRepository.update(cl);
				}else if (clave.toUpperCase().equals("BROWSER")){
					Click cl = new Click(id, hash, date, referrer,
							valor, platform, ip, country);
					clickRepository.update(cl);
				}else if (clave.toUpperCase().equals("PLATFORM")){
					Click cl = new Click(id, hash, date, referrer,
							browser, valor, ip, country);
					clickRepository.update(cl);
				}else if (clave.toUpperCase().equals("IP")){
					Click cl = new Click(id, hash, date, referrer,
							browser, platform, valor, country);
					clickRepository.update(cl);
				}else if (clave.toUpperCase().equals("COUNTRY")){
					Click cl = new Click(id, hash, date, referrer,
							browser, platform, ip, valor);
					clickRepository.update(cl);
				}else {
					return new ResponseEntity(HttpStatus.BAD_REQUEST);	
				}				
			}
			
		}
		
		return new ResponseEntity(HttpStatus.OK);		
	}
}

