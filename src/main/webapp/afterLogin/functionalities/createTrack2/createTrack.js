let map;
let eventLat;
let eventLon;
let currTravelMode = "WALKING";
let currOriginPlaceId = "";
let place;
let directionsRenderer;
let directionsService;

let emptyOrigin = true;

function initMap() {
    directionsRenderer = new google.maps.DirectionsRenderer();
    directionsService = new google.maps.DirectionsService();
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
    new AutocompleteDirectionsHandler(map, true, false);
}

let dist = 0;

let originInput;

let solidarityPoints = [];

function clearDirections() {
    emptyOrigin = true;
    document.getElementById("clearMapButton").style.visibility = "hidden";
    document.getElementById("distanceBox").style.visibility = "hidden";
    document.getElementById("submitButton").style.visibility = "hidden";
    directionsRenderer.setMap(null);
    directionsRenderer = new google.maps.DirectionsRenderer();
    directionsRenderer.setMap(map);
    while (waypts.length) {
        waypts.pop();
    }

    while (solidarityPoints.length) {
        solidarityPoints.pop();
    }
}

const waypts = [];

let originAux;
let trackInfo;
let lastDestinationLat;
let lastDestinationLon;



// Listen for the event fired when the user selects a prediction and retrieve
// more details for that place.

class AutocompleteDirectionsHandler {
    constructor(map, firstMarker, changeTransitMode) {
        this.map = map;
        this.originPlaceId = "";
        directionsRenderer.setMap(map);
        originInput = document.getElementById("eventOrigin");
        // const modeSelector = document.getElementById("mode-selector");
        // map.controls[google.maps.ControlPosition.TOP_LEFT].push(originInput);
        // map.controls[google.maps.ControlPosition.TOP_LEFT].push(modeSelector);
        const originAutocomplete = new google.maps.places.Autocomplete(originInput);
        // Specify just the place data fields that you need.
        originAutocomplete.setFields(["place_id", 'geometry']);

        firstMarker ? this.setupPlaceChangedListener(originAutocomplete, firstMarker, changeTransitMode) : this.route(firstMarker, changeTransitMode);
    }




    setupPlaceChangedListener(autocomplete, firstMarker, changeTransitMode) {
        autocomplete.bindTo("bounds", this.map);
        autocomplete.addListener("place_changed", () => {
            place = autocomplete.getPlace();


            if (!place.place_id) {
                window.alert("Please select an option from the dropdown list.");
                return;
            } else{
                solidarityPoints.push(new google.maps.LatLng(place.geometry.location.lat(), place.geometry.location.lng()));
            }

            this.originPlaceId = place.place_id;
            currOriginPlaceId = this.originPlaceId;

            this.route(firstMarker, changeTransitMode);
        });
    }

    route(firstMarker, changeTransitMode) {
        document.getElementById("eventOrigin").style.visibility = "hidden";
        document.getElementById("clearMapButton").style.visibility = "visible";

        if (firstMarker) {
            if (!this.originPlaceId && currOriginPlaceId === "") {
                return;
            }
            originAux = {placeId: currOriginPlaceId};
        }

        if (firstMarker) {
            trackInfo = {
                origin: originAux,
                destination: {
                    "lat": eventLat,
                    "lng": eventLon
                },
                travelMode: google.maps.TravelMode[currTravelMode]
            };
        } else {
            trackInfo = {
                origin: originAux,
                destination: {
                    "lat": eventLat,
                    "lng": eventLon
                },
                waypoints: waypts,
                travelMode: google.maps.TravelMode[currTravelMode]
            };
            if (!changeTransitMode) {
                fillWayPoints(new google.maps.LatLng(lastDestinationLat, lastDestinationLon));
            }
        }

        if (!changeTransitMode) {
            solidarityPoints.push(new google.maps.LatLng(eventLat, eventLon));
        }

        lastDestinationLat = eventLat;
        lastDestinationLon = eventLon;


        directionsService.route(
            trackInfo,
            (response, status) => {
                if (status === "OK") {
                    directionsRenderer.setDirections(response);

                    // if (!changeTransitMode) {
                    //     distAux = response.routes[0].legs[0].distance.value;
                    //     dist += distAux / 1000;
                    // }

                    for (let i = 0; i < response.routes[0].legs.length; i++) {
                        dist += response.routes[0].legs[i].distance.value / 1000;
                    }
                    document.getElementById("distanceBox").value = "Distance: " + Math.round(dist) + " km";
                    document.getElementById("distanceBox").style.visibility = "visible";
                    dist = 0;

                    // autocomplete.setFields(['place_id', 'geometry', 'name', 'formatted_address']);
                    var lat = place.geometry.location.lat();
                    var lng = place.geometry.location.lng();
                    var latlng = {lat , lng};
                } else {
                    window.alert("Directions request failed due to " + status);
                }
            }
        );
        document.getElementById("submitButton").style.visibility = "visible";
        document.getElementById("floating-panel").style.visibility = "visible";
    }
}

