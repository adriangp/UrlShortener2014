package urlshortener2014.richcarmine.web;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.richcarmine.massiveShortenerNaiveWS.*;
import urlshortener2014.richcarmine.massiveShortenerREST.ResponseData;
import urlshortener2014.richcarmine.massiveShortenerREST.ShortURLGenerator;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @Autowired
    private ShortURLRepository shortURLRepository;

    @Autowired
    EntityLinks entityLinks;

    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request) {
        logger.info("Requested redirection with hash " + id);
        return super.redirectTo(id, request);
    }

    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              @RequestParam(value = "brand", required = false) String brand,
                                              HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);
        return super.shortener(url, sponsor, brand, request);
    }

    @RequestMapping(value = "/qr", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> QRrize(@RequestParam("url") String url,
                                           @RequestParam(value = "sponsor", required = false) String sponsor,
                                           @RequestParam(value = "brand", required = false) String brand,
                                           HttpServletRequest request) {
        logger.info(this.toString());
        logger.info("Requested new short with QR code for uri " + url);
        return super.shortener(url, sponsor, brand, request);
    }

    @RequestMapping(value = "/qr{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> redirectQR(@PathVariable String id,
                                             HttpServletRequest request) {
        logger.info("Requested qr: qr" + id);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        //TODO improve this thing
        String uri = linkTo(methodOn(UrlShortenerController.class).redirectTo(id, null)).toUri().toString();
        String url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + uri + "&choe=UTF-8";
        ResponseEntity<?> re = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class);

        return new ResponseEntity<>((byte[]) re.getBody(), headers, HttpStatus.CREATED);
    }

    // ==========================================================================================
    //                         Serve csv files located on  /csv
    // ==========================================================================================
    @RequestMapping(value = "/csv/{fileName}", produces = "text/csv")
    public FileSystemResource serveCSV(@PathVariable String fileName, HttpServletResponse resp) {
        logger.info("Requested csv: " + fileName);
        File f = new File("csv/" + fileName + ".csv");
        if (!f.exists()) {
            logger.info(fileName + " File not found");
            throw new ResourceNotFoundException();
        }
        return new FileSystemResource(f);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private class ResourceNotFoundException extends RuntimeException {
    }

    // ==========================================================================================
    //                                  Massive shortener REST
    // ==========================================================================================

    @RequestMapping(value = "/rest/csv", method = RequestMethod.POST)
    public ResponseEntity<ResponseData> restCSVShortener(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              @RequestParam(value = "brand", required = false) String brand,
                                              HttpServletRequest req) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        CompletionService<CSVContent> pool = new ExecutorCompletionService<>(threadPool);

        logger.info("Massive shortener: " + file.getName() +"|"+ sponsor +"|"+ brand);
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream()));

            List<CSVContent> list = new ArrayList<>();

            String line = bf.readLine();
            long order = 0;

            /* Submit everything to the pool */
            while (line != null) {
                order ++;
                pool.submit(new ShortURLGenerator(order,line.trim(),sponsor,brand,req,this));
                line = bf.readLine();
            }

            /* retrieve everything */
            for(int i = 0; i < order; i++){
                logger.info("shortening: " + i);
                list.add(pool.take().get());
            }

            /* sort in case the request didn't finish in order */
            Collections.sort(list);

            /* Generating new file */
            String fileName = "csv/outputCSV" + System.currentTimeMillis() + ".csv";

            logger.info(fileName);

            File f = new File(fileName);
            f.createNewFile();

            PrintWriter writer = new PrintWriter(f);

            list.forEach(writer::println);

            writer.close();

            /* Respond */
            ResponseData data = new ResponseData();
            data.setUri(fileName);
            data.setCsv(list);

            ResponseEntity<ResponseData> re = new ResponseEntity<>(data, HttpStatus.OK);
            logger.info(re.toString());
            return re;
        } catch (IOException | InterruptedException |ExecutionException e) {
            logger.info("Smth went wrong");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // ==========================================================================================
    //                                  WebSocket? naive approach
    // ==========================================================================================

    public class MyHandler extends TextWebSocketHandler {

        AtomicLong messageOrder = new AtomicLong(0);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        CompletionService<CSVContent> pool = new ExecutorCompletionService<>(threadPool);

        /* controller reference */
        @Autowired
        UrlShortenerControllerWithLogs controller;

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            logger.info("WS: " + message.getPayload());
            long order = messageOrder.getAndIncrement();
            pool.submit(new ShortURLWSGenerator(order,message.getPayload(),"","","",session.getRemoteAddress(),controller));
            CSVContent content = pool.take().get();
            session.sendMessage(new TextMessage("Echo Test: " + content.getShortURL().getUri()));
        }
    }

    /* wrap the createAndSaveIfValid function */
    public class CreateCallable implements Callable<ShortURL>{
        String url;
        String sponsor;
        String brand;
        String owner;
        String ip;

        public CreateCallable(String url, String sponsor, String brand, String owner, String ip) {
            this.url = url;
            this.sponsor = sponsor;
            this.brand = brand;
            this.owner = owner;
            this.ip = ip;
        }

        @Override
        public ShortURL call() throws Exception {
            /* explodes while creating the new short url */
            return createAndSaveIfValid(url,sponsor,brand,owner,ip);
        }
    }
    // ==========================================================================================
    //                          WebSocket? naive approach with brokers
    // ==========================================================================================
    AtomicLong id = new AtomicLong(0);
    AtomicBoolean finished = new AtomicBoolean(false);
    List<CSVContent> list = new ArrayList<CSVContent>();

    @MessageMapping("/naivews/shorten")
    @SendTo("/response/naivews")
    public OutPutContent shorten(URLInput url) throws Exception {
        if (!finished.get()) {
            logger.info("WS: shorten " + url.getUrl() + "|");
            long order = id.getAndIncrement();

            /* doesn't work */
            ShortURL shortURL = createAndSaveIfValid(url.getUrl(), "smthrandom", "smthrandom", "smthrandom", "smthrandom");
            /* shortening simulation */
//            OutPutContent output = new OutPutContent(url.getUrl() + "/" + order);

            synchronized (this) {
//                this.list.add(new CSVContent(order, null,null));
            }

            return new OutPutContent(shortURL.getUri().toString());
        } else {
            return new OutPutContent("cannot add more content");
        }
    }

    @MessageMapping("/naivews/eof")
    @SendTo("/response/naivews")
    public synchronized OutPutContent eofFound(EOFInput url) throws Exception {
        if (!finished.get()) {
            finished.set(true);
            String fileName = "csv/outputFile" + System.currentTimeMillis() + ".csv";

            File f = new File(fileName);
            f.createNewFile();

            PrintWriter writer = new PrintWriter(f);

            Collections.sort(list);

            final PrintWriter finalWriter = writer;
            list.forEach(e -> finalWriter.println(e.getShortURL()));

            writer.close();

            return new OutPutContent("received eof order " + fileName);
        } else {
            return new OutPutContent("already finished");
        }
    }
}

