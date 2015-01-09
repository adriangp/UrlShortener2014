<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sponsored link</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.0.3/css/bootstrap.min.css" />
<style type="text/css">
html, body {
	position: fixed;
	width: 100vw;
	height: 100vh;
}

body {
	overflow: hidden;
	margin: 0;
	padding: 0;
}

button {
	float: right;
}

iframe {
	width: 100%;
	height: 100%;
}

#wrapper {
	min-height: 100%;
	height: auto !important;
	height: 100vh;
}

div {
	height: 100vh;
}

#message {
	height: 100%;
}

#form {
	height: 8vh;
}

#separator {
	height: 2vh;
}

#sponsor {
	height: 80vh;
}
</style>

</head>
<body>
	<script type="text/javascript" src="webjars/jquery/2.0.3/jquery.min.js"></script>
	<div id="wrapper">
		<div class="jumbotron">
			<div id="message" class="alert alert-danger" role="alert">
				Warning, this link may not be safe, enter only at your own risk <a
					href="<%out.print(request.getAttribute("target"));%>"
					class='btn btn-default btn-lg'>Go</a>
			</div>
		</div>
	</div>
	<script type="text/javascript"
		src="webjars/bootstrap/3.0.3/js/bootstrap.min.js"></script>
</body>
</html>