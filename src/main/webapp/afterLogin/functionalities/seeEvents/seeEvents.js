let map;

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
}

var locations = [];

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
        'Description: ' + description
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

function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function populateMap(jsonResponse) {
    for(let i = 0; i < jsonResponse.length; i++) {
        let obj = [];
        obj = JSON.parse(jsonResponse[i]);

        fillLocationsArray(obj);
    }
    addMarkers();
}

function callSeeEvents(){
    var jsonResponse = []
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            jsonResponse = JSON.parse(xhttp.responseText);
            populateMap(jsonResponse)

        }

    };
    xhttp.open("GET", "/rest/event/listUserEvents", true);
    xhttp.send();


}