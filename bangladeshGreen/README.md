Miembros del equipo BangladeshGreen:
	Alberto Blasco(Líder)
	Óscar González
	Roberto Gotor

Funcionalidades:
	1. Servicio que dado las cabeceras HTTP de una redirección es capaz de identificar desde qué navegador se hace la petición (Explorer, Chrome, etc.) y desde qué plataforma (Windows, OSX, Linux). -> Tipo 1 
	IMPLEMENTADA 
		-Se hace uso de la cabecera “User-Agent” disponible en las peticiones HTTP al redirigir la URL acortada.
	
	
	2. Servicio que comprueba una URL contra el servicio Google Safe Browsing -> Tipo 2 
	IMPLEMENTADA
		-Se llama a la API de Google mediante Rest con petición Get.
		-La respuesta se guarda en un objeto Response
		-Se mira el código de respuesta para ver si la URL es segura o no.
		-Se ha optado por mantener la URL como no segura durante todo el ciclo de vida del proyecto.

	
	3. Servicio que permite subir un CSV con todas las URL que se desean acortar y que devuelve un CSV con que contiene para cada URL su URL acortada -> Tipo 2 
	IMPLEMENTADA
		-Para subir el fichero CSV a nuestro servidor se hace uso de JavaScript y del objeto XMLHttpRequest para que sea asíncrono.
		-Se sube mediante Rest con petición POST
		-Se analiza el fichero y se crea otro CSV únicamente con las URL acortadas
		-Al acortar, si hay alguna URL no válida, se filtra mediante los códigos de estado del objeto Response. 
		
Enlace al javadoc del proyecto que contiene las especificaciones de la clase UrlShortenerControllerWithLogs(GitHub de Alberto Blasco):
	https://github.com/alberto-648702/UrlShortener2014/blob/master/bangladeshGreen/doc/urlshortener2014/bangladeshgreen/web/UrlShortenerControllerWithLogs.html
	
	
Test de las funcionalidades:
	1.  -Ejecutar el proyecto BangladeshGreen con 'gradle run', una vez este en funcionamiento el servicio, se podrá hacer uso de las direcciones creadas con el mismo, al acceder a una de ellas, se redirige a la URL correspondiente, las cabeceras serán guardadas en la base de datos, para comprobarlo basta con mirar la terminal donde se ha ejecutado el proyecto, dado que se muestra dicha información.
		-Ejecutar con junit y comprobar en el log que se han cogido las cabeceras correctas

	2. 	-Ejecutar el proyecto BangladeshGreen con 'gradle run', una vez este en funcionamiento el servicio, acceder con el navegador web a la direccion: "http://localhost:8080", en la pestaña de acortar URL introducir la URL a acortar y hacer click en el botón, si la URL introducida es de un sitio web no seguro, antes de mostrar la URL acortada se mostrará un mensaje de advertencia en en navegador, haciendo click sobre el enlace acortado, se redirige automaticamente a dicha dirección.
		-Ejecutar con junit. Se realiza un test para comprobar que una URL segura es segura y que una no segura es no segura.
	
	3. 	Ejecutar el proyecto BangladeshGreen con 'gradle run', una vez este en funcionamiento el servicio, acceder con el navegador web a la direccion: "http://localhost:8080", en la pestaña de Subir CSV, arrastrar o hacer click en el boton central para seleccionar el archivo, apareceran los datos del fichero seleccionado y un boton de UPLOAD, al hacer click sobre el, el servicio mostrara un mensaje si el fichero se ha subido correctamente, si ha habido algun error de conexion o el fichero esta mal formado.
		En caso de que el fichero este bien formado y no haya problemas cargandolo, tras el mensaje de confirmación aparecera un botón de DESCARGA, haciendo click sobre el, comenzara la descarga del fichero de enlaces acortados.
		
	