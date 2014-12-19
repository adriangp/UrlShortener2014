package urlshortener2014.goldenPoppy.isAlive;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class IsAliveController {


    @MessageMapping("/isalive")
    @SendTo("/topic/isalive")
    public String isAlive(URL url) throws Exception {
    	// TODO Con HttpClient HTTP HEAD, comprobar si la URL está disponible
    	// En funcion de si o no, devuelve una cosa u otra
        return url + " is alive!";
    }

}