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

/*
function handleLogout() {
    let inputs = document.getElementsByName("logoutInput")
    let data = {
        tokenId: inputs[0].value
    }
    callLogout(JSON.stringify(data));
}
 */