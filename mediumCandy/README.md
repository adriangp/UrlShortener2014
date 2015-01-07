

# Project "MediumCandy"

The color medium candy apple red, applied with a metallic sheen, is popular among car owners who customize their cars.

# Team Members

* Carlos Mart铆nez Castillero (leader)
* Daniel Garc铆a Paez
* Alberto Berbel Aznar
* Guillermo Ginestra D铆az

# Funcionalidades implementadas

A continuaci贸n, se dar谩 una descripci贸n de cada una de las funcionalidades que se han implementado en este proyecto.

## Servicio que almacena URLs alcanzables

Se ha implementado un Servicio que se encarga de comprobar que una URL es alcanzable previamente a ser acortada y almacenada en el sistema. Tal y como se acord贸, el Servicio implementado se corresponde con el **Tipo 2 de implementaci贸n**.

El servicio principal que se encarga de responder a las peticiones se encuentra en el `MediumCandyController` y es accesible a trav茅s de la uri mostrada a continuaci贸n (siendo el par谩metro `url` la URL que se desea acortar): 

- `http://URI_DEL_SERVIDOR/mediumcandy/linkreachable?url=URL` 

El servicio responder谩 con: 

- `CREATED (201)` en el caso de que la URL proporcionada sea alcanzable (y correctamente formada, por supuesto).
- `BAD REQUEST (400)` en el caso de que la URL que se desea acortar no sea alcanzable (o en su defecto, no se trate de una URL correctamente formada).
 
La funcionalidad m谩s importante se encuentra implementada en el `UrlShortenerControllerWithLogs`, Servicio al que accede el `MediumCandyController` para poder dar respuesta a todas las peticiones que le llegan en `/mediumcandy/linkreachable`. Cabe destacar la funci贸n `private static boolean ping(String urlIn)` que determina si `urlIn` es una URL alcanzable. Aspectos destacables acerca de su implementaci贸n son:

- La utilizaci贸n de la clase `HttpURLConnection` que permite realizar peticiones *http*.
- La existencia de un *timeout* de varios segundos, tras el cual si no hemos recibido respuesta tras realizar una petici贸n cierra la conexi贸n y determina que la URL dada no es alzanzable.
- En el caso de obtener respuesta accedemos a sus cabeceras *http* y comprobamos que el c贸digo recibido es v谩lido y se trata por tanto de de una URL alcanzable.



## Acortamiento masivo de URLs mediante fichero CSV

## Servicio de personalizaci贸n de URLs acortadas

## Informaci贸n y estad铆sticas de URLs

Funcionalidad que consiste en mostrar estad韘ticias de las URLs almacenadas en la Base de Datos con su URL corta implementado con **nivel tecnol骻ico Tipo 2** como acordamos. 

Cada vez que alguien accede a una direcci髇 con una URL corta, se almacena en la Base de Datos. Para conocer las estad韘ticas de una direcci髇 basta con hacer una petici髇 get a la siguiente direcci髇 del servidor, donde URL es la direcci髇 del cual se quieren obtener las estad韘ticias e informaci髇: 

- `http://URI_DEL_SERVIDOR/mediumcandy/linkstats?url=URL`  

Esta direcci髇 accede al `MediumCandyController` la cual a su vez llama a `UrlShortenerControllerWithLogs`. Este controller se encarga de acceder a la Base de Datos para buscar el objeto ShortURL, y con el hash de este recuperar de la Base de Datos el n鷐ero de clicks (veces que se a accedido a la direcci髇 dada por par醡etro). 

Este m閠odo devuelve lo siguiente: 

- `OK (200)` junto con la lista de las estad韘ticias, en el caso de que todo haya ido bien y haya en la Base de Datos una ShortURL con la direcci髇 dada.  
- `BAD REQUEST (400)` en el caso de que la URL dada no este en la Base de Datos
