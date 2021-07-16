function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function populate_table(jsonResponse) {
    let table = document.getElementById('demo_table');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];

        obj = JSON.parse(jsonResponse[i]);

        fillLocationsArray(obj);

        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode((obj[11].value));
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode((obj[18].value));
        cell.appendChild(text);

    }
    addMarkers();
}

function callRank(){
    var jsonResponse = []
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
                case 401: alert("Please enter a token."); break;
                case 404: alert("Token does not exist."); break;
                case 403: alert("The user with the given token does not exist."); break;
                case 406: alert("The user with the given token is disabled."); break;
            case 200:
            jsonResponse = JSON.parse(xhttp.responseText);
            populate_table(jsonResponse);break

        }

    };
    xhttp.open("GET", "/rest/ranking/rankUsers", true);
    xhttp.send();


}