var currUser;


function userAttributes(jsonResponse) {
    currUser = jsonResponse;

  document.getElementById("newEmail").value = currUser.email;
  //document.getElementById("newEmail").value = localStorage.getItem(obj[3].value);
  document.getElementById("newLandLine").value = currUser.landLine;
  document.getElementById("newMobile").value = currUser.mobile;
  document.getElementById("newAddress").value = currUser.address;
  document.getElementById("newPostal").value = currUser.zipCode;
  document.getElementById("fax").value = currUser.fax;
  document.getElementById("website").value = currUser.website;
  document.getElementById("instagram").value = currUser.instagram;
  document.getElementById("twitter").value = currUser.twitter;
  document.getElementById("facebook").value = currUser.facebook;
  document.getElementById("youtube").value = currUser.youtube;
  document.getElementById("description").value = currUser.bio;




}
function callUserAttributes(){
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            let a = this.responseText;
            switch (this.status) {
                case 200:
                    jsonResponse = JSON.parse(xhttp.responseText);
                    userAttributes(jsonResponse);
                    break;
                case 401:
                    alert("You need to be logged in to execute this operation.");
                    break;
                case 404:
                    alert("Token does not exist.");
                    break;
                case 403:
                    alert("The user with the given token does not exist.");
                    break;
                default:
                    alert("Wrong parameters.");
                    break;
            }
        }
    };
    xhttp.open("GET", "/rest/loggedInInstitution/getCurrentUser", true);
    xhttp.send();
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
                case 405: alert("Invalid Postal Code."); break;
                case 412: alert("Invalid email."); break;
                case 409: alert("Invalid fax."); break;
                case 417: alert("Invalid mobile phone or LandLine ."); break;
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
        profilePic: currUser.profilePic,
        // members: [],
        events: currUser.events,
        bio: inputs[11].value,

    }
    callChangeAttributes(JSON.stringify(data));
}

let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes();
    return false;
}