document.getElementById("mode").addEventListener("change", () => {
    currTravelMode = document.getElementById("mode").value;
    new AutocompleteDirectionsHandler(map, false, true);
});


function backToInitialPage() {
    window.location.href = "../../../initialPage/initialPage.html";
}

function goToAfterLoginPage() {
    window.location.href = "../../afterLoginPage.html";
}

function callCreateTrack(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
        } else if (this.readyState === 4 && this.status === 400) {
            alert("You need to be logged in to execute this operation.");
        } else if (this.readyState === 4 && this.status === 403) {
            alert("Please insert a title.");
        } else if (this.readyState === 4 && this.status === 409) {
            alert("The track with the given title already exists.");
        } else if (this.readyState === 4 && this.status !== 200) {
            alert("Wrong parameters.");
        }
    };
    xhttp.open("POST", "/rest/map/registerTrack", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function changeSliderValue(value) {
    document.getElementById('sliderInput').value = value;
}

document.getElementById("sliderInput").oninput = function () {
    document.getElementById("sliderOutput").value = document.getElementById("sliderInput").value;
}

function handleCreateTrack() {
    let inputs = document.getElementsByName("createTrack")

    let data = {
        title: inputs[0].value,
        description: inputs[1].value,
        origin: document.getElementById("origin-input").value,
        destination: document.getElementById("destination-input").value,
        distance: dist,
        difficulty: document.getElementById("sliderOutput").value
    }
    callCreateTrack(JSON.stringify(data));
}

let createTrackForm = document.getElementById("TrackFormId");
createTrackForm.onsubmit = () => {
    handleCreateTrack();
    return false;
}

let locations = [
    ['Canil Municipal de Sintra', 38.80103664384434, -9.362070114620005],
    ['Cáritas Diocesana de Setúbal', 38.52423234014446, -8.8967447733166],
    ['Comunidade Vida e Paz', 38.750773862083626, -9.133415786802798],
    ['Casa de Acolhimento Residencial D. Nuno Álvares Pereira', 38.67591762242458, -9.160317675163238]
];

var markers = [];

function fillWayPoints(givenLocation) {
    waypts.push({
        location: givenLocation,
        stopover: true
    });

}

function showOriginInput(i, firstMarker) {
    let elem = document.getElementById("eventOrigin");
    elem.style.visibility = "visible";
    elem.value = "";

    eventLat = locations[i][1];
    eventLon = locations[i][2];

    new AutocompleteDirectionsHandler(map, firstMarker, false);
}

function addToTrack(i) {
    if (emptyOrigin) {
        emptyOrigin = false;
        showOriginInput(i, true);
    } else {
        showOriginInput(i, false);
    }
}

function addMarkers() {

    var infowindow = new google.maps.InfoWindow();

    var marker, i;

    for (i = 0; i < locations.length; i++) {
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(locations[i][1], locations[i][2]),
            map: map
        });

        markers.push(marker);

        google.maps.event.addListener(marker, 'click', (function (marker, i) {
            return function () {
                infowindow.setContent(
                    locations[i][0] +
                    '<p></p>' +
                    `<button onclick="addToTrack(${i})">Add to track</button>`);
                infowindow.open(map, marker);
            }
        })(marker, i));
    }
}

function clearMap() {
    emptyOrigin = true;
    for (let m = 0; m < markers.length; m++) {
        markers[m].setMap(null);
    }
}

function goToAboutUs() {
    localStorage.setItem("isUserPage", "true");
    window.location.href = "../../functionalities/aboutUs/aboutUs.html";
}

function goBack() {
    window.location.href = "../../afterLoginPage.html";
}

