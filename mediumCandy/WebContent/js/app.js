var SERVICE_URI = "http://localhost:8080/";

/* Alert Messages */
var ALERT_SHORTEN_URL = "Unable to shorten that link. It is not a valid or reachable url.";

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

/* 
 * Shows the alert box with the given 'alertMessage' text in it, and 
 * automatically hides after 5 seconds - if isn't already visible.
 */
function showAlert(alertMessage) {
  if ( elementIsVisible( '#alert-box' ) ) {
    $( '#alert-box' ).html(alertMessage);
    $( '#alert-box' ).slideDown().delay(5000).slideUp();
  }
}

function elementIsVisible(element) {
  return $( element ).css('display') == 'none';
}

function showShortenedUri(objUri) {
  var shortenUri = objUri.uri;
  var targetUri = objUri.target;
  
  // DOM insertion
  $( '#shortened-url' ).text( '' );
  $( '#target-url' ).text( '' );
  $( '#shortened-url' ).html( '<a href="' + targetUri + '">' + shortenUri + '</a>');
  $( '#target-url' ).html( '<a href="' + targetUri + '">' + targetUri + '</a>');
  
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
      showAlert(ALERT_SHORTEN_URL);
      console.log("Oops! RESPONSE Status:  " + error.status);
    }
  });
}