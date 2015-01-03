var SERVICE_URI = "http://localhost:8080/";

/* Alert Messages */
var ALERT_SHORTEN_URL = "Unable to shorten that link. It is not a valid or reachable url.";
var ALERT_ALREADY_SHORTEN = "That is already a shortened link!";

/* Vars */
var shortenedUriList = [];
var menuSelected = "shortener";

/* Document Ready Functionality (jQuery stuff) */
$( document ).ready(function() { 
  setUrlSubmition();
  setMenuCallbacks();
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
 * Set's up the functionality to make the page dynamically navigable.
 */
function setMenuCallbacks() {
  $( '#link-shortener' ).on('click', function (e) {
    updateMenu('#link-shortener');
    updatePage('#shortener');
    menuSelected = "shortener";
    // show
    e.preventDefault();
  });
  
  $( '#link-csv' ).on('click', function (e) {
    updateMenu('#link-csv');
    updatePage('#csv');
    menuSelected = "csv";
    // show
    e.preventDefault();
  });
  
  $( '#link-branded' ).on('click', function (e) {
    updateMenu('#link-branded');
    updatePage('#branded');
    menuSelected = "branded";
    // show
    e.preventDefault();
  });
  
  $( '#link-stats' ).on('click', function (e) {
    updateMenu('#link-stats');
    updatePage('#stats');
    menuSelected = "stats";
    // show
    e.preventDefault();
  });
}

/*
 * Changes the menu link colors dynamically.
 */
function updateMenu(linkId) {
  var oldLinkId = '#link-' + menuSelected;
  // update menu css
  $( oldLinkId ).css( "color", "#FFF");
  $( linkId ).css( "color", "#E2062C");
}

/*
 * Hides the page showing and dynamically shows the new page
 * selected on the menu bar.
 */
function updatePage(pageId) {
  var oldPageId = '#' + menuSelected;
  if ( pageId != "#" + menuSelected ) {
    $( oldPageId ).hide();
    $( pageId ).slideDown();
  }
}

/*
 * Returns the URL the user entered.
 */
function getUrl() {
  return $( '#urlInput' ).val();
}

/*
 * Sets the content of the '#urlInput' input to the shortened
 * uri contained inside the 'objectUri' object.
 */
function setUrlInput(objectUri) {
  var shortenedUri = objectUri.uri;
  $( '#urlInput' ).val( shortenedUri );
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

/*
 * Returns TRUE if the element 'element' is visible in the DOM, and
 * FALSE if it is hidden.
 */
function elementIsVisible(element) {
  return $( element ).css('display') == 'none';
}

/*
 * Shows the given 'objUri' object in the DOM.
 */
function showShortenedUri(objUri) {
  var shortUri = objUri.uri;
  var targetUri = objUri.target;
  var shortenedUri = {uri: shortUri, target:targetUri};
  
  // DOM insertion
  insertShortenedUriInDOM(shortenedUri);
}

/* 
 * Inserts the given shortenedUri object in the DOM.
 */
function insertShortenedUriInDOM(shortenedUri) {
  // 1. Insert to latest uri details block
  insertLatestShortenedUriInDOM(shortenedUri);
  
  // 2. If there were other uris shortened before, prepend the one
  //    shown in latest uri details block to the list of shortened uris
  if ( shortenedUriList.length > 0 ) {
    // if the list is empty, it will be hidden, we have to show it
    if ( shortenedUriList.length == 1 ) {
      $( '.shortened-url-block' ).show();
    }
    // 2.1. get first array elem (we need it!)
    var latest = shortenedUriList.shift();
    // and insert it where it was ('shift()' method deleted it! from the array)
    shortenedUriList.unshift(latest);
    console.log(latest);
    // 2.2. update the shortened uris list (DOM)
    prependLatestShortenedUriInDOM(latest);
  }
  
  // 3. add new shortened uri to the array of shortened uris
  shortenedUriList.unshift(shortenedUri);
}

/*
 * Inserts the HTML of the given shortenedUri object in the DOM, inside
 * the list of already shortened URLs.
 */
function prependLatestShortenedUriInDOM(shortenedUri) {
  var uri = $(  '<div class="shorten-url-elem">' +
                  '<div class="img-block">' +
                    '<img src="/img/href_small.png">' +
                  '</div>' +
                  '<div class="details-block">' +
                    '<div class="shortened-url">' + shortenedUri.uri + '</div>' +
                    '<div class="target-url"><a target="_blank" href="' + shortenedUri.target + '">' + shortenedUri.target + '</a></div>' +
                  '</div>' +
                '</div><br>');
  
  $( '.shortened-url-block' ).prepend( uri );
}

/*
 * Inserts the HTML of the given shortenedUri object, inside
 * the latest shortened URL block.
 */
function insertLatestShortenedUriInDOM(shortenedUri) {
  var uri = $(  '<div class="shorten-url-elem">' +
                  '<div class="img-block">' +
                    '<img src="/img/href.png">' + 
                  '</div>' +
                  '<div class="details-block">' +
                    '<div class="shortened-url">' + shortenedUri.uri + '</div>' +
                    '<div class="target-url"><a target="_blank" href="' + shortenedUri.target + '">' + shortenedUri.target + '</a></div>' +
                  '</div>' +
                '</div><br>');
  
  $( '.shorten-url-block' ).html( uri );
  // animation when shown! :-)
  $( '.shorten-url-block' ).hide();
  $( '.shorten-url-block' ).slideDown();
}

/*
 * Selects the content of the #urlInput input.
 */
function selectUserInput() {
  $( '#urlInput' ).select();
}

/*
 * Returns TRUE if the given URL 'url' is already a shortened link,
 * and TRUE if it is not.
 */
function isShortenUri(url) {
  var subUri = url.substring(0, 22);
  return subUri == SERVICE_URI;
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
	success : function(response) {
      // update DOM with response data
      showShortenedUri(response);
      // things to do after call
      setUrlInput(response);
      selectUserInput();
      console.log('exito!');
    },    
    error : function(error) {
      if ( isShortenUri(url) ) {
        showAlert(ALERT_ALREADY_SHORTEN);
      } else {
        showAlert(ALERT_SHORTEN_URL);
      }
      console.log("Oops! RESPONSE Status:  " + error.status);
    }
  });
}