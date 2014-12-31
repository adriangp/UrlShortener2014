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
    alert("Your URL: " + url); //ajax call here    
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