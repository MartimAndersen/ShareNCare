function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}


function clearTable(){
    var Parent = document.getElementById('demo_table');
    while(Parent.hasChildNodes())
    {
       Parent.removeChild(Parent.firstChild);
    }
}
function populate_table(jsonResponse) {
    //clearTable()
    let table = document.getElementById('demo_table');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];

        obj = JSON.parse(JSON.stringify(jsonResponse[i]));

        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode(obj.total);
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode(obj.username);
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

    xhttp.open("GET", "/rest/ranking/rankUsersWeb", true);
    xhttp.send();
    

}

function populate_table_event(jsonResponse) {
    //clearTable()
    let table = document.getElementById('demo_table2');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];

        obj = JSON.parse(JSON.stringify(jsonResponse[i]));

        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode(obj.events);
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode(obj.username);
        cell.appendChild(text);

    }

}

function callEventRank(){

        var jsonResponse = []
           let xhttp = new XMLHttpRequest();
           xhttp.onreadystatechange = function () {
               if (this.readyState == 4 && this.status == 200) {
                   // Typical action to be performed when the document is ready:

                   jsonResponse = JSON.parse(xhttp.responseText);
                   populate_table_event(jsonResponse)

               }

           };

    xhttp.open("GET", "/rest/ranking/rankEventUsers", true);
    xhttp.send();


}

function populate_table_track(jsonResponse) {
    //clearTable()
    let table = document.getElementById('demo_table3');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];

        obj = JSON.parse(JSON.stringify(jsonResponse[i]));

        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode(obj.tracks);
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode(obj.username);
        cell.appendChild(text);

    }

}

function callTrackRank(){

        var jsonResponse = []
           let xhttp = new XMLHttpRequest();
           xhttp.onreadystatechange = function () {
               if (this.readyState == 4 && this.status == 200) {
                   // Typical action to be performed when the document is ready:

                   jsonResponse = JSON.parse(xhttp.responseText);
                   populate_table_track(jsonResponse)

               }

           };

    xhttp.open("GET", "/rest/ranking/rankTrackUsers", true);
    xhttp.send();


}


function populate_table_comments(jsonResponse) {
    //clearTable()
    let table = document.getElementById('demo_table4');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];

        obj = JSON.parse(JSON.stringify(jsonResponse[i]));

        let row = table.insertRow(-1);
        let cell = row.insertCell(0);
        let text = document.createTextNode(obj.comments);
        cell.appendChild(text);
        cell = row.insertCell(0);
        text = document.createTextNode(obj.username);
        cell.appendChild(text);

    }

}

function callCommentsRank(){

        var jsonResponse = []
           let xhttp = new XMLHttpRequest();
           xhttp.onreadystatechange = function () {
               if (this.readyState == 4 && this.status == 200) {
                   // Typical action to be performed when the document is ready:

                   jsonResponse = JSON.parse(xhttp.responseText);
                   populate_table_comments(jsonResponse)

               }

           };

    xhttp.open("GET", "/rest/ranking/rankCommentsUsers", true);
    xhttp.send();


}

function callTables(){
callRank();
callEventRank();
callTrackRank();
callCommentsRank();
}