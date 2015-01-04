$(document).ready(function() {
	connect();
});
	
function connect(){
	var socket;
	var host = "ws://localhost:8080/sponsor/ws";

	try{
		var socket = new WebSocket(host);

		socket.onopen = function(){
			var text =  $('#text').val();
			socket.send(text);
		}

		socket.onmessage = function(msg){
			window.location = msg.data;
		}

		socket.onclose = function(){
			//message('<p class="event">Socket Status: '+socket.readyState+' (Closed)');
		}			

	} catch(exception){
		message('Error: ' + exception);
	}
	
	function message(msg){
		$('#chatLog').append('<p>' + msg + '</p>');
	}

	$('#send').click(function(event) {
		send();
	});	

}//End connect