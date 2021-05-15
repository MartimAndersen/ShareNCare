function callLogout(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
            window.location.href = "../initialPage/initialPage.html";
        } else if (this.readyState === 4 && this.status === 400) {
            alert("Wrong parameters.");
        }
    };
    xhttp.open("POST", "/rest/loggedIn/logout", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

}

function callChangeRole(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
        } else if (this.readyState === 4 && this.status !== 200) {
            alert("Wrong parameters.");
        }
    };
    xhttp.open("POST", "/rest/loggedIn/changeRole", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

}

function callChangePassword(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
        } else if (this.readyState === 4 && this.status !== 200) {
            alert("Wrong parameters.");
        }
    };
    xhttp.open("POST", "/rest/loggedIn/changePassword", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function callChangeAttributes(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
        } else if (this.readyState === 4 && this.status !== 200) {
            alert("Wrong parameters.");
        }
    };
    xhttp.open("POST", "/rest/loggedIn/changeAttributes", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}


function handleLogout() {
    let inputs = document.getElementsByName("logoutInput")
    let data = {
        tokenId: inputs[0].value
    }
    callLogout(JSON.stringify(data));
}

function handleChangeRole() {
    let inputs = document.getElementsByName("changeRoleInput")
    let data = {
        userToBeChanged: inputs[0].value,
        roleToChange: inputs[1].value,
        tokenIdChangeRole: inputs[2].value
    }
    callChangeRole(JSON.stringify(data));
}

function handleChangePassword() {
    let inputs = document.getElementsByName("changePasswordInput")
    let data = {
        tokenIdChangePassword: inputs[0].value,
        oldPassword: inputs[1].value,
        newPassword: inputs[2].value,
        confirmation: inputs[3].value
    }
    callChangePassword(JSON.stringify(data));
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
        newPostal: inputs[6].value,
        tokenIdChangeAttributes: inputs[7].value
    }
    callChangeAttributes(JSON.stringify(data));
}

let logoutForm = document.getElementById("logoutFormId");
logoutForm.onsubmit = () => {
    handleLogout();
    return false;
}

let changeRoleForm = document.getElementById("changeRoleFormId");
changeRoleForm.onsubmit = () => {
    handleChangeRole();
    return false;
}

let changePasswordForm = document.getElementById("changePasswordFormId");
changePasswordForm.onsubmit = () => {
    handleChangePassword();
    return false;
}

let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes();
    return false;
}

function backToInitialPage() {
    window.location.href = "../initialPage/initialPage.html";
}

function goToMapsPage() {
    window.location.href = "../maps/maps.html";
}