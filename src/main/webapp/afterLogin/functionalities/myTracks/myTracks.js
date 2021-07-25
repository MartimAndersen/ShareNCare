let map;
let eventLat;
let eventLon;
let currTravelMode = "WALKING";
let place = "";
let directionsRenderer;
let directionsService;

let emptyOrigin = true;

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

let solidarityPoints = [];

const attributes = [coordinates, description, durability, ended, ending_date, initial_date, institutionName,
    maxParticipants, members, minParticipants, name, points, rating, tags, time];

function stringToIndex(attributes, id) {
    return attributes.indexOf(id);
}

function initMap() {
    directionsRenderer = new google.maps.DirectionsRenderer({suppressMarkers: true});
    directionsService = new google.maps.DirectionsService();
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
}

var dist = 0;

const waypts = [];

function popWayPoints() {
    while (waypts.length) {
        waypts.pop();
    }
}

let originAux;
let trackInfo;
let lastDestinationLat;
let lastDestinationLon;

class DrawTrack {
    constructor(map) {
        this.map = map;
        directionsRenderer.setMap(map);
        this.route();
    }

    route() {
        trackInfo = {
            origin: originAux,
            destination: {
                "lat": eventLat,
                "lng": eventLon
            },
            waypoints: waypts,
            travelMode: google.maps.TravelMode[currTravelMode]
        };

        lastDestinationLat = eventLat;
        lastDestinationLon = eventLon;


        directionsService.route(
            trackInfo,
            (response, status) => {
                if (status === "OK") {
                    directionsRenderer.setDirections(response);

                    for (let i = 0; i < response.routes[0].legs.length; i++) {
                        dist += response.routes[0].legs[i].distance.value / 1000;
                    }
                } else {
                    window.alert("Directions request failed due to " + status);
                }
            }
        );
        document.getElementById("floating-panel").style.visibility = "visible";
    }
}

document.getElementById("mode").addEventListener("change", () => {
    currTravelMode = document.getElementById("mode").value;
    new DrawTrack(map);
});

document.getElementById("selectTrack").addEventListener("change", () => {
    dist = 0;
    prepareDrawTrack(document.getElementById("selectTrack").value);
});

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
        name: obj[stringToIndex(attributes, name)].value,
        initial_date: obj[stringToIndex(attributes, initial_date)].value,
        ending_date: obj[stringToIndex(attributes, ending_date)].value,
        time: obj[stringToIndex(attributes, time)].value,
        durability: obj[stringToIndex(attributes, durability)].value,
        minParticipants: obj[stringToIndex(attributes, minParticipants)].value,
        maxParticipants: obj[stringToIndex(attributes, maxParticipants)].value,
        description: obj[stringToIndex(attributes, description)].value,
        tags: obj[stringToIndex(attributes, tags)].value.split("[")[1].split("]")[0].replace(/,/g, ''), // '[2,6]' to '26' (g means global/all string)
        nrMembers: getNrMembers(obj[stringToIndex(attributes, members)].value),
        latitude: obj[stringToIndex(attributes, coordinates)].value.split(" ")[0],
        longitude: obj[stringToIndex(attributes, coordinates)].value.split(" ")[1],
        ended: obj[stringToIndex(attributes, ended)].value,
        points: obj[stringToIndex(attributes, points)].value,
        rating: obj[stringToIndex(attributes, rating)].value
    }
    locations.push(locationInfo);
}

var markers = [];

function fillWayPoints(givenLocation) {
    waypts.push({
        location: givenLocation,
        stopover: true
    });
}

function generateLocation(givenLat, givenLon) {
    return {
        "lat": givenLat,
        "lng": givenLon
    };
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
        'Description: ' + locationAux.description
    );
    infowindow.open(map, marker);
}

