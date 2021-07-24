var currUser;

function goToPageBefore(){
    window.location.href = "../../afterLoginPage.html";
}

function fillTagsList(jsonResponse) {

   	let tags = jsonResponse.tags;
    var arrayLength = tags.length;
    for (var i = 0; i < arrayLength; i++) {
     s = tags[i];
     currTagId = "tag" + s;

     document.getElementById(currTagId).checked = true;
}


}


function userAttributes(jsonResponse) {
    currUser = jsonResponse;


  document.getElementById("newEmail").value = currUser.email;
  //document.getElementById("newEmail").value = localStorage.getItem(obj[3].value);
  document.getElementById("newLandLine").value = currUser.landLine;
  document.getElementById("newMobile").value = currUser.mobile;
  document.getElementById("newAddress").value = currUser.address;
  document.getElementById("newSecondAddress").value = currUser.secondAddress;
  document.getElementById("newPostal").value = currUser.zipCode;
  document.getElementById("description").value = currUser.bio;

   profile = currUser.profileType;
   if(profile == "public"){
       document.getElementById('newProfile').checked = true;
   }
   if(profile == "private"){
    document.getElementById('newProfile1').checked = true;
   }

  fillTagsList(jsonResponse);


}
function callUserAttributes(){
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            let a = this.responseText;
            switch (this.status) {
                case 200:
                    jsonResponse = JSON.parse(xhttp.responseText);
                    userAttributes(jsonResponse);
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
                default:
                    alert("Wrong parameters.");
                    break;
            }
        }
    };
    xhttp.open("GET", "/rest/loggedIn/getCurrentUser", true);
    xhttp.send();
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
function fillTagsListToSend(inputs) {
    let tagsList = [];
    let currTagId = "";
    for (let counter = 0; counter <=5; counter++) {
        currTagId = "tag" + counter;
        if (document.getElementById(currTagId).checked) {
            // tagsList.push(inputs[t].value);
            tagsList.push(counter);
        }
    }
    return tagsList;
}

function fileToByteArray(file) {
    return new Promise((resolve, reject) => {
        try {
            let reader = new FileReader();
            let fileByteArray = [];
            reader.readAsArrayBuffer(file);
            reader.onloadend = (evt) => {
                if (evt.target.readyState == FileReader.DONE) {
                    let arrayBuffer = evt.target.result,
                        array = new Uint8Array(arrayBuffer);
                    for (byte of array) {
                        fileByteArray.push(byte);
                    }
                }
                resolve(fileByteArray);
            }
        }
        catch (e) {
            reject(e);
        }
    })
}
/*
var bytes;
async function getByteArray() {
    //Get file from your input element
    let myFile = document.getElementById('image').files[0];

    //Wait for the file to be converted to a byteArray
    let byteArray = await fileToByteArray(myFile);

    //Do something with the byteArray
    return byteArray;
    console.log(byteArray);

}

//atob(decodeURIComponent(dataToBeDecoded)
function base64ToArrayBuffer(base64) {
    var binary_string = window.atob(btoa((base64)));
    var len = binary_string.length;
    var bytes = new Uint8Array(len);
    for (var i = 0; i < len; i++) {
        bytes[i] = binary_string.charCodeAt(i);
    }
    return bytes.buffer;
}
*/
var change;
function transform(base){
console.log(base);
    change = base;
}
function handleChangeAttributes(bytesPic) {
    let inputs = document.getElementsByName("changeAttributesInput")
    let radioButton = ""
    if (document.getElementById('newProfile').checked) {
        radioButton = inputs[1].value
    }
    if (document.getElementById('newProfile1').checked) {
        radioButton = inputs[2].value
    }
   // const inputNode = inputs[0].value
   // url = document.getElementById('image');
    //console.log(url);

    //var bytes = bytesPic;
    /*
      var files = document.getElementById('image').files;
      if (files.length > 0) {
        getBase64(files[0]);

*/

  //getByteArray();
  //console.log(bytes);
//b = JSON.stringify(bytes);
//console.log(b);
   //bytes = base64ToArrayBuffer(fileContent);
   // const byteFile = getAsByteArray(inputNode)
    let data = {
        email: inputs[0].value,
        profileType: radioButton,
        landLine: inputs[3].value,
        mobile: inputs[4].value,
        address: inputs[5].value,
        secondAddress: inputs[6].value,
        zipCode: inputs[7].value,
        bio: inputs[8].value,
        tags: fillTagsListToSend(inputs),
        events: currUser.events,
        profilePic: ""
    }
    callChangeAttributes(JSON.stringify(data));
}
function handleChangeAttributesPic(){
    var fileInput = document.getElementById('image');

    var reader = new FileReader();
    reader.readAsDataURL(fileInput.files[0]);

    reader.onload = function () {
        transform(reader.result);
        handleChangeAttributes(reader.result)
    	console.log(reader.result);//base64encoded string
    };
    reader.onerror = function (error) {
    	console.log('Error: ', error);
    };
}

let changeAttributesForm = document.getElementById("changeAttributesFormId");
changeAttributesForm.onsubmit = () => {
    handleChangeAttributes("");
    return false;
}




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


/*
document.addEventListener("DOMContentLoaded", () => {
    const recentImageDataUrl = localStorage.getItem("userPic");
    if (recentImageDataUrl) {
        document.querySelector("#imgPreview").setAttribute("src", recentImageDataUrl);

        let b = recentImageDataUrl.split("base64,")[1];

        let a = base64ToArrayBuffer(b);

    }
});
*/


