function backToInitialPage() {
    window.location.href = "../welcomePage/welcomePage.html";
}

function goToAboutUs() {
    localStorage.setItem("isUserPage","false");
    window.location.href = "functionalities/aboutUs/aboutUs.html";
}

function goToChangePassword() {
    localStorage.setItem("isUserPage","false");
    window.location.href = "functionalities/changePassword/changePassword.html";
}

function goToChangeAttributes() {
    window.location.href = "functionalities/changeAttributes/changeAttributesCompany.html";
}

function goToCreateEvent() {
    window.location.href = "functionalities/createEvent/createEvent.html";
}

function goToDeleteAccount() {
    localStorage.setItem("isUserPage","false");
    window.location.href = "functionalities/deleteAccount/deleteInstitutionAccount.html";
}

function goToMyEvent() {
    window.location.href = "functionalities/myEventsCompany/myEvents.html";
}

function logout() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: backToInitialPage(); break;
                default: alert("You need to be logged in to execute this operation."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/logout", true);
    xhttp.setRequestHeader("Content-type", "application/json");

    xhttp.send("");
}

window.onload = function() {
    document.getElementById("header2Id").innerHTML = "Welcome " + localStorage.getItem('currUser');
}