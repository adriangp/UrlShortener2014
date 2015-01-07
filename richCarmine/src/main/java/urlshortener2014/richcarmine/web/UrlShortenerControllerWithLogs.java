package urlshortener2014.richcarmine.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.richcarmine.massiveShortenerNaiveWS.CSVContent;
import urlshortener2014.richcarmine.massiveShortenerREST.ResponseData;
import urlshortener2014.richcarmine.massiveShortenerREST.ShortURLGenerator;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @Autowired
    private ShortURLRepository shortURLRepository;

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

    // ==========================================================================================
    //                                  QR code module
    // ==========================================================================================

    /**
     * Method that receives post petitions for url shortener with QR code.
     * @param url url to be shortened
     * @param sponsor not used
     * @param brand not used
     * @param request client request containing his IP
     * @return a response with the requested ShortURL
     */
    @RequestMapping(value = "/qr", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> QRrize(@RequestParam("url") String url,
                                           @RequestParam(value = "sponsor", required = false) String sponsor,
                                           @RequestParam(value = "brand", required = false) String brand,
                                           HttpServletRequest request) {
        logger.info(this.toString());
        logger.info("Requested new short with QR code for uri " + url);
        return super.shortener(url, sponsor, brand, request);
    }

    /**
     * Method that gets a QR code for the given ShortURL hash.
     * @param id hash of the ShortURL to be embedded in a QR code
     * @param request client request containing his IP
     * @return a response with the QR code (image) of the ShortURL
     */
    @RequestMapping(value = "/qr{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> redirectQR(@PathVariable String id,
                                             HttpServletRequest request) {
        logger.info("Requested qr: qr" + id);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<String> entity = new HttpEntity<>(headers);
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
    public FileSystemResource serveCSV(@PathVariable String fileName) {
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

        logger.info("Massive shortener: " + file.getName() + "|" + sponsor + "|" + brand);
        long start = System.currentTimeMillis();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream()));

            List<CSVContent> list = new ArrayList<>();

            String line = bf.readLine();
            long order = 0;

            /* Submit everything to the pool */
            while (line != null) {
                order++;
                pool.submit(new ShortURLGenerator(order, line.trim(), sponsor, brand, req, this));
                line = bf.readLine();
            }

            /* retrieve everything */
            for (int i = 0; i < order; i++) {
                logger.info("shortening: " + i);
                list.add(pool.take().get());
            }

            /* sort in case the request didn't finish in order */
            Collections.sort(list);

            /* Generating new file */
            String fileName = "csv/outputCSV" + UUID.randomUUID().toString() + ".csv";

            logger.info(fileName);

            File f = new File(fileName);
            f.createNewFile();

            PrintWriter writer = new PrintWriter(f);

            for(CSVContent c: list){
                writer.println( c );
            }

            writer.close();

            /* Respond */
            ResponseData data = new ResponseData();
            data.setConsumedTime(System.currentTimeMillis() - start);
            data.setUri(fileName);
            data.setCsv(list);

            ResponseEntity<ResponseData> re = new ResponseEntity<>(data, HttpStatus.OK);
            logger.info(re.toString());
            return re;
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.info("Smth went wrong");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // ==========================================================================================
    //                                  WebSocket? naive approach
    // ==========================================================================================

    public class MyHandler extends TextWebSocketHandler {

        private ObjectMapper mapper = new ObjectMapper();
        private AtomicLong messageOrder = new AtomicLong(0);
        private ExecutorService threadPool = Executors.newCachedThreadPool();
        private CompletionService<CSVContent> pool = new ExecutorCompletionService<>(threadPool);
        private List<CSVContent> csvContentList = new ArrayList<>();
        private String fileName = "csv/outputCSV" + UUID.randomUUID().toString() + ".csv";
        private String localAddress = "";
        AtomicBoolean finished = new AtomicBoolean(false);
        long start = 0;

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            localAddress = session.getHandshakeHeaders().getFirst("host");
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            finished.set(message.getPayload().trim().equals("<<EOF>>"));
            if (!finished.get()) {
                String messageURL = message.getPayload().trim();
                logger.info("[WS] shorten: " + messageURL);
                long order = messageOrder.getAndIncrement();
                if (order == 1) start = System.currentTimeMillis();
                pool.submit(new CreateCallable(order, messageURL, "", "", "", session.getRemoteAddress().getHostName()));
                CSVContent content = pool.take().get();
                synchronized (this) {
                    csvContentList.add(content);
                }
                session.sendMessage(new TextMessage(mapper.writeValueAsString(content)));
            } else {
            /* Generating new file */
                File f = new File(fileName);
                if (verifyAndCreate(f)) {
                    PrintWriter writer = new PrintWriter(f);

                    for(CSVContent c: csvContentList){
                        writer.println( c );
                    }

                    writer.close();
                }

                /* Respond */
                ResponseData data = new ResponseData();
                data.setConsumedTime(System.currentTimeMillis() - start);
                data.setUri(fileName);
                data.setCsv(csvContentList);
                session.sendMessage(new TextMessage(mapper.writeValueAsString(data)));
            }
        }

        private synchronized boolean verifyAndCreate(File f) throws IOException {
            return f.createNewFile();
        }

        /* wrap the createAndSaveIfValid function */
        public class CreateCallable implements Callable<CSVContent> {
            long order;
            String url;
            String sponsor;
            String brand;
            String owner;
            String ip;

            public CreateCallable(long order, String url, String sponsor, String brand, String owner, String ip) {
                this.order = order;
                this.url = url;
                this.sponsor = sponsor;
                this.brand = brand;
                this.owner = owner;
                this.ip = ip;
            }

            @Override
            public CSVContent call() throws Exception {
                ShortURL shortURL = nonHTTPCreateAndSaveIfValid(url, sponsor, brand, owner, ip);
                CSVContent content = new CSVContent();
                content.setOrder(order);
                content.setUrl(url);
                content.setShortURL(shortURL.getUri().toString());
                return content;
            }
        }

        public ShortURL nonHTTPCreateAndSaveIfValid(String url, String sponsor,
                                                    String brand, String owner, String ip) {
            UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                    "https"});
            if (urlValidator.isValid(url)) {
                String id = Hashing.murmur3_32()
                        .hashString(url, StandardCharsets.UTF_8).toString();
                ShortURL su = new ShortURL(id, url, createLink(id), sponsor, new java.sql.Date(
                        System.currentTimeMillis()), owner,
                        HttpStatus.TEMPORARY_REDIRECT.value(), true, ip,
                        getLocationByIP(ip));
                return shortURLRepository.save(su);
            } else {
                return null;
            }
        }

        private URI createLink(String id) {
            return URI.create("http://" + localAddress + "/l" + id);
        }
    }

    // ==========================================================================================
    //                                  Geolocation Module
    // ==========================================================================================

    /**
     * Method that given an IP address returns the country where it belongs.
     * @param ip address from where to retrieve country
     * @return country where the IP belongs
     */
    private String getLocationByIP(String ip) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ArrayList<MediaType> acceptableMedia = new ArrayList<>();
        acceptableMedia.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptableMedia);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "http://www.telize.com/geoip/" + ip;
        String country;
        try {
            ResponseEntity<String> re = restTemplate.exchange(url, HttpMethod.GET,
                    entity, String.class);
            JSONObject json = new JSONObject(re.getBody());
            logger.info("country: " + json.getString("country"));
            country = json.getString("country");
        } catch (Exception e) {
            country = null;
        }
        return country;
    }

    /**
     * Method that creates a ShortURL if it's valid, storing client's country too.
     * @param url url to be shortened
     * @param sponsor not used
     * @param brand not used
     * @param owner not used
     * @param ip client IP
     * @return a ShortURL object
     */
    @Override
    protected ShortURL createAndSaveIfValid(String url, String sponsor,
                                            String brand, String owner, String ip) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
        if (urlValidator.isValid(url)) {
            String id = Hashing.murmur3_32()
                    .hashString(url, StandardCharsets.UTF_8).toString();
            ShortURL su = new ShortURL(id, url,
                    linkTo(
                            methodOn(UrlShortenerController.class).redirectTo(
                                    id, null)).toUri(), sponsor, new Date(
                    System.currentTimeMillis()), owner,
                    HttpStatus.TEMPORARY_REDIRECT.value(), true, ip,
                    getLocationByIP(ip));
            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }

    /**
     * Method that creates a Click if it's valid, storing client's country too.
     * @param hash requested hash of the ShortURL
     * @param ip client IP
     */
    @Override
    protected void createAndSaveClick(String hash, String ip) {
        Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
                null, null, null, ip, getLocationByIP(ip));
        clickRepository.save(cl);
    }
}
