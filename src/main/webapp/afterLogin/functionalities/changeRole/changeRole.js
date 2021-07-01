


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

    goToPageBefore();
}

function handleChangeRole() {
    let inputs = document.getElementsByName("changeRoleInput")
    let data = {
        userToBeChanged: inputs[0].value,
        roleToChange: inputs[1].value
    }
    callChangeRole(JSON.stringify(data));
}

let changeRoleForm = document.getElementById("changeRoleFormId");
changeRoleForm.onsubmit = () => {
    handleChangeRole();
    return false;
}