function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function callChangeAttributes(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            let a = this.responseText;
            switch (this.status) {
                case 200: alert("Properties changed."); goToPageBefore(); break;
                case 411: alert("Please enter at least one new attribute."); break;
                case 401: alert("You need to be logged in to execute this operation."); break;
                case 404: alert("Token does not exist."); break;
                case 403: alert("The user with the given token does not exist."); break;
                case 406: alert("The user with the given token is disabled."); break;
                case 412: alert("Invalid email."); break;
                case 409: alert("Invalid postal code."); break;
                case 417: alert("Invalid mobile phone."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/changeAttributesWeb", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function handleChangeAttributes() {
    let inputs = document.getElementsByName("changeAttributesInput")
    let radioButton = ""
    if(document.getElementById('newProfile').checked){
        radioButton = inputs[1].value
    }
    if(document.getElementById('newProfile1').checked){
       radioButton = inputs[2].value
    }
    let data = {
        newEmail: inputs[0].value,   
        newProfileType: radioButton,
        newLandLine: inputs[3].value,
        newMobile: inputs[4].value,
        newAddress: inputs[5].value,
        newSecondAddress: inputs[6].value,
        newPostal: inputs[7].value
    }
    callChangeAttributes(JSON.stringify(data));
}

let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes();
    return false;
}
