var list;

$(document).ready(function() {
	$("#csvupload").submit(function(event){
		event.preventDefault();
		var data = document.getElementById('csvfile');
		var formData = new FormData();
		var file = $("#csvfile")[0];
		var aux = file.files[0];
		if( typeof aux === 'undefined' || !/.+\.csv$/.test(aux.name)){
			alert("You have to submit a .csv file.");
		}else{
			formData.append("file",aux);
			$.ajax({
				type : "POST",
				url : "/upload",
				enctype : "multipart/form-data",
				data : formData,
				processData: false,
				contentType: false,
				success : function(msg) {
					list = "";
					$("#resultcsv").html("<table class='table table-bordered' id='csvtable'> <tr> <td>Url</td> <td>ShortenedUrl</td></tr></table>");
					var trHTML='';
					$.each(msg, function(index, value) {
						if(value.shortenedUrl != null)
							trHTML = '<tr><td>' + value.url + '</td><td><a target="_blank" href="'+value.shortenedUrl+'" class="btn btn-default btn-lg" role="button">'+value.shortenedUrl+'</a></td></tr>';
						else
							trHTML = '<tr class="danger"><td>' + value.url + '</td><td>Incorrect Url</td></tr>';
						list += value.url+','+value.shortenedUrl+'\n';

						$("#csvtable").append(trHTML);
					});
					$("#resultcsv").append('<input type="text" id="csvname" placeholder="filename"/>.csv <button type="button" onclick="save()" class="btn btn-lg btn-primary">Save</button>');
				},
				error : function() {
					alert("failure");
				}
			});
		}

	});
});

function save(){
	var val = $('#csvname').val().concat('.csv');
	if(val === '.csv'){
		alert("You have to choose a name for the file");
	}
	else{
		var blob = new Blob([list], {type: "text/plain;charset=utf-8"});
		saveAs(blob,val);
	}
}
