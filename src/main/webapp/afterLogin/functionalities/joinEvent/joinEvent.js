function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function myFunction(jsonResponse) {
    for(var i = 0; i < jsonResponse.length; i++) {
    var obj = []
         obj= JSON.parse(jsonResponse[i]);

        console.log(obj);
    }
}
function populate_table(jsonResponse) {
   let table = document.getElementById('demo_table');
   for(var i = 0; i < jsonResponse.length; i++) {
      var obj = []
             obj= JSON.parse(jsonResponse[i]);
        let row = table.insertRow(-1);
		let cell = row.insertCell(0);
		let text = document.createTextNode((obj[8].value));
        cell.appendChild(text);
		cell = row.insertCell(0);
		text = document.createTextNode((obj[5].value));
        cell.appendChild(text);
		cell = row.insertCell(0)
		text = document.createTextNode((obj[4].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
         text = document.createTextNode((obj[3].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
         text = document.createTextNode((obj[2].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[1].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[6].value));
        cell.appendChild(text);
   }
}

function callGetEvents(){
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
xhttp.open("GET", "/rest/event/getAllEventsWeb", true);
xhttp.send();


}

function callJoinEvents(data) {
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
    xhttp.open("POST", "/rest/event/addEventWeb", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function handleJoinEvents() {
    let inputs = document.getElementsByName("joinEventInput")
    let data = {
        eventId: inputs[0].value
    }
    callJoinEvents(JSON.stringify(data));
}

let joinEventForm= document.getElementById("joinEventFormId");
joinEventForm.onsubmit = () => {
    handleJoinEvents();
    return false;
}
