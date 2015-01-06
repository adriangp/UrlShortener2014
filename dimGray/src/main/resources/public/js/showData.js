$(document).ready(function() {
		$("#showData").submit(function(event){
			event.preventDefault();	
			$.ajax({
				type : "POST",
				url : "/showInfo",
				success : function(message) {
					var trHTML='';
					$("#modify").html("<table class='table table-bordered' id='infoTable'> <tr> <td>Mod</td><td>Del</td><td>Url</td><td>Uri-Id</td><td>Sponsor-Created</td><td>Created-Referrer</td><td>Owner-Browser</td><td>Mode-Platform</td><td>Ip</td><td>Country</td></tr></table>");
					
					$.each(message, function(index, value) {
						if (value.isUrl == 1) {
							var val = "url "+value.hash;
							$("#infoTable").append('<tr><td><input id="mod" type="radio" name="radMod" value="'+val+'"></td><td><input id="mod" type="radio" name="radDel" value="'+val+'"></td><td>'+value.target+'</td><td>'+value.uri+'</td><td>'+value.sponsor+'</td><td>'+value.urlCreated+'</td><td>'+value.owner+'</td><td>'+value.mode+'</td><td>'+value.urlIp+'</td><td>'+value.urlCountry+'</td></tr>');
						}
						else  {
							var val = "click "+value.id+" "+value.hash+" "+value.clickCreated+" "+value.referrer+" "+value.browser+" "+value.platform+" "+value.clickIp+" "+value.clickCountry;
							$("#infoTable").append('<tr><td><input type="radio" name="radMod" value="'+val+'"></td><td><input type="radio" name="radDel" value="'+val+'"></td><td></td><td>'+value.id+'</td><td>'+value.clickCreated+'</td><td>'+value.referrer+'</td><td>'+value.browser+'</td><td>'+value.platform+'</td><td>'+value.clickIp+'</td><td>'+value.clickCountry+'</td><td></td></tr>');
						}
					});
					
					$("#modify").append('Modify - <input id="mod" type="radio" name="modOrDel" value="modify">   <input type="text" name="campo" value="Introduce el campo">    <input type="text" name="valor" value="Introduce el valor"><br/>');
					$("#modify").append('Delete - <input id="mod" type="radio" name="modOrDel" value="delete"><br/>');
					$("#modify").append('<button type="submit" class="btn btn-lg btn-primary">Modify</button>');
				},
				error : function() {
					alert("failure");
				}
				
			});
		});
});