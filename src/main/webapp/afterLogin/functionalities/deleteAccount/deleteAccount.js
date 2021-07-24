function callDeleteAccount(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); goToPageBefore(); break;
                case 401: alert("Please enter a token."); break;
                case 404: alert("Token does not exist."); break;
                case 403: alert("The user with the given token does not exist."); break;
                case 406: alert("The user with the given token is disabled."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/removeUser", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function handleDeleteAccount() {
    let inputs = document.getElementsByName("deleteAccountInput")
    let data = {
        userToBeDeleted: inputs[0].value
    }
    callDeleteAccount(JSON.stringify(data));
}

let deleteAccountForm = document.getElementById("deleteAccountFormId");
deleteAccountForm.onsubmit = () => {
    handleDeleteAccount();
    return false;
}

function goToPageBefore() {
    window.location.href = "../../../welcomePage/welcomePage.html";
}