function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

var locations = [];

function fillLocationsArray(obj) {
    let locationAux = [];
    locationAux.push(obj[7].value);
    locationAux.push(obj[0].value.split(" ")[0]);
    locationAux.push(obj[0].value.split(" ")[1]);
    locations.push(locationAux);
}

function populate_table(jsonResponse) {
    let table = document.getElementById('demo_table');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];

        obj = JSON.parse(jsonResponse[i]);
       
        fillLocationsArray(obj);

        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode((obj[7].value));
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode((obj[13].value));
        cell.appendChild(text);

    }

}

function callRank(){

        var jsonResponse = []
           let xhttp = new XMLHttpRequest();
           xhttp.onreadystatechange = function () {
               if (this.readyState == 4 && this.status == 200) {
                   // Typical action to be performed when the document is ready:

                   jsonResponse = JSON.parse(xhttp.responseText);
                   populate_table(jsonResponse)

               }

           };

    xhttp.open("GET", "/rest/ranking/rankUsers", true);
    xhttp.send();
    

}
