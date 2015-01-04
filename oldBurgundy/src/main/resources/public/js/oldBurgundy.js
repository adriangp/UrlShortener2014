var socket;
var recive = 0;
var longURLs = [];
var sponsors = [];
var shortURLs = [];

$(document).ready(function() {	
	$("#shortener").submit(function(event){
		event.preventDefault();
		if($("#csvFile").get(0).files[0] !== undefined){
			$("#csvFile").attr("disabled","");
			$("#btn_submit").attr("disabled","");
			readFile($("#csvFile").get(0).files);
		}
	});	
});

function createTable(){
	$("#result").html(	"<table class='table'>" + 
							"<thead><tr><th width='30%'>URL</th><th width='30%'>SPONSOR</th><th width='40%'>SHORT URL</th></tr></thead>" +
							"<tbody></tbody>" +
						"</table>");
}

function addRow(id, url, sponsor){
	$("#result").find("tbody").append("<tr class='active' id='" + id + "'><td width='30%'>" + url + "</td><td width='30%'>" + sponsor + "</td><td class='shortURL' width='40%'></td></tr>");
}

function updateRow(message){
	if(message[0] == "error"){
		$("#result").find("#" + message[1]).removeClass("active").addClass("danger");
		$("#result").find("#" + message[1]).find(".shortURL").append("Error: " + message[2]);
	}
	else if(message[0] == "shortUrl"){
		$("#result").find("#" + message[1]).removeClass("active").addClass("success");
		$("#result").find("#" + message[1]).find(".shortURL").append("<a target='_blank' href='" + message[2] + "' class='btn btn-default btn-lg' role='button' style='width: 80%'>" + message[2] + "</a>");
	} 
}

function connect(){
	var host = "ws://localhost:8080/csv/ws";

	try{
		var socket = new WebSocket(host);

		socket.onopen = function(){
			for(var i in longURLs){
				if(sponsors[i].length == 0){
					send(i, longURLs[i]);
				}
				else{
					send(i, longURLs[i] + ", " + sponsors[i]);
				}
			}
		}

		socket.onmessage = function(msg){
			var message = msg.data.split("::");
			
			updateRow(message);
			
			shortURLs[message[1]] = message[2];
			
			recive++;
			if(recive == longURLs.length){
				$("#result").append("<button class='btn btn-primary' id='btn_download'>Download file</button>");
				$("#btn_download").click(function(){
					downloadFile(generateText(), "shortURL.csv");
				});
			}
		}

		socket.onclose = function(){
			//message('<p class="event">Socket Status: '+socket.readyState+' (Closed)');
		}			

	} catch(exception){
		message('Error: ' + exception);
	}

	function send(id, line){	
		socket.send(id + ", " + line);
	}
}

function readFile(files){
	var reader = new FileReader();
	
	reader.onload = function(e){
		createTable();
		var lines = e.target.result.split("\n");
		for(var i in lines){
			var line = lines[i].split(",");
			longURLs[i] = line[0];
			if(line[1] == undefined){
				addRow(i, line[0], "");
				sponsors[i] = "";
			}
			else{
				sponsors[i] = line[1];
				addRow(i, line[0], line[1]);
			}
		}
		connect();
	};
	
	reader.readAsText(files[0]);
}

function downloadFile(blob, fileName) {
    var reader = new FileReader();
	
    reader.onload = function (event) {
        var save = document.createElement('a');
        save.href = event.target.result;
        save.target = '_blank';
        save.download = fileName || 'shortURL.csv';
		
        var clicEvent = new MouseEvent('click', {
            'view': window,
			'bubbles': true,
			'cancelable': true
        });
		
        save.dispatchEvent(clicEvent);
		
        (window.URL || window.webkitURL).revokeObjectURL(save.href);
    };
	
    reader.readAsDataURL(blob);
};

function generateText() {
    var text = [];
	for(var i in shortURLs){
		text.push(longURLs[i].trim());
		text.push(', ');
		text.push(sponsors[i].trim());
		text.push(', ');
		text.push(shortURLs[i].trim());
		text.push('\n');
	}
    return new Blob(text, {
        type: 'text/plain'
    });
};
	/*
function message(msg){
	$('#chatLog').append('<p>' + msg + '</p>');
}*/