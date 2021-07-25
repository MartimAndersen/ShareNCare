document.getElementById("eventOrigin").style.visibility = "hidden";
document.getElementById("distanceBox").style.visibility = "hidden";
document.getElementById("floating-panel").style.visibility = "hidden";
document.getElementById("clearMapButton").style.visibility = "hidden";

let map;
let eventLat;
let eventLon;
let currTravelMode = "WALKING";
let currOriginPlaceId = "";
let place = "";
let directionsRenderer;
let directionsService;

const coordinates = "coordinates";
const description = "description";
const ended = "ended";
const durability = "durability";
const ending_date = "ending_date";
const initial_date = "initial_date";
const institutionName = "institutionName";
const maxParticipants = "maxParticipants";
const members = "members";
const minParticipants = "minParticipants";
const name = "name";
const points = "points";
const rating = "rating";
const tags = "tags";
const time = "time";

const attributes = [coordinates, description, durability, ended, ending_date, initial_date, institutionName,
    maxParticipants, members, minParticipants, name, points, rating, tags, time];

function stringToIndex(id) {
    return attributes.indexOf(id);
}

function initMap() {
    directionsRenderer = new google.maps.DirectionsRenderer();
    directionsService = new google.maps.DirectionsService();
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
    new AutocompleteDirectionsHandler(map, true);
}

let originInput;

let dist = 0;

function clearDirections() {
    document.getElementById("clearMapButton").style.visibility = "hidden";
    document.getElementById("distanceBox").style.visibility = "hidden";
    directionsRenderer.setMap(null);
    directionsRenderer = new google.maps.DirectionsRenderer();
    directionsRenderer.setMap(map);
}

class AutocompleteDirectionsHandler {
    constructor(map, waitForChanges) {
        this.map = map;
        this.originPlaceId = "";
        directionsRenderer.setMap(map);
        originInput = document.getElementById("eventOrigin");
        // const modeSelector = document.getElementById("mode-selector");
        // map.controls[google.maps.ControlPosition.TOP_LEFT].push(originInput);
        // map.controls[google.maps.ControlPosition.TOP_LEFT].push(modeSelector);
        const originAutocomplete = new google.maps.places.Autocomplete(originInput);
        // Specify just the place data fields that you need.
        originAutocomplete.setFields(["place_id"]);

        if (waitForChanges) {
            this.setupPlaceChangedListener(originAutocomplete);
        } else {
            this.setupPlace(originAutocomplete);
        }
    }

    setupPlace(autocomplete) {
        autocomplete.bindTo("bounds", this.map);

        autocomplete.addListener("place_changed", () => {
            if (!place.place_id) {
                window.alert("Please select an option from the dropdown list.");
                return;
            }
            this.originPlaceId = place.place_id;
            currOriginPlaceId = this.originPlaceId;
        });


        this.originPlaceId = place.place_id;
        currOriginPlaceId = this.originPlaceId;

        // if (this.directionsRenderer != null) {
        //     // this.directionsRenderer.setDirections({routes: []});
        //     this.directionsRenderer.setMap(null);
        // }

        // this.directionsRenderer.set('directions', null);

        clearDirections();

        this.route();
    }

    setupPlaceChangedListener(autocomplete) {
        autocomplete.bindTo("bounds", this.map);
        autocomplete.addListener("place_changed", () => {
            place = autocomplete.getPlace();

            if (!place.place_id) {
                window.alert("Please select an option from the dropdown list.");
                return;
            }

            this.originPlaceId = place.place_id;
            currOriginPlaceId = this.originPlaceId;

            this.route();
        });
    }

    route() {
        document.getElementById("eventOrigin").style.visibility = "hidden";
        document.getElementById("clearMapButton").style.visibility = "visible";
        if (!this.originPlaceId && currOriginPlaceId === "") {
            return;
        }

        // this.originPlaceId = currOriginPlaceId;

        directionsService.route(
            {
                origin: {placeId: currOriginPlaceId},
                destination: {
                    "lat": parseFloat(eventLat),
                    "lng": parseFloat(eventLon)
                },
                travelMode: google.maps.TravelMode[currTravelMode],
            },
            (response, status) => {
                if (status === "OK") {
                    directionsRenderer.setDirections(response);
                    dist = response.routes[0].legs[0].distance.text;
                    document.getElementById("distanceBox").value = "Distance: " + dist;
                    document.getElementById("distanceBox").style.visibility = "visible";
                } else {
                    window.alert("Directions request failed due to " + status);
                }
            }
        );
    }
}

document.getElementById("mode").addEventListener("change", () => {
    currTravelMode = document.getElementById("mode").value;
    new AutocompleteDirectionsHandler(map, false);
});

var locations = [];

