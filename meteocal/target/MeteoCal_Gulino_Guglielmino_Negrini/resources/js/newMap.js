/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var map;
var newEvent = false;
var updateEvent=false;
var checked=false;
var repeatEvent=false;

// Initialize and display a google map
function Init(mapDivName)
{
    // Create a Google coordinate object for where to initially center the map
    var latlng = new google.maps.LatLng( 45.4773, 9.1815 );	// Milano,MI
    // Map options for how to display the Google map
    var mapOptions = { zoom: 10, center: latlng  };
    // Show the Google map in the div with the attribute id 'map-canvas'.
    map = new google.maps.Map(document.getElementById(mapDivName), mapOptions);
}

// Update the Google map for the user's inputted address
function UpdateMap(formName, mapDivName, cityField, streetField, numberField, coordField)
{
    Init(mapDivName);
    var geocoder = new google.maps.Geocoder();    // instantiate a geocoder object

    // Get the user's inputted address
    var city=document.getElementById(formName+":"+cityField).value;
    var address_name="";
    var number="";
    if(newEvent || updateEvent){
        address_name=document.getElementById(formName+":"+streetField).value;
        number=document.getElementById(formName+":"+numberField).value;
    }
    var address = city+", "+address_name+", "+number;
    // Make asynchronous call to Google geocoding API
    geocoder.geocode( { 'address': address }, function(results, status) {
            var addr_type = results[0].types[0];	// type of address inputted that was geocoded
            if ( status == google.maps.GeocoderStatus.OK ){ 
                    ShowLocation( results[0].geometry.location, address, addr_type );
                    var latitude=results[0].geometry.location;
                    document.getElementById(formName+":"+coordField).value=latitude+" ";
                }
            else     
                    alert("Geocode was not successful for the following reason: " + status);        
    });
}

// Show the location (address) on the map.
function ShowLocation( latlng, address, addr_type )
{

    // Center the map at the specified location
    map.setCenter( latlng );

    // Set the zoom level according to the address level of detail the user specified
    var zoom = 12;
    switch ( addr_type )
    {
    case "administrative_area_level_1"	: zoom = 6; break;		// user specified a state
    case "locality"						: zoom = 10; break;		// user specified a city/town
    case "street_address"				: zoom = 15; break;		// user specified a street address
    }
    map.setZoom( zoom );

    // Place a Google Marker at the same location as the map center 
    // When you hover over the marker, it will display the title
    var marker = new google.maps.Marker( { 
            position: latlng,     
            map: map,      
            title: address
    });

    // Create an InfoWindow for the marker
    var contentString = "" + address + "";	// HTML text to display in the InfoWindow
    var infowindow = new google.maps.InfoWindow( { content: contentString } );

    // Set event to display the InfoWindow anchored to the marker when the marker is clicked.
    google.maps.event.addListener( marker, 'click', function() { infowindow.open( map, marker ); });
}

// Call the method 'Init()' to display the google map when the web page is displayed ( load event )
//google.maps.event.addDomListener( window, 'load', Init );

function manageAddress(formName, mapDivName, cityField, streetField, numberField, coordField) {
    var el = document.getElementById(formName+":"+cityField).value;
    if( el == "") {
        
    } else {
        UpdateMap(formName, mapDivName, cityField, streetField, numberField, coordField);
    }
}

function showMap(formName, mapDivName, cityField, streetField, numberField, coordField) {
    $("#"+mapDivName).show();
    manageAddress(formName, mapDivName, cityField, streetField, numberField, coordField);
}