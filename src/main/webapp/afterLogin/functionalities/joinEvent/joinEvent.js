document.getElementById("eventOrigin").style.visibility = "hidden";
document.getElementById("distanceBox").style.visibility = "hidden";

let map;
let eventLat;
let eventLon;

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
    new AutocompleteDirectionsHandler(map);
}

let originInput;

let dist = 0;

class AutocompleteDirectionsHandler {
    constructor(map) {
        this.map = map;
        this.originPlaceId = "";
        this.travelMode = google.maps.TravelMode.WALKING;
        this.directionsService = new google.maps.DirectionsService();
        this.directionsRenderer = new google.maps.DirectionsRenderer();
        this.directionsRenderer.setMap(map);
        originInput = document.getElementById("eventOrigin");
        const modeSelector = document.getElementById("mode-selector");
        // map.controls[google.maps.ControlPosition.TOP_LEFT].push(originInput);
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(modeSelector);
        const originAutocomplete = new google.maps.places.Autocomplete(originInput);
        // Specify just the place data fields that you need.
        originAutocomplete.setFields(["place_id"]);
        this.setupPlaceChangedListener(originAutocomplete, "ORIG");
    }

    setupPlaceChangedListener(autocomplete, mode) {
        autocomplete.bindTo("bounds", this.map);
        autocomplete.addListener("place_changed", () => {
            const place = autocomplete.getPlace();

            if (!place.place_id) {
                window.alert("Please select an option from the dropdown list.");
                return;
            }

            if (mode === "ORIG") {
                this.originPlaceId = place.place_id;
            }
            this.route();
        });
    }

    route() {
        document.getElementById("eventOrigin").style.visibility = "hidden";
        document.getElementById("distanceBox").style.visibility = "visible";
        if (!this.originPlaceId) {
            return;
        }
        const me = this;
        this.directionsService.route(
            {
                origin: {placeId: this.originPlaceId},
                destination: {
                    "lat": parseFloat(eventLat),
                    "lng": parseFloat(eventLon)
                },
                travelMode: this.travelMode,
            },
            (response, status) => {
                if (status === "OK") {
                    me.directionsRenderer.setDirections(response);
                    dist = response.routes[0].legs[0].distance.text;
                    document.getElementById("distanceBox").value = "Distance: " + dist;
                } else {
                    window.alert("Directions request failed due to " + status);
                }
            }
        );
    }
}

var locations = [];

// locations = [
//     ['Canil Municipal de Sintra', 38.80103664384434, -9.362070114620005],
//     ['Comunidade Vida e Paz', 38.750773862083626, -9.133415786802798],
//     ['Cáritas Diocesana de Setúbal', 38.750773862083626, -9.133415786802798],
//     ['Comunidade Vida e Paz', 38.52423234014446, -8.8967447733166],
//     ['Casa de Acolhimento Residencial D. Nuno Álvares Pereira', 38.67591762242458, -9.160317675163238]
// ];

function fillLocationsArray(obj) {
    let locationAux = [];
    locationAux.push(obj[9].value); // eventName - 0
    locationAux.push(obj[0].value.split(" ")[0]); // latitude - 1
    locationAux.push(obj[0].value.split(" ")[1]); // longitude - 2
    locationAux.push(obj[4].value); // initDate - 3
    locationAux.push(obj[3].value); // endDate - 4
    locationAux.push(obj[13].value); // hour - 5
    locationAux.push(obj[2].value); // frequency - 6
    locationAux.push(obj[8].value); // minParticipants - 7
    locationAux.push(obj[6].value); // maxParticipants - 8
    locationAux.push(obj[1].value); // description - 9

    locations.push(locationAux);
}

var markers = [];

function showOriginInput(i){
    let elem = document.getElementById("eventOrigin");
    elem.style.visibility = "visible";
    elem.value="";

    eventLat = locations[i][1];
    eventLon = locations[i][2];
}

function fillInfoWindow(marker, i) {
    var infowindow = new google.maps.InfoWindow();

    locationAux = locations[i];

    let eventName = locationAux[0];
    let initDate = locationAux[3];
    let endDate = locationAux[4];
    let hour = locationAux[5];
    let frequency = locationAux[6];
    let minParticipants = locationAux[7];
    let maxParticipants = locationAux[8];
    let description = locationAux[9];

    infowindow.setContent(
        'Event name: ' + eventName +
        '<p></p>' +
        'Initial date: ' + initDate +
        '<p></p>' +
        'End date: ' + endDate +
        '<p></p>' +
        'Hour: ' + hour +
        '<p></p>' +
        'Frequency: ' + frequency +
        '<p></p>' +
        'Min participants: ' + minParticipants +
        '<p></p>' +
        'Max participants: ' + maxParticipants +
        '<p></p>' +
        'Description: ' + description +
        '<p></p>' +
        '<button onclick="handleJoinEvent(locationAux[0])">Join event</button> &nbsp &nbsp' +
        `<button onclick="showOriginInput(${i})">View directions</button>`
    );
    infowindow.open(map, marker);
}

function addMarkers() {

    var marker, i;

    for (i = 0; i < locations.length; i++) {
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(locations[i][1], locations[i][2]),
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

        // obj[pos] - value
        // 0 - coordinates
        // 1 - description
        // 2 - frequency
        // 3 - endDate
        // 4 - initDate
        // 5 - institutionName
        // 6 - maxParticipants
        // 7 - members
        // 8 - minParticipants
        // 9 - eventName
        // 10 - points
        // 11 - rating
        // 12 - tags
        // 13 - hour

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
            populateMap(jsonResponse)
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

