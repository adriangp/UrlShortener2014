# Project "DimGray"

Dim Gray is a dark tone of gray used in the X11 palette.

# Team Members

* Carlos Ivan Pinos (leader)
* Paulo Pizarro

#Funcionalidades

1. Servicio que muestra las url y los clicks de la base de datos. Además permite el borrado de clicks u urls y la edición de urls.
   -Funcionalidad de tipo 2 : Implementada
2. Servicio que devuelve un código qr con una url acortada.
  -Funcionalidad de tipo 2 : Implementada
  -Se hace una petición al servidor con la url acortada de la que se quiere la imagen.
  -El servidor manda una petición al servicio de google para que devuelva la imagen como byte[].
  -Se codifica la imagen en base64 y se envía al cliente para que la muestre.
3. Servicio que permite subir un fichero .csv con una url por línea y devuelve una lista de dichas urls con sus urls acortadas.
  -Funcionalidad de tipo 2 : Implementada
  - Se hace una petición al servidor con el fichero de urls.
  - El servidor procesa el fichero y va asignando cada url a un thread pool para que mande una petición al servicio acortador.
  - Finalmente, se devuelve una lista que se muestra en el cliente permitiendo guardarla en un fichero posteriormente.

Enlace al javadoc del proyecto: https://github.com/ivanpinos/UrlShortener2014/tree/master/dimGray/doc

Los test se pueden realizar automaticamente mediante jUnit.
