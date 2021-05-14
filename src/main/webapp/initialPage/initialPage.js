

function callRegister(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
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
        if (this.readyState === 4 && this.status === 200) {
            alert(this.responseText);
        }
    };
    xhttp.open("POST", "/rest/login/user", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);

}



function handleRegister() {
    let inputs = document.getElementsByName("regInput")
    let data = {
        username: inputs[0].value,
        email: inputs[1].value,
        password: inputs[2].value,
        confirmation: inputs[3].value,
        mobile: inputs[4].value,
        address: inputs[5].value,
        postal: inputs[6].value
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

