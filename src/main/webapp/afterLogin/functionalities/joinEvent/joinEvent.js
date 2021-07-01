function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function callGetEvents(){
let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
       // Typical action to be performed when the document is ready:
           //console.log(xhttp.response);
          // console.log(xhttp.responseText);
          var jsonResponse = JSON.parse(xhttp.responseText);
          console.log(jsonResponse);
    }
};
xhttp.open("GET", "/rest/event/getAllEventsWeb", true);
xhttp.send();

var jsonResponse = JSON.parse(xhttp.responseText);
console.log(jsonResponse);

let names = []
_.each(jsonResponse, (result) => {
    names.push(result.name)
})
console.log(names);



}