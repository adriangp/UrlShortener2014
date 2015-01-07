package urlshortener2014.goldenPoppy.qr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.*;

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

@RestController
public class QREndPoint {	
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	@RequestMapping(value = "/qr/{uri}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> redirectQR(@PathVariable String uri,
			 									HttpServletRequest request) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.IMAGE_PNG);
		HttpEntity<String> entity = new HttpEntity<>(h);
				
		String url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + uri + "&choe=UTF-8";
		
		ResponseEntity<?> re = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
		return new ResponseEntity<>((byte[]) re.getBody(), h, HttpStatus.OK);
	}
}
