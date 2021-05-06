// Globals
var map, 		// the map
	coords, 
	markers, 
	points, 
	track, 		// Polyline that represents the track
	freq=100 ;		// Sampling frequency

// Auxiliary function that sends an XMLHTTPREQUEST to load the contents of an external resource
// This function works across different browsers (namely, it should work with IE)
function LoadXMLDoc(dname, callback){
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    }
    else {
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.overrideMimeType('text/xml');
    xhttp.onreadystatechange = function() {
        if(this.readyState == 4 && this.status == 200) {
            callback(xhttp.responseXML);
        }
    }
    xhttp.open("GET", dname, true);
    xhttp.send();
}

// Convenience functions to access XML node data
function GetTextForFirstElementNamed(elem, name) {
	return elem.getElementsByTagName(name)[0].childNodes[0].nodeValue;
}

function GetAttrNamed(elem, attrName) {
	return elem.attributes.getNamedItem(attrName).nodeValue;
}



// Functions to compute distance between two points on earth's surface
Rad = function(x) {return x*Math.PI/180;}

DistHaversine = function(p1, p2) {
  var R = 6371; // earth's mean radius in km
  var dLat  = Rad(p2.lat() - p1.lat());
  var dLong = Rad(p2.lon() - p1.lon());

  var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
          Math.cos(Rad(p1.lat())) * Math.cos(Rad(p2.lat())) * Math.sin(dLong/2) * Math.sin(dLong/2);
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  var d = R * c;

  return d;
}


// Function that clears the map. We need to go through all the layers (markers and polyline)
// and, for each object, set their map to null.
function ClearMap() 
{
	if(markers) {
		for(i in markers)
			markers[i].setMap(null);
	}
	
	if(track) track.setMap(null);
	
	// reset global arrays
    points = [];
    markers = [];
    coords = [];
}


// Point "constructor
function Point(lat, lon, ele, time) {
	this.latitude = lat;
	this.longitude = lon;
	this.elevation = ele;
	this.time = time;
	this.toString = function () {
		return this.latitude + " " + this.longitude + " " + this.elevation + " " + this.time;
	}
	
	this.lat = function () { return parseFloat(this.latitude); }
	this.lon = function () { return parseFloat(this.longitude); }	
	this.ele = function () { return parseFloat(this.elevation); }
	
	this.distanceFrom = function (other) {
		return DistHaversine(this, other);
	}
	this.timeFrom = function (other) {
		return 1;
	}
	this.climbFrom = function (other) {
		return Math.max(0,parseFloat(this.elevation) - parseFloat(other.elevation));
	}
}



// Statistical functions needed for info windowss
function ComputeDistance(idx) {
	var total = 0;
	for(var i=1; i<idx; i++) {
		total += points[i].distanceFrom(points[i-1]);
	}
	return total;
}

function ComputeTotalClimb(idx) {
	var total = 0;
	for(var i=1; i<idx; i++) {
		total += points[i].climbFrom(points[i-1]);
	}
	return total;
}




function PopulateMap() {

	for(var i=0; i<points.length; i++) {
		var loc = new google.maps.LatLng(points[i].lat(), points[i].lon());
	 	var m = new google.maps.Marker( { position: loc, map: map, title: "Sample " + (i+1) } );

		coords.push(loc);		
		markers.push(m);				
		addListener(m, i);
	}
	
	// Build polyline with gps coordinates
	track = new google.maps.Polyline(
		{ path: coords, strokeColor: "#FF0000", strokeOpacity: 0.6, strokeWeight: 8 }
	);
	
	// Associate track with the map
	track.setMap(map);
}

function addListener(m, i) {

	var infowindow = new google.maps.InfoWindow( { content: m.getPosition().toString(), index: i });

	google.maps.event.addListener(m, 'click', function () {
			infowindow.setContent(
				"<strong>" + "Sample " + infowindow.index + "</strong><br />"
				+ "<strong>Position:</strong> " + points[i].lat() + " " + points[i].lon() + "<br />"
				+ "<strong>Elevation:</strong> " + points[i].elevation + "m<br />"
				+ "<strong>Time:</strong> " + points[i].time + "<br />"
				+ "<strong>Distance: </strong>" + ComputeDistance(infowindow.index).toFixed(3) + "km<br />"
				+ "<strong>Total Climb:</strong> " + ComputeTotalClimb(infowindow.index).toFixed(3) + "m<br />"
			);
	
			infowindow.open(map, m);
		} );
}



// Adds a point to an array
function AddPoint(library, elem) {		
	points.push( new Point(
			parseFloat(GetAttrNamed(elem, "lat")),
			parseFloat(GetAttrNamed(elem, "lon")), 
			GetTextForFirstElementNamed(elem, "ele"),
			GetTextForFirstElementNamed(elem, "time")
		));
}

function LoadData(url){

	// Clear previous content
	ClearMap();
    
	// Load the GPX (XML) content
	var xmlDoc = LoadXMLDoc(url, displayData);
}

function displayData(xmlDoc) { 

	// Get array with all the "trkpt" elements in the loaded document
	var pts = xmlDoc.getElementsByTagName("trkpt"); 

	if(pts.length == 0) {
		alert("Ficheiro inválido ou sem pontos");
		return;
	}
	
	var v = Math.min(100, Math.max(1,freq));
	var incr = 100.0/v;

	for(var i=0; i<pts.length-1; i+=incr) {
		AddPoint(pts, pts.item(i));
	}	
	AddPoint(pts, pts.item(pts.length-1));

    PopulateMap();
}


// mais funções TODO

function initMap() {
// Init globals
    
// Create map
    var center = new google.maps.LatLng(38.660998431780286, -9.204448037385937) ;
    var mapOptions = {
        zoom: 12,
        center: center,
        mapTypeId: google.maps.MapTypeId.SATELLITE
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
// Install map listeners
    google.maps.event.addListener(map, 'rightclick', function(event) {
        // TODO: Center map. The map should be centered at the center of the track bounding box
    });
    LoadData('data/track.gpx');
}