// locations = [
//     ['Canil Municipal de Sintra', 38.80103664384434, -9.362070114620005],
//     ['Comunidade Vida e Paz', 38.750773862083626, -9.133415786802798],
//     ['Cáritas Diocesana de Setúbal', 38.750773862083626, -9.133415786802798],
//     ['Comunidade Vida e Paz', 38.52423234014446, -8.8967447733166],
//     ['Casa de Acolhimento Residencial D. Nuno Álvares Pereira', 38.67591762242458, -9.160317675163238]
// ];

function getNrMembers(membersString) {
    // [b,g] comes looking like "[\"b\",\"g\"]"
    let nrMembers = 0;
    if (membersString !== "[]") {
        let nrMembersAux = (membersString.match(/,/g) || []).length;
        if (membersString === 0) {
            nrMembers = 1;
        } else {
            nrMembers = nrMembersAux + 1;
        }
    }
    return nrMembers;
}

function fillLocationsArray(obj) {
    let locationInfo = {
        name: obj[stringToIndex(name)].value,
        initial_date: obj[stringToIndex(initial_date)].value,
        ending_date: obj[stringToIndex(ending_date)].value,
        time: obj[stringToIndex(time)].value,
        durability: obj[stringToIndex(durability)].value,
        minParticipants: obj[stringToIndex(minParticipants)].value,
        maxParticipants: obj[stringToIndex(maxParticipants)].value,
        description: obj[stringToIndex(description)].value,
        tags: obj[stringToIndex(tags)].value.split("[")[1].split("]")[0].replace(/,/g, ''), // '[2,6]' to '26' (g means global/all string)
        nrMembers: getNrMembers(obj[stringToIndex(members)].value),
        latitude:  obj[stringToIndex(coordinates)].value.split(" ")[0],
        longitude:  obj[stringToIndex(coordinates)].value.split(" ")[1],
        ended: obj[stringToIndex(ended)].value,
        points: obj[stringToIndex(points)].value,
        rating: obj[stringToIndex(rating)].value
    }
    locations.push(locationInfo);
}

var markers = [];

function showOriginInput(i) {
    let elem = document.getElementById("eventOrigin");
    elem.style.visibility = "visible";
    elem.value = "";

    eventLat = locations[i].latitude;
    eventLon = locations[i].longitude;

    document.getElementById("floating-panel").style.visibility = "visible";

    new AutocompleteDirectionsHandler(map, true);
}

function convertToTags(currTags) {
    // tags:  ["animals", "environment", "children", "elderly", "supplies", "homeless"]
    // 1 to 6
    // currTags: if '[2,6]' then comes looking like '26'
    let tagsString = "";
    let currNrTags = currTags.length;
    if (currNrTags === 0) {
        tagsString = "None.";
    } else {
        for (let j = 0; j < currNrTags; j++) {
            switch (currTags[j]) {
                case '0':
                    if (j + 1 === currNrTags) {
                        tagsString += "animals.";
                    } else {
                        tagsString += "animals, ";
                    }
                    break;
                case '1':
                    if (j + 1 === currNrTags) {
                        tagsString += "environment.";
                    } else {
                        tagsString += "environment, ";
                    }
                    break;
                case '2':
                    if (j + 1 === currNrTags) {
                        tagsString += "children.";
                    } else {
                        tagsString += "children, ";
                    }
                    break;
                case '3':
                    if (j + 1 === currNrTags) {
                        tagsString += "elderly.";
                    } else {
                        tagsString += "elderly, ";
                    }
                    break;
                case '4':
                    if (j + 1 === currNrTags) {
                        tagsString += "supplies.";
                    } else {
                        tagsString += "supplies, ";
                    }
                    break;
                case '5':
                    tagsString += "homeless.";
                    break;
                default:
                    tagsString += "ERROR ";
                    break;
            }
        }
    }
    return tagsString;
}

function fillInfoWindow(marker, i) {
    var infowindow = new google.maps.InfoWindow();

    locationAux = locations[i];

    infowindow.setContent(
        'Event name: ' + locationAux.name +
        '<p></p>' +
        'Initial date: ' + locationAux.initial_date +
        '<p></p>' +
        'End date: ' + locationAux.ending_date +
        '<p></p>' +
        'Hour: ' + locationAux.time +
        '<p></p>' +
        'Frequency: ' + locationAux.durability +
        '<p></p>' +
        'Min participants: ' + locationAux.minParticipants +
        '<p></p>' +
        'Max participants: ' + locationAux.maxParticipants +
        '<p></p>' +
        'Number of current members: ' + locationAux.nrMembers +
        '<p></p>' +
        'Tags: ' + convertToTags(locationAux.tags) +
        '<p></p>' +
        'Description: ' + locationAux.description +
        '<p></p>' +
        '<button onclick="handleJoinEvent(locationAux.name)">Join event</button> &nbsp &nbsp' +
        `<button onclick="showOriginInput(${i})">View directions</button>`
    );
    infowindow.open(map, marker);
}

