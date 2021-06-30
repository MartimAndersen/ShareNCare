function backToInitialPage() {
    window.location.href = "../initialPage/initialPage.html";
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

function logout() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); backToInitialPage(); break;
                default: alert("You need to be logged in to execute this operation."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/logout", true);
    xhttp.setRequestHeader("Content-type", "application/json");

    xhttp.send("");
}