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
        let text = document.createTextNode((obj[9].value));
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode((obj[5].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[6].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[2].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[3].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[4].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[1].value));
        cell.appendChild(text);
        cell = row.insertCell(0)
        text = document.createTextNode((obj[7].value));
        cell.appendChild(text);
    }
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
    xhttp.open("GET", "/rest/event/listUserEventsWeb", true);
    xhttp.send();


}