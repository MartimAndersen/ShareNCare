
function callRegister(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: alert(this.responseText);  break;
                case 401: alert("Please fill in all non-optional fields."); break;
                case 409: alert("User already exists."); break;
                case 403: alert("Invalid email."); break;
                case 411: alert("Invalid password. Please enter 5 or more characters."); break;
                case 417: alert("The passwords are not the same."); break;
                case 404: alert("Invalid postal code."); break;
                case 406: alert("Invalid mobile phone number."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/register/backofficega", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

    // fetch("/rest/register/user")
    //     .then(response =>{
    //     let res;
    //     if(response.status===200){
    //         res = "User registered successfully.";
    //     } else{
    //         res ="Something went wrong.";
    //     }
    //     alert(res);
    // })

}

function callLogin(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            switch (this.status) {
                case 200: window.location.href = "../afterLogin/afterLoginBackOfficePage.html"; break;
                case 401: alert("Please fill in all non-optional fields."); break;
                case 404: alert("User does not exist."); break;
                case 417: alert("Incorrect password."); break;
                case 403: alert("You cannot login as an institution here."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/login/backOfficega", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

    // let result = false;
    // fetch("/rest/login/user")
    //     .then(response =>{
    //     if(response.status===200){
    //         result = true;
    //     }
    // })


}

let regForm = document.getElementById("regFormId");
regForm.onsubmit = () => {
    handleRegister();
    return false;
}

let loginForm = document.getElementById("loginFormId");
loginForm.onsubmit = () => {
    handleLogin();
    return false;
}

function handleRegister() {
    let inputs = document.getElementsByName("regInput")
    let data = {
        username: inputs[0].value,
        email: inputs[1].value,
        password: inputs[2].value,
        confirmation: inputs[3].value
    }
    callRegister(JSON.stringify(data));
}

function handleLogin() {
    let inputs = document.getElementsByName("loginInput")
    let givenUsername = inputs[0].value;
    let data = {
        usernameLogin: inputs[0].value,
        passwordLogin: inputs[1].value
    }
    localStorage.setItem("currUser", givenUsername);
    callLogin(JSON.stringify(data));
}

function goToAfterLoginPage() {
    window.location.href = "../afterLogin/afterLoginPage.html";
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