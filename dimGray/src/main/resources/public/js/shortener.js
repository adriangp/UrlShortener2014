$(document).ready(function() {
		$("#shortener").submit(function(event){
			event.preventDefault();
			$.ajax({
				type : "POST",
				url : "/link",
				data : $(this).serialize(),
				success : function(msg) {
					$("#result").html("<a target='_blank' id='short' href='"+msg.uri+"' class='btn btn-default btn-lg' role='button'>"+msg.uri+"</a>")
					$("#result").append('<button type="submit" class="btn btn-lg btn-primary">Generate QR code</button>');
					$("#result").append('<img id="QRImage" src= "" style="visibility:hidden" />');
				},
				error : function() {
					alert("Incorrect url.");
				}
			});
		});
});