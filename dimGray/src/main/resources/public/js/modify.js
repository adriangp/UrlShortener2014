$(document).ready(function() {
		$("#modify").submit(function(event){
			event.preventDefault();
			$.ajax({
				type : "GET",
				url : "/modify",
				data : $('#modify').serialize(),
				success : function() {				
					document.forms['modify'].submit();
				},
				error : function() {
					alert("Error al modificar o borrar.");
				}
				
			});
		
			
		});
});