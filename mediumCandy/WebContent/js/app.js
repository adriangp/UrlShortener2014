var SERVICE_URI = "http://localhost:8080/";

/* Document Ready Functionality (jQuery stuff) */
$( document ).ready(function() { 
  setUrlSubmition();
});

/*
 * This function is called everytime the user submits the form.
 */
function setUrlSubmition() {
  $("form").on('submit', function (e) {
    var url = getUrl();  
    shortenURL(url);
    e.preventDefault(); //stop form submission
    clearUrlInput();
  });
}

/*
 * Returns the URL the user entered.
 */
function getUrl() {
  return $( '#urlInput' ).val();
}

function clearUrlInput() {
  $( '#urlInput' ).val('');
}


/* API CALLS */

/*
 * Shortens an URL.
 */
function shortenURL(url) {
  $.ajax({
    type : 'POST',
	contentType : 'application/json',
	url : SERVICE_URI + "linkreachable?url=" + url,
	dataType : "json",
	//data : url,		
	success : function(response) {      
      console.log(response);
      console.log("exito!");
    },    
    error : function(error) {
      console.log(error);
    }
  });
}