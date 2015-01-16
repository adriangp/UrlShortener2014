package urlshortener2014.bangladeshgreen.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ClickRepositoryImpl;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private AtomicInteger numeroAtomico = new AtomicInteger();

	@Autowired
	private ClickRepositoryImpl clickRepository;
	@Autowired
	private ShortURLRepository SURLR;
	private static final Logger logger = LoggerFactory
			.getLogger(UrlShortenerControllerWithLogs.class);

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {

		logger.info("Requested new short for uri " + url);
		ResponseEntity<ShortURL> su = super.shortener(url, sponsor, brand,
				request);

		// comprobar si es segura
		Client c = ClientBuilder.newClient();
		url = parse(url);
		Response response = c
				.target("https://sb-ssl.google.com/safebrowsing/api/lookup?client=Roberto&key="
						+ "AIzaSyBbjDCPwK13dOYioVf6Cp9_lrFZ_MOEFbU&appver=1.5.2&pver=3.1&url="
						+ url).request(MediaType.TEXT_HTML).get();

		if (response.getStatus() == 200) {

			SURLR.mark(su.getBody(), false);// marcar como no segura
			new DirectFieldAccessor(su.getBody()).setPropertyValue("safe",
					false);
		}
		return su;
	}

	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		String ip = request.getRemoteAddr();
		String navegador = "", SO = "";
		if (agent != null) {
			if (agent.indexOf("Chrome") != -1)
				navegador = "Chrome";
			else if (agent.indexOf("Firefox") != -1)
				navegador = "Firefox";
			else if (agent.indexOf("Safari") != -1)
				navegador = "Safari";
			else
				navegador = "Explorer";
			if (agent.indexOf("Windows") != -1)
				SO = "Windows";
			else if (agent.indexOf("Linux") != -1)
				SO = "Linux";
			else if (agent.indexOf("Macintosh") != -1)
				SO = "Macintosh";
			else
				SO = "Desconocido";
		} else {
			navegador = "Desconocido";
			SO = "Desconocido";
		}

		// Guardar en un objeto la llamada al padre, guardarme en una lista la
		// consulta
		// a los Cliks, y quedarme con el ultimo con la IP del request,
		// modificar el
		// click con el navegador y SO, actualizar BD y return
		ResponseEntity<?> response = super.redirectTo(id, request);
		List<Click> listaClicks = clickRepository.findByHash(id);
		for (int i = listaClicks.size() - 1; i >= 0; i--) {
			Click click = listaClicks.get(i);
			logger.info("Click con ip: " + click.getIp());
			if (click.getIp().equals(ip)) {
				String hash = click.getHash();
				Long identificador = click.getId();
				logger.info("ID del click: " + identificador);
				Date fecha = click.getCreated();
				Click clickFinal = new Click(identificador, hash, fecha, null,
						navegador, SO, ip, null);
				clickRepository.update(clickFinal);
				break;
			}
		}
		List<Click> listaClicks2 = clickRepository.findByHash(id);
		for (int i = listaClicks2.size() - 1; i >= 0; i--) {
			Click click = listaClicks2.get(i);
			logger.info("Click con ip: " + click.getIp());

			if (click.getIp().equals(ip)) {
				logger.info("Actualizado Click con " + click.getBrowser()
						+ " y SO: " + click.getPlatform() + " e ID: "
						+ click.getId());
				break;
			}
		}

		return response;
	}

	/**
	 * Guarda el CSV que recibe como parametro de un formulario web
	 * 
	 * @param request
	 *            peticion con el fichero
	 * @return mensaje de confirmacion en caso de carga correcta, de error en
	 *         caso contrario
	 * @throws Exception
	 */
	@RequestMapping(value = "/Upload", method = RequestMethod.POST)
	public String upload(HttpServletRequest request) throws Exception {

		int atomico = Integer.parseInt(request.getParameter("atomic"));

		Part part = request.getPart("fileToUpload");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				part.getInputStream()));
		Resource fich = new FileSystemResource("fich_original_" + atomico
				+ ".csv");
		if (!fich.exists())
			fich.createRelative("fich_original_" + atomico + ".csv");
		File fichero = fich.getFile();
		fichero.createNewFile();
		PrintWriter f = new PrintWriter(fichero);
		String linea = "";
		while ((linea = reader.readLine()) != null) {
			String[] listaURL = linea.split(",");
			for (String url : listaURL)
				f.write(url + ",");
			f.write("\n");
		}
		f.close();
		reader.close();
		Response respuesta = analizarCSV(fich, atomico, request);
		if (respuesta.getStatus() == 400) {
			return "Error con el fichero!";
		}
		return "Fichero Subido!"+atomico;
	}

	/**
	 * Lee el csv que recibe como parametro, acorta todas las URL que contenga
	 * 
	 * @param csv
	 *            CSV que contiene las URL a acortar
	 * @param request
	 *            necesario para llamar al acortador
	 * @return al response le anade un "ok" o un "BAD_REQUEST" segun corresponda
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public Response analizarCSV(Resource csv, int atomicInteger,
			HttpServletRequest request) throws IOException {
		File fichero = csv.getFile();
		BufferedReader br = new BufferedReader(new FileReader(fichero));
		Resource fich = new FileSystemResource("fich_temporal_" + atomicInteger
				+ ".csv");
		if (!fich.exists())
			fich.createRelative("fich_temporal_" + atomicInteger + ".csv");
		
		File csvAcortado = fich.getFile();
		csvAcortado.createNewFile();
		csvAcortado.deleteOnExit();
		PrintWriter fileResul = new PrintWriter(csvAcortado);
		String linea = "";
		while ((linea = br.readLine()) != null) {
			String[] listaURL = linea.split(",");
			for (String url : listaURL) {
				if (!url.equals(null) || !url.equals("")) {
					ResponseEntity<ShortURL> urlAcortada = shortener(url, null,
							null, request);
					if (urlAcortada.getBody() == null)
						return Response.status(Status.BAD_REQUEST).build();
					else
						fileResul.write(urlAcortada.getBody().getUri()
								.toString()
								+ ",");
				}
			}
			fileResul.write("\n");
		}
		fileResul.close();
		br.close();
		fichero.delete();
		return Response.status(Status.OK).build();
	}

	/**
	 * Codifica los caracteres de la URL que recibe como parametro para que esta
	 * pueda ser pasada por parametro en un formulario
	 * 
	 * @param a
	 *            String que contiene la URL
	 * @return String que contiene la URL con los caracteres especiales
	 *         codificados
	 */
	private static String parse(String a) {
		String res = "";
		for (int i = 0; i < a.length(); i++) {
			switch (a.charAt(i)) {
			case ':':
				res = res + "%3A";
				break;
			case '/':
				res = res + "%2F";
				break;
			case ' ':
				res = res + "%20";
				break;
			case '?':
				res = res + "%3F";
				break;
			case '<':
				res = res + "%3C";
				break;
			case '>':
				res = res + "%3E";
				break;
			case '%':
				res = res + "%25";
				break;
			case '#':
				res = res + "%23";
				break;
			case ';':
				res = res + "%3B";
				break;
			case '|':
				res = res + "%7C";
				break;
			case '&':
				res = res + "%26";
				break;
			default:
				res = res + a.charAt(i);
				break;
			}
		}
		return res;
	}

	@RequestMapping(value = "/atomic", method = RequestMethod.GET)
	private int generarTurno() {
		return numeroAtomico.incrementAndGet();
	}

	@RequestMapping(value = "/download/{id}/{nombre}", method = RequestMethod.GET)
	public void doDownload(@PathVariable int id, @PathVariable String nombre, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Resource fich = new FileSystemResource("fich_temporal_" + id
				+ ".csv");
		File downloadFile = fich.getFile();
		FileInputStream inputStream = new FileInputStream(downloadFile);
		String mimeType =  "application/vnd.ms-excel";
		
		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		nombre=nombre+"_acortado.csv";
		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				nombre);
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		OutputStream outStream = response.getOutputStream();
		byte[] buffer = new byte[2048];
		int bytesRead = -1;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		
		inputStream.close();
		outStream.close();
	}
}
