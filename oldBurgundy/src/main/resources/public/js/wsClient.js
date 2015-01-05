$(document).ready(function() {
	$("#sponsor").height($(window).height() - $("#header").height());
	
	$("#sponsor").width($(window).width());
	$("#header").width($(window).width());
	
	$("#text").css("padding-left", $("#sponsor").width() - $("#text").width() - 20 + "px");
		
	$(window).resize(function(){
		$("#sponsor").height($(window).height() - $("#header").height());
	});
	
	connect();
	
	//setInterval(suspension_points, 200);
});

var iteration = 3;
function suspension_points(){
	var points = "";
	for(var i = 0; i < iteration; i++){
		points = points + ". ";
	}
	iteration++;
	if(iteration > 3){
		iteration = 0;
	}
	$("#dots").text(points);
}
	
function connect(){
	var socket;
	var host = "ws://localhost:8080/sponsor/ws";

	try{
		var socket = new WebSocket(host);

		socket.onopen = function(){
			socket.send($('#link').val());
		}

		socket.onmessage = function(msg){
			window.location = msg.data;
		}

		socket.onclose = function(){
			//message('<p class="event">Socket Status: '+socket.readyState+' (Closed)');
		}			

	} catch(exception){
		//message('Error: ' + exception);
	}
	/*
	function message(msg){
		$('#chatLog').append('<p>' + msg + '</p>');
	}*/
}