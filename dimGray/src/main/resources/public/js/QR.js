$(document).ready(function() {
		$("#result").submit(function(event){
			event.preventDefault();
			var data = document.getElementById('short');
			$.ajax({
				type : "GET",
				url : "/qr",
				data : "url="+data.text,
				success : function(msg) {
					var field = document.getElementById('QRImage');
					field.src = "data:image/png;base64,"+msg;
					field.style.visibility="visible";
				},
				error : function() {
					alert("failure");
				}
			});
		});
});
