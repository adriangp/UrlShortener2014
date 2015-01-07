

# Project "RichCarmine"

The rich carmine color tone displayed at right matches the color shown as carmine in the 1930 book A Dictionary of Color. This color is also called Chinese carmine. This is the color usually referred to as carmine in fashion and interior design.


# Team Members

* Adrian Reyes (leader)
* Sandra Campos
* David Recuenco

# Funcionalities

Massive URL upload (CSV File)
----------------------------
### REST (@/rest/csv)
First approach, the service receives a POST request on /rest/csv with the following parameter:

|url|the file to be upload|
|---|---------------------|

The service will respond with a json containing the following tree:
```json
{
  "uri":"the uri used to locate the csv file with all the short urls (csv/some_file.csv)",
  "consumedTime": "a number, which is time consumed by the server",
  "csv":[
    {"url":"former url", "shortURL":"shortened url"}
  ]
}
```

### WebSocket (@/ws/naivews)
In this case the service requires the client to keep the same communication protocol, this websocket will try to transform every message into a ShortURL even if it isn't a URL, answering null in this case. The communication will end when the websocket reads `"<<EOF>>"`  and will terminate the session, thus forcing just one conversion per session.

With any message the websocket receives, it will answer back with a CSVContent as json:

```json
{
  "order":"the message's order number",
  "url": "the former url",
  "shortURL": "the shortened url"
}
```
Then, when receiving `"<<EOF>>"` the websocket will stop transforming urls and will answer back with the same json as the REST service
```json
{
  "uri":"the uri used to locate the csv file with all the short urls (csv/some_file.csv)",
  "consumedTime": "a number, which is time consumed by the server",
  "csv":[
    {"url":"former url", "shortURL":"shortened url"}
  ]
}
```

Get QR code from ShortURL
----------------------------
We wanted to create a proxy over Google's QR code API. To do it, the method is simple. We have a service `/qr{id}` where `id` is the ShortURL hash. 
When the service receives a `GET` request a new request (proxied) is sent to Google API to make a QR code. 
Then, from its response we get the body, which is a `byte[]` containing the QR code as a PNG image, we place it in our own response and send it back.

Geolocation via IP
----------------------------
As an additional feature, we do not only store user's IP but their country location too. This is done by using an [external API](http://www.telize.com/geoip/) that, given an IP, returns various location data, including the country. 
