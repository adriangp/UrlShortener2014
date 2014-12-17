# Validator Rest! Server (standalone)
This project contains web service build with [JAX-RS]
that can run standalone. That is, it does not require to be deployed in a server.

Run the code with ```gradle server``` and then navigate to [http://localhost:8080/validator]

The code of the service is:
```java

@POST
	@Path("/uri")
	@Consumes(MediaType.APPLICATION_JSON)
	public String validateUrlPOst(Uri url)  {
		HttpClient httpClient = null;  // Objeto a traves del cual realizamos las peticiones
		HttpMethodBase request = null;     // Objeto para realizar las peticiones HTTP GET o POST
		int status = 0;         // Codigo de la respuesta HTTP
		//String targetURL = "https://google.es";		
		// Instanciamos el objeto
		httpClient = new HttpClient();
		
		// Invocamos por Get
		if (url.getProtocol()==1)
			request = new GetMethod("https://" + url.getUrl()); 
		else if (url.getProtocol()==2)
			request = new GetMethod("http://" + url.getUrl()); 
		else
			return "Error: protocol not suported";

		request.setFollowRedirects(false);
		
		// Indicamos reintente en caso de que haya errores.
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		                                new DefaultHttpMethodRetryHandler(1, true));
		
		// Leemos el codigo de la respuesta HTTP que nos devuelve el servidor
		try {
			status = httpClient.executeMethod(request);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (UnknownHostException e){
			e.printStackTrace();
			return "Error: url bad formed";
		} catch (ConnectException e){
			e.printStackTrace();
			return "Error: Acces https not posible";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Vemos si la peticion se ha realizado satisfactoriamente
		if (status != HttpStatus.SC_OK) {
			String error = "Error\t" + request.getStatusCode() + "\t" + 
                    request.getStatusText() + "\t" + request.getStatusLine();
			System.out.println(error);	        	 
			return error;
		}
		return "SC_OK";
	
	}
```

Therefore,  the returned message if url exist 
