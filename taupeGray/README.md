

# Project "TaupeGray"

Taupe is a dark tan, sometimes grayish brown or brownish gray. The word derives from the French noun taupe meaning "mole". The name originally referred only to the average color of the French mole, but beginning in the 1940s, its usage expanded to encompass a wider range of shades.

# Team Members

* Rubén Tomás García (leader)
* Victor Arellano Vicente
* Marcela Simão Gomez da Silva

# Functionalities

1. QR generator from a short URL - Fully implemented
endpoint: 
	/qr{id}
	
classes created/modified/used:
	urlshortener2014.taupegray.web.UrlShortenerControllerWithLogs
	urlshortener2014.taupegray.qr.QRFetcher
	urlshortener2014.taupegray.client.*
	
2. Sponsorized URLs - Fully implemented
endpoint: 
	/l{id}
	
classes created/modified/used:
	urlshortener2014.taupegray.web.UrlShortenerControllerWithLogs
	urlshortener2014.taupegray.sponsor.WebSocketSponsorHandler
	urlshortener2014.taupegray.WebSocket
	urlshortener2014.taupegray.common.WebToStringWrapper
	
resources used:
	src/main/webapp/WEB-INF/sponsor.jsp
	
3. Secure browsing - Fully implemented
endpoint: 
	/l{id}

classes created/modified/used:
	urlshortener2014.taupegray.web.UrlShortenerControllerWithLogs
	urlshortener2014.taupegray.safebrowsing.SafeBrowsing
	urlshortener2014.taupegray.common.WebToStringWrapper
	urlshortener2014.taupegray.client.*
	
resources used:
	src/main/webapp/WEB-INF/warning.jsp
