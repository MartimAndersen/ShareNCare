function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function callChangeAttributes(data) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            let a = this.responseText;
            switch (this.status) {
                case 200:
                    alert("Properties changed.");
                    break;
                case 411:
                    alert("Please enter at least one new attribute.");
                    break;
                case 401:
                    alert("You need to be logged in to execute this operation.");
                    break;
                case 404:
                    alert("Token does not exist.");
                    break;
                case 403:
                    alert("The user with the given token does not exist.");
                    break;
                case 406:
                    alert("The user with the given token is disabled.");
                    break;
                case 412:
                    alert("Invalid email.");
                    break;
                case 409:
                    alert("Invalid postal code.");
                    break;
                case 417:
                    alert("Invalid mobile phone.");
                    break;
                default:
                    alert("Wrong parameters.");
                    break;
            }
        }
    };
    xhttp.open("POST", "/rest/loggedIn/changeAttributesWeb", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(data);
}

function handleChangeAttributes() {
    let inputs = document.getElementsByName("changeAttributesInput")
    let radioButton = ""
    if (document.getElementById('newProfile').checked) {
        radioButton = inputs[1].value
    }
    if (document.getElementById('newProfile1').checked) {
        radioButton = inputs[2].value
    }
    let data = {
        newEmail: inputs[0].value,
        newProfileType: radioButton,
        newLandLine: inputs[3].value,
        newMobile: inputs[4].value,
        newAddress: inputs[5].value,
        newSecondAddress: inputs[6].value,
        newPostal: inputs[7].value,
        newBio: inputs[8].value
    }
    callChangeAttributes(JSON.stringify(data));
}

let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes();
    return false;
}

/**

document.querySelectorAll(".drop-zone__input").forEach((inputElement) => {
    const dropZoneElement = inputElement.closest(".drop-zone");

    dropZoneElement.addEventListener("click", (e) => {
        inputElement.click();
    });

    inputElement.addEventListener("change", (e) => {
        if (inputElement.files.length) {
            updateThumbnail(dropZoneElement, inputElement.files[0]);
        }
    });

    dropZoneElement.addEventListener("dragover", (e) => {
        e.preventDefault();
        dropZoneElement.classList.add("drop-zone--over");
    });

    ["dragleave", "dragend"].forEach((type) => {
        dropZoneElement.addEventListener(type, (e) => {
            dropZoneElement.classList.remove("drop-zone--over");
        });
    });

    dropZoneElement.addEventListener("drop", (e) => {
        e.preventDefault();

        if (e.dataTransfer.files.length) {
            inputElement.files = e.dataTransfer.files;
            updateThumbnail(dropZoneElement, e.dataTransfer.files[0]);
        }

        dropZoneElement.classList.remove("drop-zone--over");
    });
});

 //
 // Updates the thumbnail on a drop zone element.
 //
 // @param {HTMLElement} dropZoneElement
 // @param {File} file
 //
function updateThumbnail(dropZoneElement, file) {
    let thumbnailElement = dropZoneElement.querySelector(".drop-zone__thumb");

    // First time - remove the prompt
    if (dropZoneElement.querySelector(".drop-zone__prompt")) {
        dropZoneElement.querySelector(".drop-zone__prompt").remove();
    }

    // First time - there is no thumbnail element, so lets create it
    if (!thumbnailElement) {
        thumbnailElement = document.createElement("div");
        thumbnailElement.classList.add("drop-zone__thumb");
        dropZoneElement.appendChild(thumbnailElement);
    }

    thumbnailElement.dataset.label = file.name;

    // Show thumbnail for image files
    if (file.type.startsWith("image/")) {
        const reader = new FileReader();

        reader.readAsDataURL(file);
        reader.onload = () => {
            thumbnailElement.style.backgroundImage = `url('${reader.result}')`;
            localStorage.setItem("userPic", reader.result);
        };
    } else {
        thumbnailElement.style.backgroundImage = null;
    }

}

function base64ToArrayBuffer(base64) {
    var binary_string = window.atob(base64);
    var len = binary_string.length;
    var bytes = new Uint8Array(len);
    for (var i = 0; i < len; i++) {
        bytes[i] = binary_string.charCodeAt(i);
    }
    return bytes.buffer;
}

document.addEventListener("DOMContentLoaded", () => {
    const recentImageDataUrl = localStorage.getItem("userPic");
    if (recentImageDataUrl) {
        document.querySelector("#imgPreview").setAttribute("src", recentImageDataUrl);

        let b = recentImageDataUrl.split("base64,")[1];

        let a = base64ToArrayBuffer(b);

    }
});

*/
