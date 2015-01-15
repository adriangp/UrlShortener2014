package urlshortener2014.goldenPoppy.qr;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.repository.ShortURLRepository;

@RestController
public class QREndPoint {	
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	@RequestMapping(value = "/generateqr", method = RequestMethod.POST)
	public ResponseEntity<byte[]> generateQR(@RequestParam("url") String uri,
			 									HttpServletRequest request) {
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.IMAGE_PNG);
				
		String url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + uri + "&choe=UTF-8";
		
		byte[] image = restTemplate.getForObject(url, byte[].class);
		image = Base64.encodeBase64(image);
		
		return new ResponseEntity<>(image, h, HttpStatus.OK);
	}
}
