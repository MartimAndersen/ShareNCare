

function callRegister(data) {
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
                case 406: alert("Invalid mobile phone number."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/register/user", true);
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
                case 200: alert(this.responseText); window.location.href = "../afterLogin/afterLogin.html"; break;
                case 401: alert("Please fill in all non-optional fields."); break;
                case 404: alert("User does not exist."); break;
                case 417: alert("Incorrect password."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/login/user", true);
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
                case 200: alert(this.responseText); window.location.href = "../afterLogin/afterLogin.html"; break;
                case 401: alert("Please fill in all non-optional fields."); break;
                case 404: alert("User does not exist."); break;
                case 417: alert("Incorrect password."); break;
                default: alert("Wrong parameters."); break;
            }
        }
    };
    xhttp.open("POST", "/rest/login/institution", true);
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
    let data = {
        usernameLogin: inputs[0].value,
        passwordLogin: inputs[1].value
    }
    callLogin(JSON.stringify(data));
}


function handleRegisterCompany() {
    let inputs = document.getElementsByName("regInputCompany")
    let data = {
        username: inputs[0].value,
        nif: inputs[1].value,
        email: inputs[2].value,
        password: inputs[3].value,
        confirmation: inputs[4].value
    }
    callRegisterCompany(JSON.stringify(data));
}

function handleLoginCompany() {
    let inputs = document.getElementsByName("loginInputCompany")
    let data = {
        nifLogin: inputs[0].value,
        passwordLogin: inputs[1].value
    }
    callLoginCompany(JSON.stringify(data));
}

function goToAfterLoginPage() {
    window.location.href = "../afterLogin/afterLogin.html";
}

// function toggleVisibility() {
//     let divs = document.getElementsByClassName("loginDiv")
//     for (let i = 0; i < divs.length; i++) {
//         if(divs[i].style.visibility === "hidden"){
//             divs[i].style.visibility = "visible"
//         } else{
//             divs[i].style.visibility = "hidden"
//         }
//     }
// }