
function callRegisterCompany(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText); break;
                case 401: alert("Please fill in all non-optional fields."); break;
                case 409: alert("User already exists."); break;
                case 403: alert("Invalid email."); break;
                case 411: alert("Invalid password. Please enter 5 or more characters."); break;
                case 417: alert("The passwords are not the same."); break;
                case 404: alert("Invalid postal code."); break;
                case 406: alert("Invalid identification number. Example: 123456789"); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/register/institution", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

}

function callLoginCompany(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(localStorage.getItem("currUser") + " is now logged in."); window.location.href = "../afterLogin/afterLoginCompanyPage.html"; break;
                case 401: alert("Please fill in all non-optional fields."); break;
                case 404: alert("User does not exist."); break;
                case 417: alert("Incorrect password."); break;
                case 403: alert("You must be an institution to login in here."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/login/institution", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

let regFormCompany = document.getElementById("regCompanyFormId");
regFormCompany.onsubmit = () => {
    handleRegisterCompany();
    return false;
}

let loginFormCompany = document.getElementById("loginCompanyFormId");
loginFormCompany.onsubmit = () => {
    handleLoginCompany();
    return false;
}



function handleRegisterCompany() {
    let inputs = document.getElementsByName("regInputCompany")
    let data = {
        username: inputs[0].value,
        nif: inputs[1].value,
        email: inputs[2].value,
        password: inputs[3].value,
        confirmation: inputs[4].value,
        lat: "",
        lon: ""
    }
    callRegisterCompany(JSON.stringify(data));
}

function handleLoginCompany() {
    let inputs = document.getElementsByName("loginInputCompany");
    let givenID = inputs[0].value;
    let data = {
        nifLogin: inputs[0].value,
        passwordLogin: inputs[1].value
    }
    localStorage.setItem("currUser", givenID);
    callLoginCompany(JSON.stringify(data));
}

function goToAfterLoginCompanyPage() {
    window.location.href = "../afterLogin/afterLoginCompanyPage.html";
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