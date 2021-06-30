function backToInitialPage() {
    window.location.href = "../initialPage/initialPage.html";
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

function goToLogout() {
    window.location.href = "functionalities/logout/logout.html";
}