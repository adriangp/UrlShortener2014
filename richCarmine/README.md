


# Project "RichCarmine"

The rich carmine color tone displayed at right matches the color shown as carmine in the 1930 book A Dictionary of Color. This color is also called Chinese carmine. This is the color usually referred to as carmine in fashion and interior design.

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

# Team Members

* Adrian Reyes (leader)
* Sandra Campos
* David Recuenco