function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function callChangeAttributes(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); break;
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

    goToPageBefore();
}

function handleChangeAttributes() {
    let inputs = document.getElementsByName("changeAttributesInput")
    let data = {
        newEmail: inputs[0].value,
        newProfileType: inputs[1].value,
        newLandLine: inputs[2].value,
        newMobile: inputs[3].value,
        newAddress: inputs[4].value,
        newSecondAddress: inputs[5].value,
        newPostal: inputs[6].value
    }
    callChangeAttributes(JSON.stringify(data));
}

let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes();
    return false;
}
