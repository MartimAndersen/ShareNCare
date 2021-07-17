
let map;

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
}

var locations = [];

function getNrMembers(membersString){
    // [b,g] comes looking like "[\"b\",\"g\"]"
    let nrMembers = 0;
    if(membersString !== "[]"){
        let nrMembersAux = (membersString.match(/,/g) || []).length;
        if(membersString === 0){
            nrMembers = 1;
        } else{
            nrMembers = nrMembersAux + 1;
        }
    }
    return nrMembers;
}

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

    let tagsStringAux = obj[12].value.split("[")[1].split("]")[0].replace(/,/g, ''); // '[2,6]' to '26' (g means global/all string)

    locationAux.push(tagsStringAux); // tags - 10

    locationAux.push(getNrMembers(obj[7].value)); // nrMembers - 11

    locations.push(locationAux);
}

var markers = [];

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
                case '1':
                    if (j + 1 === currNrTags) {
                        tagsString += "animals.";
                    } else {
                        tagsString += "animals, ";
                    }
                    break;
                case '2':
                    if (j + 1 === currNrTags) {
                        tagsString += "environment.";
                    } else {
                        tagsString += "environment, ";
                    }
                    break;
                case '3':
                    if (j + 1 === currNrTags) {
                        tagsString += "children.";
                    } else {
                        tagsString += "children, ";
                    }
                    break;
                case '4':
                    if (j + 1 === currNrTags) {
                        tagsString += "elderly.";
                    } else {
                        tagsString += "elderly, ";
                    }
                    break;
                case '5':
                    if (j + 1 === currNrTags) {
                        tagsString += "supplies.";
                    } else {
                        tagsString += "supplies, ";
                    }
                    break;
                case '6':
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

    let eventName = locationAux[0];
    let initDate = locationAux[3];
    let endDate = locationAux[4];
    let hour = locationAux[5];
    let frequency = locationAux[6];
    let minParticipants = locationAux[7];
    let maxParticipants = locationAux[8];
    let description = locationAux[9];

    let currTags = locationAux[10];

    let tagsString = convertToTags(currTags);

    let nrMembers = locationAux[11];

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
        'Number of current members: ' + nrMembers +
        '<p></p>' +
        'Tags: ' + tagsString +
        '<p></p>' +
        'Description: ' + description +
        '<p></p>' +
        '<button onclick="editEvent(locationAux[0])">Edit event</button> &nbsp &nbsp' +
        `<button onclick="handleEventFinished(locationAux[0])">Finish event</button> &nbsp &nbsp` +
        `<button onclick="handleDeleteEvent(locationAux[0])">Delete event</button>`
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
    window.location.href = "../../afterLoginCompanyPage.html";
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

function handleEventFinished(eventName) {
    let data = {
       name: eventName

    }
    callEventFinished(JSON.stringify(data));
}

function editEvent(eventName){

}

function handleDeleteEvent(eventName) {
    // /rest/event/deleteEvent nao tira o evento dos membros
}