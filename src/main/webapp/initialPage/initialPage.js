function callRegister(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {

            if (this.status === 200) {
                alert(this.responseText);
            } else if (this.status === 409) {
                alert("User already exists.");
            } else if (this.status === 403) {
                alert("Invalid email.");
            } else if (this.status === 401) {
                alert("Invalid password. Please enter 5 or more characters.");
            } else if (this.status === 417) {
                alert("The passwords are not the same.");
            } else if (this.status === 404) {
                alert("Invalid postal code.");
            } else if (this.status === 406) {
                alert("Invalid mobile phone number.");
            } else if (this.status !== 200) {
                alert("Wrong parameters.");
            }
            // switch (this.status) {
            //     case 200: alert(this.responseText);
            //     case 409: alert("User already exists.");
            //     case 403: alert("Invalid email.");
            //     case 401: alert("Invalid password. Please enter 5 or more characters.");
            //     case 417: alert("The passwords are not the same.");
            //     case 404: alert("Invalid postal code.");
            //     case 406: alert("Invalid mobile phone number.");
            //     default: alert("Wrong parameters.");
            // }
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

            // let divs = document.getElementsByClassName("loginDiv");
            // divs[0].style.visibility = "hidden";
            // divs[1].style.visibility = "hidden";
            window.location.href = "../afterLogin/afterLogin.html";

        } else if (this.readyState === 4 && this.status === 403) {
            alert("Wrong parameters.");
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


function handleRegister() {
    let inputs = document.getElementsByName("regInput")
    let data = {
        username: inputs[0].value,
        email: inputs[1].value,
        password: inputs[2].value,
        confirmation: inputs[3].value,
        mobile: inputs[4].value,
        landLine: inputs[5].value,
        address: inputs[6].value,
        secondAddress: inputs[7].value,
        postal: inputs[8].value,
        profileType: inputs[9].value
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