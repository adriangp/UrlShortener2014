$(document).ready(function() {
		$("#showData").submit(function(event){
			event.preventDefault();	
			$.ajax({
				type : "GET",
				url : "/showInfo",
				success : function(msg) {
					var trHTML='';
					$("#modify").html("<table class='table table-bordered' id='infoTable'> <tr> <td>Mod</td><td>Del</td><td>Url</td><td>Uri-Id</td><td>Sponsor-Created</td><td>Created-Referrer</td><td>Owner-Browser</td><td>Mode-Platform</td><td>Ip</td><td>Country</td></tr></table>");
					var message = msg.infoDBList;
					$.each(message, function(index, value) {
						
						if (value.isUrl == 1) {
							var hash = value.hash, target = value.target, uri = value.uri, sponsor = value.sponsor, created = value.urlCreated, owner = value.owner, mode = value.mode,safe = value.safe, ip = value.urlIp, country = value.urlCountry;
							
							if (uri == null)	uri = "-";
							if (sponsor == null)	sponsor = "-";
							if (owner == null)	owner = "-";
							if (country == null)	country = "-";
							
							var val = "url "+hash+" "+target+" "+uri+" "+sponsor+" "+created+" "+owner+" "+mode+" "+safe+" "+ip+" "+country;
							$("#infoTable").append('<tr><td><input id="mod" type="radio" name="radMod" value="'+val+'"></td><td><input id="mod" type="radio" name="radDel" value="'+val+'"></td><td>'+target+'</td><td>'+uri+'</td><td>'+sponsor+'</td><td>'+created+'</td><td>'+owner+'</td><td>'+mode+'</td><td>'+ip+'</td><td>'+country+'</td></tr>');
						}
						else  {
							var id = value.id, hash = value.hash, created = value.clickCreated, referrer = value.referrer, browser = value.browser, platform = value.platform, ip = value.clickIp, country = value.clickCountry;
							var val = "click "+id+" "+hash+" "+created+" "+referrer+" "+browser+" "+platform+" "+ip+" "+country;
							if (referrer == null)	referrer = "-";
							if (browser == null)	browser = "-";
							if (platform == null)	platform = "-";

							if (country == null)	country = "-";
							$("#infoTable").append('<tr><td></td><td><input type="radio" name="radDel" value="'+val+'"></td><td></td><td>'+id+'</td><td>'+created+'</td><td>'+referrer+'</td><td>'+browser+'</td><td>'+platform+'</td><td>'+ip+'</td><td>'+country+'</td><td></td></tr>');
						}
					});
					
					$("#modify").append('Modify - <input id="mod" type="radio" name="modOrDel" value="modify">   <input type="text" name="campo" value="Introduce el campo">    <input type="text" name="valor" value="Introduce el valor"><br/>');
					$("#modify").append('Delete - <input id="mod" type="radio" name="modOrDel" value="delete"><br/>');
					$("#modify").append('<button id="show" type="submit" class="btn btn-lg btn-primary">Modify</button>');
				},
				error : function() {
					alert("failure");
				}
				
			});
		});
});