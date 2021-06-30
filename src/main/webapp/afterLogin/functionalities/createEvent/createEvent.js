


function callCreateEvent(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); break;
                case 406: alert("Wrong parameters."); break;
                case 409: alert("Event already exists"); break;
                default: alert("Something went wrong"); break;
            }
        }
    };
    xhttp.open("POST", "/rest/event/registerEvent", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}


function handleCreateEvent() {
    let inputs = document.getElementsByName("createEventInput")
    let data = {
        name: inputs[0].value,
        description: inputs[1].value,
        minParticipants: inputs[2].value,
        maxParticipants: inputs[3].value,
        lat: inputs[4].value,
        lon: inputs[5].value,
        date: inputs[6].value,
        temporary: inputs[7].value,
        tags : []

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
