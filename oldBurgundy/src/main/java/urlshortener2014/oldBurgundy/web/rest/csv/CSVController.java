package urlshortener2014.oldBurgundy.web.rest.csv;

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

@RestController
public class CSVController {

	@Autowired
	WorksRepository worksRepository;

	@RequestMapping(value = "/csv/rest/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> failValidteUrl(@PathVariable int id, @RequestBody String msg)  {
		Work work = this.worksRepository.takePendingWork(id);
		try {
			try{
				int status = Integer.parseInt(msg);
				work.getSession().sendMessage(new TextMessage("error::" + work.getLine() + "::" + HttpStatus.valueOf(status).toString()));
			}
			catch(NumberFormatException e){
				work.getSession().sendMessage(new TextMessage("shortUrl::" + work.getLine() + "::" + msg));
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
