$(document).ready(function() {

      //The user has WebSockets
			$('#send').attr("disabled", true);
		      connect();
	
	
      function connect(){
          var socket;
          var host = "ws://localhost:8080/sponsor/ws";
		  //var host = "ws://localhost:8080/csv/ws";


          try{
              var socket = new WebSocket(host);

              message('<p class="event">Socket Status: '+socket.readyState);

              socket.onopen = function(){
				var text =  $('#text').val();
             	 socket.send(text);
              }

              socket.onmessage = function(msg){
				 window.location=msg.data;
             	 message('<p class="message">Received: '+msg.data);
              }

              socket.onclose = function(){
              	message('<p class="event">Socket Status: '+socket.readyState+' (Closed)');
              }			

          } catch(exception){
             message('<p>Error'+exception);
          }

          function send(){
              var text =  $('#text').val();

              try{
                  socket.send(text);
                  message('<p class="event">Sent: '+text)

              } catch(exception){
                 message('<p class="warning">');
              }
          }
		
          function message(msg){
            $('#chatLog').append(msg+'</p>');
          }

          $('#send').click(function(event) {
                send();
          });	

      }//End connect

});