

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
    xhttp.open("POST", "/rest/loggedInInstitution/changeAttributesWeb", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);


}

function handleChangeAttributes() {
    let inputs = document.getElementsByName("changeAttributesInput")
    let data = {
        email: inputs[0].value,
        landLine: inputs[1].value,
        mobile: inputs[2].value,
        address: inputs[3].value,
        zipCode: inputs[4].value,
        fax: inputs[5].value,
        website: inputs[6].value,
        instagram: inputs[7].value,
        twitter: inputs[8].value,
        facebook: inputs[9].value,
        youtube: inputs[10].value,
        profilePic: [],
        // members: [],
        events: [],
        bio: inputs[11].value,

    }
    callChangeAttributes(JSON.stringify(data));
}

let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes();
    return false;
}
