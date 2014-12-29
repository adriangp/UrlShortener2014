<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AJAX JSP Servelts</title>
<script src="http://code.jquery.com/jquery-latest.js">
	
</script>
<script>
	$(document).ready(function() {
		$('#submit').click(function(event) {
			var fichero = $('#nombre').val();
			// Si en vez de por post lo queremos hacer por get, cambiamos el $.post por $.get
			ver = document.getElementById("respuesta");
			$.post(
				'Funciones', 				//Destino del Post
				{file : fichero},			//Lista con los parametros
				function(responseText){		//Funcion que se ejecuta al acabar el servlet
					ver.innerHTML = fichero;
				});
		});

	});
</script>
</head>

<body>
	<h2>Ejemplo de AJAX con JSP y Servelts</h2>
	<form id="form1" >
		Nombre:<input type="file" id="nombre" /> <br> 
		<input type="button" id="submit" value="Añadir" />
	<br>
	<div id="respuesta"></div>
	<div id="res2"></div>
</body>
</html>
