
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
    locationAux.push(obj[7].value);
    locationAux.push(obj[0].value.split(" ")[0]);
    locationAux.push(obj[0].value.split(" ")[1]);
    locations.push(locationAux);
}

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

function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

// function myFunction(jsonResponse) {
//     for(var i = 0; i < jsonResponse.length; i++) {
//         var obj = []
//         obj= JSON.parse(jsonResponse[i]);
//
//         console.log(obj);
//     }
// }

function populate_table(jsonResponse) {
    let table = document.getElementById('demo_table');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];

        obj = JSON.parse(jsonResponse[i]);

        fillLocationsArray(obj);

        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode((obj[2].value));
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode((obj[6].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[8].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[3].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[13].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[4].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[1].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[9].value));
        cell.appendChild(text);
    }
    addMarkers();
}

function callSeeEvents(){
    var jsonResponse = []
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            //console.log(xhttp.response);
            // console.log(xhttp.responseText);
            jsonResponse = JSON.parse(xhttp.responseText);
            populate_table(jsonResponse)

        }

    };
    xhttp.open("GET", "/rest/event/listInstitutionEvents", true);
    xhttp.send();


}

function callEventFinished(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText);  break;
                case 401: alert("Please enter a token."); break;
                case 404: alert("Token does not exist."); break;
                case 403: alert("The user with the given token does not exist."); break;
                case 406: alert("The user with the given token is disabled."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };

    xhttp.open("POST", "/rest/event/finishEvent", true);

    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);


}

function handleEventFinished() {
    let inputs = document.getElementsByName("eventInput")
    let data = {
       name: inputs[0].value

    }
    callEventFinished(JSON.stringify(data));
}

let finishEventForm = document.getElementById("EventFinishedId");
finishEventForm.onsubmit = () => {
    handleEventFinished();
    return false;
}