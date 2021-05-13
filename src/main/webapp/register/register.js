

function callRest(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
        }
    };
    xhttp.open("POST", "/rest/register/user", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    //xhttp.setRequestHeader("Content-type", "multipart/form-data");
    xhttp.send(data);

}



function handleSubmit() {
    let counter = 0;
    //alert("ola1")
    // let data = new FormData(document.querySelector('form'))
    //alert("ola1")

    // for (let pair of data.entries()) {
    //     counter++;
    //     if(counter <= nrAttributes){
    //         console.log(pair[0])
    //         console.log(pair[1])
    //         data.append(pair[0], pair[1]);
    //
    //     } else{
    //         break;
    //     }
    // }

    let inputs = document.getElementsByName("regInput")

    let data = {
        username: inputs[0].value,
        email: inputs[1].value,
        password: inputs[2].value,
        confirmation: inputs[3].value,
        mobile: inputs[4].value,
        address: inputs[5].value,
        postal: inputs[6].value
    }
    callRest(JSON.stringify(data));
}



let myForm = document.getElementById("formID");
myForm.onsubmit = () => {
    handleSubmit();
    return false;
}