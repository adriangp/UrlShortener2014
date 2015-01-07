

# Project "OldBurgundy"

The color old burgundy is a dark tone of burgundy. The first recorded use of old burgundy as a color name in English was in 1926.

# Team Members

* Pablo Oliver Remón (leader)
* Jorge Izquierdo Bueno
* Ismael Artero Gasca

# Features implemented

* Rest Validator Services (100%)
	** This services have two rest endpoints:
	https://github.com/pablooli/UrlShortener2014/blob/master/oldBurgundy/src/main/java/urlshortener2014/oldBurgundy/web/rest/validator/ValidatorWebService.java .
	The first its for client calls  "/validator"
		- This endpoint evaluates accessibility of url taken to parameter (Post)
		If skip an error return HttpStatus.{error.name}.value.
		If skip ok return HttpStatus.OK.
		Only evaluate http and https urls.
	The second its for internal server calls "/validator/{id}"
		This endpoint taken id and url and return 200 if ok.
		
* CSV Services (100%)
	This services consist in Client and 3 endpoints:
	Client is a HTML/JS application
		Offer a friendly user interface to connect at websoket endpoint.
		Have JS code to loading a File.
		Have JS code to download a File.
	First endpoint is a WebSoket "/csv/ws"
	https://github.com/pablooli/UrlShortener2014/blob/master/oldBurgundy/src/main/java/urlshortener2014/oldBurgundy/web/websocket/csv/CSVHandler.java .
		This service is called from the client and take TextMenssage and divide
		it by "," with split,  msg.length=2 means this msg no contains sponsor and if
		msg.length=3 this msg contains sponsor. In both case add to url "sponsor" and session
		to a BlockingQueue. In other case return to client session an error message.
	Second is a rest endpoint "/link/{id}"
	https://github.com/pablooli/UrlShortener2014/blob/master/oldBurgundy/src/main/java/urlshortener2014/oldBurgundy/web/rest/UrlShortenerControllerOldBurgundy.java
		This enpoint taken id  url "sponsor" and "brand".
		Call to a shortener() method for add url to BBDD and create shorturl.
		Call to a Third enpoint "http://localhost:8080/csv/rest/" with id and url.
	Third is a rest endpoint "/csv/rest/{id}"
	https://github.com/pablooli/UrlShortener2014/blob/master/oldBurgundy/src/main/java/urlshortener2014/oldBurgundy/web/rest/csv/CSVController.java .
		This endpoint take id and msg.
		Add to Hash pendingWork id and return HttpStatus.ok if all go ok, or 		Http.INTERNAL_SERVER_ERROR if something go wrong.
		This services returns by websoket session to client error or shorturl.
		
* Sponsor Web Services (100%)
	This services consist in Client and 2 endpoints:
	Client is a HTML/JS application.
		Offer to client a banner and connects to websocket and send shorturl
		And wait msg from websoket (10 seg), this msg can contains url(in this
		case redirected to this) or error.
	First endpoint is a Websocket "/sponsor/ws"
	https://github.com/pablooli/UrlShortener2014/blob/master/oldBurgundy/src/main/java/urlshortener2014/oldBurgundy/web/websocket/sponsor/SponsorHandler.java .
		This service is called from the client and take TextMenssage and divide
		it by " " with split,  msg.length=1 add  shorturl  and session to Hash 
		WaitingClient. In other case return to client session an error message.
	Second endpoint is a rest "/l{id}"
	https://github.com/pablooli/UrlShortener2014/blob/master/oldBurgundy/src/main/java/urlshortener2014/oldBurgundy/web/rest/UrlShortenerControllerOldBurgundy.java .
		This service taken id (shorturl), check id in BBDD, is not found return
		HttpStatus.NOT_FOUND. Its found obtained url and sponsor, if sponsor=null
		or sponsor is empty put "default_sponsor.html", else put sponsor obtained
		in previous search . Return templates HTML/JS with velocity and HttpStatus.OK. 

* To disengage services and get scalability, use files .properties.
* Use Junit test for the endpoints.
* Use velocity to generate HTML/JS templates return.


	
		
		
	
	 
