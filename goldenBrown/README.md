

# Project "GoldenBrown"

Golden brown is commonly referenced in recipes as the desired color of properly baked foods. The first recorded use of golden brown as a color name in English was in the year 1891.

# Team Members

* Javier Garcia Barcos (leader)
* Jorge Garuz Sanchez
* Gabriel Barreras Sanz

# Functionalities
### URL Reachable (Implemented)
It is a Web Service that checks if a given URL can be reached or not. For that purpose it invokes a HEAD request method.
If the response of the HEAD request is successful (status 200) within a fixed timeout, then the URL is reachable.
If either the timeout expire without any response, or the URL tries to redirect to another site (status 3XX), 
or response contains any error status (4XXX, 5XX), then the URL is not reachable.

This Service is used directly by the front-end of the UrlShortener2014 application.
When the user introduces a URL to shorten, an Ajax call is made to this service in order to check if the URL is reachable or not.

Relevant libraries used:
* java.net.HttpURLConnection

### Platform and browser identifier (Implemented)
It is a Web Service that obtains the platform and the browser from a given User-Agent header.
It also obtains the browser version whenever it's known.

This Service is used by the UrlShortenerControllerWithLogs Service of the UrlShortener2014 application.
When the UrlShortenerControllerWithLogs service is asked for a redirection, a Rest call is made to this service in order to obtain the platform and browser of the client device.

Relevant libraries used:
* nl.bitwalker.useragentutils.UserAgent;

### BlackList (Implemented)
It is a Web Service that checks if a given URL is considered as Spam by some third-party anti-spam DNS Servers.
The idea of the service comes from the next PHP plugin: https://github.com/YOURLS/antispam
This service processes the input URL and builds three different DNS queries. Those queries are executed concurrently.
If any of the three responses received is positive, then the URL is considered as spam. Otherwise, it is considered as not-spam.
The service implements a cache system which makes possible to save the execution of the DNS queries whenever the URL is in the cache.

The Service is used by the UrlShortenerControllerWithLogs Service of the UrlShortener2014 application.
When the UrlShortenerControllerWithLogs service is asked for a new short url, a Rest call is made to this service in order to check if the URL is contained in any of the
third-party anti-spam databases. In the case the URL is considered as not-spam, a ShortURL is created and returned from the UrlShortenerControllerWithLogs service
to the user. The DNS queries for a given URL are executed just the first time. The following calls will be asked by the cache system with no need to actually execute the query. 
When the UrlShortenerControllerWithLogs service is asked for a redirection, a Rest call is made to this service in order to to check if the URL is spam or not.
If the URL was created (or checked) up to two hours ago, there is no need to execute the DNS query and a stored "safe" field is used to determine if it's spam or not.
Otherwise, if the URL was checked more than two hours ago, then the DNS query is executed.

Relevant libraries used:
* java.util.concurrent.ExecutorService;

### Interstitial Ad (Not implemented)