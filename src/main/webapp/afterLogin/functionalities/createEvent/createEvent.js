var map;
var searchBoxInput;
var latitude;
var longitude;

function goToAboutUs() {
    localStorage.setItem("isCompanyPage","false");
    window.location.href = "../../functionalities/aboutUs/aboutUs.html";
}

function initAutocomplete() {
    map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: true,
        center: {lat: 38.659784, lng: -9.202765},
        zoom: 9
    });
    // Create the search box and link it to the UI element.
    searchBoxInput = document.getElementById("searchBoxInput");
    const searchBox = new google.maps.places.SearchBox(searchBoxInput);
    map.controls[google.maps.ControlPosition.TOP_LEFT].push(searchBoxInput);
    // Bias the SearchBox results towards current map's viewport.
    map.addListener("bounds_changed", () => {
        searchBox.setBounds(map.getBounds());

    });
    let markers = [];
    // Listen for the event fired when the user selects a prediction and retrieve
    // more details for that place.

    searchBox.addListener("places_changed", () => {
        const places = searchBox.getPlaces();
        if (places.length == 0) {
            return;
        }
        // Clear out the old markers.
        markers.forEach((marker) => {
            marker.setMap(null);
        });
        markers = [];
        // For each place, get the icon, name and location.
        const bounds = new google.maps.LatLngBounds();
        places.forEach((place) => {
            if (!place.geometry || !place.geometry.location) {
                console.log("Returned place contains no geometry");
                return;
            }
            const icon = {
                url: place.icon,
                size: new google.maps.Size(71, 71),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(17, 34),
                scaledSize: new google.maps.Size(25, 25),
            };
            // Create a marker for each place.
            markers.push(
                new google.maps.Marker({
                    map,
                    icon,
                    title: place.name,
                    position: place.geometry.location,
                })
            );

            if (place.geometry.viewport) {
                // Only geocodes have viewport.
                bounds.union(place.geometry.viewport);
            } else {
                bounds.extend(place.geometry.location);
            }

            latitude = place.geometry.location.lat()
            longitude = place.geometry.location.lng()
        });
        map.fitBounds(bounds);
    });

}

function callCreateEvent(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200:
                    alert(this.responseText);
                    break;
                case 401:
                    alert("You need to be logged in to execute this operation.");
                    break;
                case 411:
                    alert("Please fill in all fields.");
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
    xhttp.open("POST", "/rest/event/addEventWeb", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function fillTagsList(inputs) {
    let tagsList = [];
    let currTagId = "";
    let counter = 1;
    for (let t = 8; t <= 13; t++) {
        currTagId = "tag" + counter;
        if (document.getElementById(currTagId).checked) {
            // tagsList.push(inputs[t].value);
            tagsList.push(counter);
        }
        counter++;
    }
    return tagsList;
}

function handleCreateEvent() {
    let inputs = document.getElementsByName("createEventInput");

    let radioButtonResult = ""
    if (document.getElementById('temporary').checked) {
        radioButtonResult = inputs[5].value
    }
    if (document.getElementById('temporary2').checked) {
        radioButtonResult = inputs[6].value
    }

    let data = {
        name: inputs[0].value,
        description: inputs[1].value,
        minParticipants: inputs[2].value,
        maxParticipants: inputs[3].value,
        lat: latitude,
        lon: longitude,
        date: inputs[4].value,
        hour: inputs[5].value,
        temporary: radioButtonResult,
        tags: fillTagsList(inputs)

    }
    callCreateEvent(JSON.stringify(data));
}


let createEventForm = document.getElementById("createEventId");
createEventForm.onsubmit = () => {
    handleCreateEvent();
    return false;
}

function goBack() {
    window.location.href = "../../afterLoginCompanyPage.html";
}