function addMarkers() {

    var marker, i;

    for (i = 0; i < locations.length; i++) {
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(locations[i].latitude, locations[i].longitude),
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

function callSeeEvents() {
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

function goToAboutUs() {
    localStorage.setItem("isUserPage", "true");
    window.location.href = "../../functionalities/aboutUs/aboutUs.html";
}

function goBack() {
    window.location.href = "../../afterLoginPage.html";
}

/* ====================== See Tracks ====================== */

const average_rating = "average_rating";
const comments = "comments";
// const description = "description";
const difficulty = "difficulty";
const distance = "distance";
// const markers = "markers";
const solidarity_points = "solidarity_points";
// const time = "time";
const title = "title";
const trackDangerZones = "trackDangerZones";
const trackMedia = "trackMedia";
const trackNotes = "trackNotes";
const type = "type";
const username = "username";

let tracks = [];

const trackAttributes = [average_rating, comments, description, difficulty, distance, markers,
    solidarity_points, time, title, trackDangerZones, trackMedia, trackNotes, type, username];

function fillTracksArray(obj) {

    let commentsParsed = JSON.parse(obj[stringToIndex(trackAttributes, comments)].value);
    let solidarityPointsParsed = JSON.parse(obj[stringToIndex(trackAttributes, solidarity_points)].value);

    // commentsParsed[0].comment == "nice"
    // comment: "nice"
    // likes: 0
    // rating: "3"
    // routeName: "sobreda ate costa"
    // username: "a"


    let trackInfo = {
        average_rating: obj[stringToIndex(trackAttributes, average_rating)].value,
        comments: commentsParsed,
        description: obj[stringToIndex(trackAttributes, description)].value,
        difficulty: obj[stringToIndex(trackAttributes, difficulty)].value,
        distance: obj[stringToIndex(trackAttributes, distance)].value,
        markers: obj[stringToIndex(trackAttributes, markers)].value,
        solidarity_points: solidarityPointsParsed,
        time: obj[stringToIndex(trackAttributes, time)].value,
        title: obj[stringToIndex(trackAttributes, title)].value,
        trackDangerZones: obj[stringToIndex(trackAttributes, trackDangerZones)].value,
        trackMedia: obj[stringToIndex(trackAttributes, trackMedia)].value,
        trackNotes: obj[stringToIndex(trackAttributes, trackNotes)].value,
        type: obj[stringToIndex(trackAttributes, type)].value,
        username: obj[stringToIndex(trackAttributes, username)].value
    }
    tracks.push(trackInfo);
}

let tracksLength = 0;

function populateMapTrack(jsonResponse) {
    for (let i = 0; i < jsonResponse.length; i++) {
        let obj = [];
        obj = JSON.parse(jsonResponse[i]);

        fillTracksArray(obj);
    }
    // if (tracks.length !== 0) {
    //     let currPoints = [];
    //     currPoints = tracks[0].solidarity_points;
    //     originAux = new google.maps.LatLng(parseFloat(currPoints[0].latitude), parseFloat(currPoints[0].longitude));
    //     for (let i = 1; i < currPoints - 1; i++) {
    //         fillWayPoints(new google.maps.LatLng(parseFloat(currPoints[i].latitude), parseFloat(currPoints[i].longitude)));
    //     }
    //     new DrawTrack(map);
    // }
    tracksLength = tracks.length;

    if (tracksLength !== 0) {
        document.getElementById("deleteTrackButton").style.visibility = "visible";
        fillSelectTrackPanel();
        prepareDrawTrack(0);
    }
}

let trackTitle;

function fillSelectTrackPanel() {
    for (let i = 0; i < tracksLength; i++) {
        let opt = document.createElement('option');
        opt.value = i;
        trackTitle = tracks[i].title;
        opt.innerHTML = trackTitle;
        document.getElementById("selectTrack").appendChild(opt);
    }
}

function prepareDrawTrack(trackIndex){
    let currPoints = [];
    currPoints = tracks[trackIndex].solidarity_points;
    originAux = generateLocation(currPoints[0].latitude, currPoints[0].longitude);
    let lastPointIndex = currPoints.length - 1;
    popWayPoints();
    for (let i = 1; i < lastPointIndex; i++) {
        fillWayPoints(generateLocation(currPoints[i].latitude, currPoints[i].longitude));
    }
    eventLat = currPoints[lastPointIndex].latitude;
    eventLon = currPoints[lastPointIndex].longitude;
    new DrawTrack(map);
    document.getElementById("avgRating").innerHTML = tracks[trackIndex].average_rating + " out of 5";
    // document.getElementById("distance").innerHTML = Math.round(dist) + " km";
    document.getElementById("difficultyOfTheWalk").innerHTML = tracks[trackIndex].difficulty + " out of 10";
    document.getElementById("descriptionOfTrack").innerHTML = tracks[trackIndex].description;
}

function callSeeTracks() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    let jsonResponse = JSON.parse(xhttp.responseText);
                    populateMapTrack(jsonResponse);

                    break;
                case 400:
                    alert("Initial date needs to be in the future");
                    break;
                case 401:
                    alert("You need to be logged in to execute this operation.");
                    break;
                case 411:
                    alert("Please fill in all fields.");
                    break;
                case 412:
                    alert("Final date needs to be after initial date or in the same day");
                    break;
                case 406:
                    alert("Number of participants is invalid.");
                    break;
                case 403:
                    alert("Invalid date format.");
                    break;
                case 409:
                    alert("Event already exists.");
                    break;
                case 417:
                    alert("Invalid hour format.");
                    break;
                default:
                    alert("Something went wrong.");
                    break;
            }
        }
    };
    xhttp.open("GET", "/rest/map/listUserTrack", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}


/* ====================== See Tracks End ====================== */

function handleDeleteTrack(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    alert("Track deleted successfully.");
                    location.reload();
                    break;
                case 409:
                    alert("There are no tracks available to be deleted.");
                    break;
                default:
                    alert("Something went wrong.");
                    break;
            }
        }
    };
    xhttp.open("POST", "/rest/map/deleteTrackWeb", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function callDeleteTrack() {
    let data = {
        trackName: trackTitle
    }
    handleDeleteTrack(JSON.stringify(data));
}