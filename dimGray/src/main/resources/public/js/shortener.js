$(document).ready(function() {
		$("#shortener").submit(function(event){
			event.preventDefault();
			$.ajax({
				type : "POST",
				url : "/link",
				data : $(this).serialize(),
				success : function(msg) {
					$("#result").html("<a target='_blank' href='"+msg.uri+"' class='btn btn-default btn-lg' role='button'>"+msg.uri+"</a>")
				},
				error : function() {
					alert("failure");
				}
			});
		});
});