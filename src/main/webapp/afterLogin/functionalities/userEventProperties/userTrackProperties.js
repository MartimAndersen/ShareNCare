let time = 0;
/*
class AutocompleteDirectionsHandler {
    constructor() {
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
}*/

function callTrackProperties() {
    var jsonResponse = []
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            jsonResponse = JSON.parse(xhttp.responseText);
            populate_table(jsonResponse);
        } else if (this.readyState === 4 && this.status === 400) {
            alert("You need to be logged in to execute this operation.");
        }
    };
    xhttp.open("GET", "/rest/map/userTrackProperties", true);
    xhttp.send();
}

function populate_table(jsonResponse) {
    let table = document.getElementById('demo_table');
    var obj = []
    time = response.routes[0].legs[0].time.text;
    obj= JSON.parse(jsonResponse[0]);
    for(var i = 0; i < jsonResponse.length; i++) { //tem que começar na 2 posicao, a 1 e o tempo
        obj= JSON.parse(jsonResponse[i]);
        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode((obj[0].value));
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode((obj[5].value));
        cell.appendChild(text);
    }
}