function adjustMarkerPlace(latlng) {
   ///get array of markers currently in cluster
   //final position for marker, could be updated if another marker already exists in same position
   var finalLatLng = new google.maps.LatLng(latlng.latitude,latlng.longitude);

   //check to see if any of the existing markers match the latlng of the new marker
   if (markers.length !== 0) {
       for (let i=0; i < markers.length; i++) {
           var existingMarker = markers[i];
           var pos = existingMarker.getPosition();

           //check if a marker already exists in the same position as this marker
           if (finalLatLng.equals(pos)) {

               //update the position of the coincident marker by applying a small multipler to its coordinates
               var newLat = finalLatLng.lat() + (Math.random() / 10000);
               var newLng = finalLatLng.lng() + (Math.random() / 10000);
               console.log(newLat,newLng);

               finalLatLng = new google.maps.LatLng(newLat,newLng);

           }
       }
   }

   return finalLatLng;
}
function addMarkers() {

    var marker, i;
    for (i = 0; i < locations.length; i++) {
      var position = adjustMarkerPlace(locations[i]);
        marker = new google.maps.Marker({
            position: position,
            map: map
        });

        markers.push(marker);

        google.maps.event.addListener(marker, 'click', (function (marker, i) {
            return function () {
                fillInfoWindow(marker, i)
            }
        })(marker, i));
    }
}

function goToPageBefore() {
    window.location.href = "../../afterLoginPage.html";
}

function populateMap(jsonResponse) {
    for (let i = 0; i < jsonResponse.length; i++) {
        let obj = [];
        obj = JSON.parse(jsonResponse[i]);

        fillLocationsArray(obj);
    }
    addMarkers();
}

function callGetEvents() {
    var jsonResponse = []
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            //console.log(xhttp.response);
            // console.log(xhttp.responseText);
            jsonResponse = JSON.parse(xhttp.responseText);
            populateMap(jsonResponse);
        }
    };
    xhttp.open("GET", "/rest/event/getAllEvents", true);
    xhttp.send();


}

function callJoinEvents(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    alert("Joined successfully.");
                    location.reload();
                    break;
                case 411:
                    alert("You need to give a name of an event.");
                    break;
                case 401:
                    alert("You need to be logged in to execute this operation.");
                    break;
                case 404:
                    alert("Token does not exist.");
                    break;
                case 403:
                    alert("The user with the given token does not exist.");
                    break;
                case 406:
                    alert("The user with the given token is disabled.");
                    break;
                case 417:
                    alert("You cannot join now because the participants' limit was already reached.");
                    break;
                case 409:
                    alert("You are already a  member of this event.");
                    break;
                default:
                    alert("Wrong parameters.");
                    break;
            }
        }
    };
    xhttp.open("POST", "/rest/event/joinEvent", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function handleJoinEvent(eventName) {
    let data = {
        eventId: eventName
    }
    callJoinEvents(JSON.stringify(data));
}

function openFormFilter(){

  document.getElementById("filterForm").style.display = "block";
}
function closeFormFilter() {
  document.getElementById("filterForm").style.display = "none";
}

function callFilter(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    location.reload();
                    break;
                case 401:
                    alert("You need to be logged in to execute this operation.");
                    break;

                default:
                    alert("Something went wrong.");
                    break;
            }
        }
    };
    xhttp.open("POST", "/rest/event/filterEventsWeb", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function fillTagsList(inputs) {
    let tagsList = [];
    let currTagId = "";
    for (let counter = 0; counter <= 5; counter++) {
        currTagId = "tag" + counter;
        if (document.getElementById(currTagId).checked) {
            // tagsList.push(inputs[t].value);
            tagsList.push(counter);
        }
    }
    return "[" + tagsList.toString() + "]";
}

function ChangeFormateDate(oldDate)
{
   return oldDate.toString().split("-").reverse().join("/");
}

function handleFilter(){
    let inputs = document.getElementsByName("filterInput")
    date = ChangeFormateDate(inputs[0].value);

    let radioButtonResult = ""
        if (document.getElementById('popular').checked) {
            radioButtonResult = inputs[3].value
        }
        if (document.getElementById('popular1').checked) {
            radioButtonResult = inputs[4].value
        }


    let data = {
        coordinates: "",
        date: date,
        institution: inputs[1].value,
        name: inputs[2].value,
        popularity: radioButtonResult,
        tags: fillTagsList(inputs)
    }
    callFilter(JSON.stringify(data));
    console.log(data)
}

let filterForm = document.getElementById("filterForm");
filterForm.onsubmit = () => {
    handleFilter();
    return false;
}

