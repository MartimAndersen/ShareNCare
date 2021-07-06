document.getElementById("submitButton").style.visibility = "hidden";

var map;

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
    new AutocompleteDirectionsHandler(map);
}

let dist = 0;

class AutocompleteDirectionsHandler {
    constructor(map) {
        this.map = map;
        this.originPlaceId = "";
        this.destinationPlaceId = "";
        this.travelMode = google.maps.TravelMode.WALKING;
        this.directionsService = new google.maps.DirectionsService();
        this.directionsRenderer = new google.maps.DirectionsRenderer();
        this.directionsRenderer.setMap(map);
        const originInput = document.getElementById("origin-input");
        const destinationInput = document.getElementById("destination-input");
        const modeSelector = document.getElementById("mode-selector");
        const originAutocomplete = new google.maps.places.Autocomplete(originInput);
        // Specify just the place data fields that you need.
        originAutocomplete.setFields(["place_id"]);
        const destinationAutocomplete = new google.maps.places.Autocomplete(
            destinationInput
        );
        // Specify just the place data fields that you need.
        destinationAutocomplete.setFields(["place_id"]);
        this.setupPlaceChangedListener(originAutocomplete, "ORIG");
        this.setupPlaceChangedListener(destinationAutocomplete, "DEST");
        this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(originInput);
        this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(
            destinationInput
        );
        this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(modeSelector);

        // new google.maps.Marker({
        //     position: new google.maps.LatLng(38.736946, -9.142685),
        //     map: map
        // })
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
            } else {
                this.destinationPlaceId = place.place_id;
            }
            this.route();
        });
    }

    route() {
        if (!this.originPlaceId || !this.destinationPlaceId) {
            return;
        }
        const me = this;
        this.directionsService.route(
            {
                origin: {placeId: this.originPlaceId},
                destination: {placeId: this.destinationPlaceId},
                travelMode: this.travelMode,
            },
            (response, status) => {
                if (status === "OK") {
                    me.directionsRenderer.setDirections(response);
                    dist = response.routes[0].legs[0].distance.text;
                    document.getElementById("distanceBox").innerHTML = "Distance: " + dist;
                } else {
                    window.alert("Directions request failed due to " + status);
                }
            }
        );

        document.getElementById("submitButton").style.visibility = "visible";
    }
}

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
                infowindow.setContent(locations[i][0]);
                infowindow.open(map, marker);
            }
        })(marker, i));
    }
}

function clearMap() {
    for(let m=0; m<markers.length; m++){
        markers[m].setMap(null);
    }
}

function goToAboutUs() {
    localStorage.setItem("isUserPage","true");
    window.location.href = "../../functionalities/aboutUs/aboutUs.html";
}

function goBack() {
    window.location.href = "../../afterLoginPage.html";
}

