$(document).ready(function() {
		$("#modify").submit(function(event){
			event.preventDefault();
			$.ajax({
				type : "GET",
				url : "/modify",
				data : $('#modify').serialize(),
				success : function() {				

				},
				error : function() {
					alert("Incorrect url.");
				}
			});
		
			
		});
});