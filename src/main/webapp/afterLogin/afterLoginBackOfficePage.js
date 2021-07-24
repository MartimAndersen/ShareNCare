let map;

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
}

var locations = [];

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
        latitude: obj[stringToIndex(coordinates)].value.split(" ")[0],
        longitude: obj[stringToIndex(coordinates)].value.split(" ")[1],
        ended: obj[stringToIndex(ended)].value,
        points: obj[stringToIndex(points)].value,
        rating: obj[stringToIndex(rating)].value
    }
    locations.push(locationInfo);
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
        '<p></p>' +
        '<button onclick="openForm(locationAux.name)">Remove User</button> &nbsp &nbsp'
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

        //getLocation(locations[i].latitude, locations[i].longitude);
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
    window.location.href = "../afterLoginCompanyPage.html";
}

function populateMap(jsonResponse) {
    for (let i = 0; i < jsonResponse.length; i++) {
        let obj = [];
        obj = JSON.parse(jsonResponse[i]);

        fillLocationsArray(obj);
    }
    addMarkers();
}

function callSeeEvents() {
    var jsonResponse = []
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            jsonResponse = JSON.parse(xhttp.responseText);
            populateMap(jsonResponse)
        }
    };
    xhttp.open("GET", "/rest/event/getAllEvents", true);
    xhttp.send();
}
function openFormDeleteUser() {

  document.getElementById("deleteUser").style.display = "block";
}

function openForm(eventName) {

    localStorage.setItem("eventName", eventName);
  document.getElementById("myForm").style.display = "block";
}

function closeForm() {
  document.getElementById("myForm").style.display = "none";
}
function closeFormDeleteUser() {
  document.getElementById("deleteUser").style.display = "none";
}
function callDeleteUserFromEvent(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); break;
                case 401: alert("You need to be logged in to execute this operation."); break;
                case 404: alert("Token does not exist."); break;
                case 403: alert("The user with the given token does not exist."); break;
                case 406: alert("The user with the given token is disabled."); break;
                case 409: alert(" User is not a member of the event."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/event/removeUserFromEvent", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);


}

function handleDeleteUserFromEvent() {
    var event = [localStorage.getItem("eventName")];
    let inputs = document.getElementsByName("deleteUserInput")
    let data = {
        eventsId: event,
        username: inputs[0].value


    }
    callDeleteUserFromEvent(JSON.stringify(data));
}

let deleteUserFromEventForm = document.getElementById("myForm");
deleteUserFromEventForm.onsubmit = () => {
    handleDeleteUserFromEvent();
    return false;
}
function callDeleteUser(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); break;
                case 401: alert("You need to be logged in to execute this operation."); break;
                case 404: alert("Token does not exist."); break;
                case 403: alert("The user with the given token does not exist."); break;
                case 406: alert("The user with the given token is disabled."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/removeUser", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);


}

function handleDeleteUser() {
    let inputs = document.getElementsByName("deleteInput")
    let data = {
        userToDelete: inputs[0].value

    }
    callDeleteUser(JSON.stringify(data));
}

let changeAttributesForm = document.getElementById("deleteUser");
changeAttributesForm.onsubmit = () => {
    handleDeleteUser();
    return false;
}

function logout() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: window.location.href = "../welcomePage/welcomePage.html"; break;
                default: alert("You need to be logged in to execute this operation."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/logout", true);
    xhttp.setRequestHeader("Content-type", "application/json");

    xhttp.send("");
}