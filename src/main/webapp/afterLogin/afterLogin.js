/*
function callLogout() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); window.location.href = "../initialPage/initialPage.html"; break;
                default: alert("You need to be logged in to execute this operation."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/logout", true);
    xhttp.setRequestHeader("Content-type", "application/json");

    // xhttp.send(null);
     xhttp.send("");

}
 */

/*
function callChangeRole(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); break;
                case 401: alert("Please fill in all fields."); break;
                case 404: alert("Token does not exist."); break;
                case 400: alert("The user with the given token is disabled."); break;
                case 403: alert("User to be changed does not exist."); break;
                case 406: alert("You do not have permissions to execute this operation."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/changeRole", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

}
 */

/*
function callChangePassword(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); break;
                case 401: alert("Please enter a token."); break;
                case 404: alert("Token does not exist."); break;
                case 403: alert("The user with the given token does not exist."); break;
                case 406: alert("The user with the given token is disabled."); break;
                case 409: alert("The old password is incorrect."); break;
                case 411: alert("Invalid new password. Please enter 5 or more characters."); break;
                case 417: alert("The new password and the confirmation password don't match."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/changePassword", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}
 */

/*
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
}
 */

/*
function handleLogout() {
    let inputs = document.getElementsByName("logoutInput")
    let data = {
        tokenId: inputs[0].value
    }
    callLogout(JSON.stringify(data));
}
 */

/*
function handleChangeRole() {
    let inputs = document.getElementsByName("changeRoleInput")
    let data = {
        userToBeChanged: inputs[0].value,
        roleToChange: inputs[1].value
    }
    callChangeRole(JSON.stringify(data));
}
 */

/*
function handleChangePassword() {
    let inputs = document.getElementsByName("changePasswordInput")
    let data = {
        oldPassword: inputs[0].value,
        newPassword: inputs[1].value,
        confirmation: inputs[2].value
    }
    callChangePassword(JSON.stringify(data));
}
 */

/*
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
 */

/*
let changeRoleForm = document.getElementById("changeRoleFormId");
changeRoleForm.onsubmit = () => {
    handleChangeRole();
    return false;
}
 */

/*
let changePasswordForm = document.getElementById("changePasswordFormId");
changePasswordForm.onsubmit = () => {
    handleChangePassword();
    return false;
}
 */

/*
let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes();
    return false;
}
 */

/*
function backToInitialPage() {
    window.location.href = "../initialPage/initialPage.html";
}
 */

function goToMapsPage() {
    window.location.href = "../maps/maps.html";
}

function goToEventsPage() {
    window.location.href = "../event/eventPage.html";
}