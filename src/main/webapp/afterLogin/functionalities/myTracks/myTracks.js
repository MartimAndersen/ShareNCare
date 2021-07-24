let map;
let eventLat;
let eventLon;
let currTravelMode = "WALKING";
let currOriginPlaceId = "";
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
    new AutocompleteDirectionsHandler(map, true, false);
}

let originInput;

let dist = 0;

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

class AutocompleteDirectionsHandler {
    constructor(map, firstMarker, changeTransitMode) {
        this.map = map;
        this.originPlaceId = "";
        directionsRenderer.setMap(map);
        originInput = document.getElementById("eventOrigin");
        const originAutocomplete = new google.maps.places.Autocomplete(originInput);
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
            } else {
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

                    for (let i = 0; i < response.routes[0].legs.length; i++) {
                        dist += response.routes[0].legs[i].distance.value / 1000;
                    }
                    document.getElementById("distanceBox").value = "Distance: " + Math.round(dist) + " km";
                    document.getElementById("distanceBox").style.visibility = "visible";
                    dist = 0;
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

function fillWayPoints(givenLocation) {
    waypts.push({
        location: givenLocation,
        stopover: true
    });

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
        `<button onclick="addToTrack(${i}, locationAux.latitude, locationAux.longitude)">Add to track</button>`
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

function showOriginInput(i, firstMarker, latitude, longitude) {
    let elem = document.getElementById("eventOrigin");
    elem.style.visibility = "visible";
    elem.value = "";

    eventLat = parseFloat(latitude);
    eventLon = parseFloat(longitude);

    new AutocompleteDirectionsHandler(map, firstMarker, false);
}

function addToTrack(i, latitude, longitude) {
    if (emptyOrigin) {
        emptyOrigin = false;
        showOriginInput(i, true, latitude, longitude);
    } else {
        showOriginInput(i, false, latitude, longitude);
    }
}


function callCreateTrack(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    openModal();
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
    xhttp.open("POST", "/rest/map/registerTrack", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function callRegisterComment(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    alert("Track " + trackTitle + " registered.")
                    break;
                case 405:
                    alert("Your comment is inappropriate.");
                    break;
                default:
                    alert("Something went wrong.");
                    break;
            }
        }
    };
    xhttp.open("POST", "/rest/map/comment", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}


let trackTitle;

function handleSubmitTrack() {
    let inputs = document.getElementsByName("createTrack");

    let solidarityPointsString = JSON.stringify(solidarityPoints).toString();
    solidarityPointsString = solidarityPointsString.replace(/lat/g, 'latitude');
    solidarityPointsString = solidarityPointsString.replace(/lng/g, 'longitude');

    trackTitle = inputs[0].value;

    let data = {
        title: trackTitle,
        description: inputs[1].value,
        distance: dist.toString(),
        difficulty: document.getElementById("sliderOutput").value,
        solidarityPoints: solidarityPointsString,
        type: "pre-made",
        username: localStorage.getItem("currUser"),
        time: "0"
    }
    callCreateTrack(JSON.stringify(data));
}

function isChecked(element) {
    return document.getElementById(element).checked;
}

function getStarsRating() {
    let res = 5;
    if (isChecked("star1")) {
        res = 1;
    } else if (isChecked("star2")) {
        res = 2;
    } else if (isChecked("star3")) {
        res = 3;
    } else if (isChecked("star4")) {
        res = 4;
    }
    return res;
}

let submitTrackForm = document.getElementById("TrackFormId");
submitTrackForm.onsubmit = () => {
    handleSubmitTrack();
    return false;
}

/* ====================== Review Form ====================== */

let focusedElementBeforeModal;
const modal = document.getElementById('modal');
const modalOverlay = document.querySelector('.modal-overlay');

// function callAddReview() {
//     const addReview = document.getElementById('submitButton');
//     addReview.id = 'submitButton';
//     addReview.innerHTML = '+';
//     addReview.setAttribute('aria-label', 'add review');
//     addReview.title = 'Add Review';
//     addReview.addEventListener('click', openModal);
//     addReview.click();
// }

function openModal() {
    // Save current focus
    focusedElementBeforeModal = document.activeElement;

    // Listen for and trap the keyboard
    modal.addEventListener('keydown', trapTabKey);

    // Listen for indicators to close the modal
    modalOverlay.addEventListener('click', closeModal);
    // Close btn
    const closeBtn = document.querySelector('.close-btn');
    closeBtn.addEventListener('click', closeModal);

    // submit form
    const form = document.getElementById('review-form');
    form.addEventListener('submit', submitAddReview, false);

    // Find all focusable children
    let focusableElementsString = 'a[href], area[href], input:not([disabled]), select:not([disabled]), textarea:not([disabled]), button:not([disabled]), iframe, object, embed, [tabindex="0"], [contenteditable]';
    let focusableElements = modal.querySelectorAll(focusableElementsString);
    // Convert NodeList to Array
    focusableElements = Array.prototype.slice.call(focusableElements);

    let firstTabStop = focusableElements[0];
    let lastTabStop = focusableElements[focusableElements.length - 1];

    // Show the modal and overlay
    modal.classList.add('show');
    modalOverlay.classList.add('show');

    function trapTabKey(e) {
        // Check for TAB key press
        if (e.keyCode === 9) {
            // SHIFT + TAB
            if (e.shiftKey) {
                if (document.activeElement === firstTabStop) {
                    e.preventDefault();
                    lastTabStop.focus();
                }
                // TAB
            } else {
                if (document.activeElement === lastTabStop) {
                    e.preventDefault();
                    firstTabStop.focus();
                }
            }
        }
        // ESCAPE
        if (e.keyCode === 27) {
            closeModal();
        }
    }
};

const submitAddReview = (e) => {
    let comment = document.getElementById("reviewComments").value;
    let starsRating = getStarsRating();
    handleSubmitComment(comment, starsRating);
    e.preventDefault();
    closeModal();
};

const closeModal = () => {
    // Hide the modal and overlay
    modal.classList.remove('show');
    modalOverlay.classList.remove('show');

    const form = document.getElementById('review-form');
    form.reset();
    // Set focus back to element that had it before the modal was opened
    focusedElementBeforeModal.focus();
};

/* ====================== Review Form End ====================== */


function handleSubmitComment(comment, starsRating) {

    let data = {
        comment: comment,
        rating: starsRating,
        routeName: trackTitle,
        username: localStorage.getItem("currUser"),
        likes: 0
    }
    callRegisterComment(JSON.stringify(data));
}

function goToAboutUs() {
    localStorage.setItem("isUserPage", "true");
    window.location.href = "../../functionalities/aboutUs/aboutUs.html";
}

function goBack() {
    window.location.href = "../../afterLoginPage.html";
}

/* ====================== See Tracks ====================== */

function callSeeTracks() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    let a = JSON.parse(xhttp.responseText);
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