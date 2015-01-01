var SERVICE_URI = "http://localhost:8080/";

/* Document Ready Functionality (jQuery stuff) */
$( document ).ready(function() { 
  setUrlSubmition();
});

/*
 * This function is called everytime the user submits the form.
 */
function setUrlSubmition() {
  // jQuery way!
  $("form").on('submit', function (e) {
    var url = getUrl();  
    
    if ( ! emptyUserInput(url) ) {
      shortenURL(url);
      e.preventDefault(); //stop form submission
    }
  });
}

/*
 * Returns the URL the user entered.
 */
function getUrl() {
  return $( '#urlInput' ).val();
}

/*
 * Clears all text in #urlInput.
 */
function clearUrlInput() {
  $( '#urlInput' ).val('');
}

/*
 * Returns TRUE if 'input' is an empty String.
 */
function emptyUserInput(input) {
  return input == "";
}

function showShortenedUri(objUri) {
  var shortenUri = objUri.uri;
  var targetUri = objUri.target;
  // DOM insertion
  $( '#shortened-url' ).text( '' );
  $( '#target-url' ).text( '' );
  $( '#shortened-url' ).html( '<a href="' + targetUri + '">' + shortenUri + '</a>');
  $( '#target-url' ).text( targetUri );
  // animation when shown! :-)
  $( '.shorten-url-block' ).slideDown();
}


/* API CALLS */

/*
 * Shortens an URL.
 */
function shortenURL(url) {
  $.ajax({
    type : 'POST',
	contentType : 'application/json',
	url : SERVICE_URI + "link?url=" + url,
	dataType : "json",
	//data : url,		
	success : function(response) {
      // update DOM with response data
      showShortenedUri(response);
      // things to do after call
      clearUrlInput();
      console.log('exito!');
    },    
    error : function(error) {
       console.log("Oops! RESPONSE Status:  " + error.status);
    }
  });
}