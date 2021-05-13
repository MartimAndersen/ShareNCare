
function callRest(data) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
        }
    };
    xhttp.open("POST", "https://localhost:8080/rest/register/op1", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

}



function handleSubmit(nrAttributes) {
    let counter = 0;
    //alert("ola1")
    var data = new FormData(document.querySelector('form'))
    //alert("ola1")

    for (let pair of data.entries()) {
        counter++;
        if(counter <= nrAttributes){
            console.log(pair[0])
            console.log(pair[1])
            data.append(pair[0], pair[1]);
            //alert("ola1")
        } else{
            break;
        }
    }
    callRest(data);
}
