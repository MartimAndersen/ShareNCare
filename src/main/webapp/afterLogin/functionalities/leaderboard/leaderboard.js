function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

var locations = [];



function populate_table(jsonResponse) {
    let table = document.getElementById('demo_table');
    for(var i = 0; i < jsonResponse.length; i++) {
        let obj = [];
        console.log(jsonResponse);
        obj = JSON.parse(JSON.stringify(jsonResponse[i]));
        console.log(obj);


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

    xhttp.open("GET", "/rest/ranking/rankUsers", true);
    xhttp.send();
    

}
