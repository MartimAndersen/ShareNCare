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

function backToInitialPage() {
    window.location.href = "../initialPage/initialPage.html";
}