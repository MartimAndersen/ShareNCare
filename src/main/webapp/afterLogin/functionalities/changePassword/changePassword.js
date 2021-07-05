var isUserPage = localStorage.getItem("isUserPage");
function goToPageBefore(){
    if(isUserPage === "true") {
        window.location.href = "../../afterLoginPage.html";
    } else{
        window.location.href = "../../afterLoginCompanyPage.html";
    }
}

function callChangePassword(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); goToPageBefore(); break;
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
    if(isUserPage === "true") {
        xhttp.open("POST", "/rest/loggedIn/changePassword", true);
    } else{
        xhttp.open("POST", "/rest/loggedIn/changePasswordCompany", true);
    }
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);


}

function handleChangePassword() {
    let inputs = document.getElementsByName("changePasswordInput")
    let data = {
        oldPassword: inputs[0].value,
        newPassword: inputs[1].value,
        confirmation: inputs[2].value
    }
    callChangePassword(JSON.stringify(data));
}

let changePasswordForm = document.getElementById("changePasswordFormId");
changePasswordForm.onsubmit = () => {
    handleChangePassword();
    return false;
}

function togglePassword(id1, id2) {
    let password = document.getElementById(id1);
    let image = document.getElementById(id2);
    if (password.type === "password") {
        password.type = "text";
        image.setAttribute("class","fas fa-eye");
    } else {
        password.type = "password";
        image.setAttribute("class","fas fa-eye-slash");
    }
}
