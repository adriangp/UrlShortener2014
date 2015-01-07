package urlshortener2014.oldBurgundy.web.rest.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;

import urlshortener2014.oldBurgundy.repository.csv.Work;
import urlshortener2014.oldBurgundy.repository.csv.WorksRepository;

/**
 * Rest controllor of the csv service
 */
@RestController
public class CSVController {

	private static final Logger logger = LoggerFactory.getLogger(CSVController.class);

	@Autowired
	WorksRepository worksRepository;

	/**
	 * Manages the result of the short URL request
	 * @param id Id of the work
	 * @param msg Short URL
	 * @return
	 */
	@RequestMapping(value = "/csv/rest/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> shortUrlResult(@PathVariable int id, @RequestBody String msg)  {
		
		Work work = this.worksRepository.takePendingWork(id);
		int i = 0;
		do{
			i++;
			try {
				try{
					int status = Integer.parseInt(msg);
					work.getSession().sendMessage(new TextMessage("error::" + work.getLine() + "::" + HttpStatus.valueOf(status).getReasonPhrase()));
					logger.info("Work finish url: '" + work.getUrl() + "' error: '" + status + "'");
				}
				catch(NumberFormatException e){
					work.getSession().sendMessage(new TextMessage("shortUrl::" + work.getLine() + "::" + msg));
					logger.info("Work finish url: '" + work.getUrl() + "' hash: '" + msg + "'");
				}
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				
			}
		}while(i < 3);
		
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
