function goToPageBefore(){
    let isUserPage = localStorage.getItem("isUserPage");
    if(isUserPage === "true") {
        window.location.href = "../../afterLoginPage.html";
    } else{
        window.location.href = "../../afterLoginCompanyPage.html";
    }
}