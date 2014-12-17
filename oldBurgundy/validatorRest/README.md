# Validator Rest! Server (standalone)
This project contains web service build with [JAX-RS]
that can run standalone. That is, it does not require to be deployed in a server.

Run the code with ```gradle server``` and then navigate to [http://localhost:8080/validator]

The code of the service is:
```java
@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String validateUrl(@Context String TargetURL) throws HttpException, IOException {
		System.out.println("llamada");
		HttpClient httpClient = null;  // Objeto a trav�s del cual realizamos las peticiones
		HttpMethodBase request = null;     // Objeto para realizar las peticiines HTTP GET o POST
		int status = 0;         // C�digo de la respuesta HTTP       
		// Instanciamos el objeto
		httpClient = new HttpClient();
		// Invocamos por Get
		request = new GetMethod(TargetURL);        			

		
		// Indicamos reintente 3 veces en caso de que haya errores.
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		                                new DefaultHttpMethodRetryHandler(3, true));
		// Leemos el c�digo de la respuesta HTTP que nos devuelve el servidor
		status = httpClient.executeMethod(request);
		// Vemos si la petici�n se ha realizado satisfactoriamente
		if (status != HttpStatus.SC_OK) {
			System.out.println("Error\t" + request.getStatusCode() + "\t" + 
		                                      request.getStatusText() + "\t" + request.getStatusLine());	        	 
			return "mal";
		}
		return "bien";
	
	}
```

Therefore,  the returned message if url exist 
