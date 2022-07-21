
if(localStorage.getItem("accessToken") === null){
    $("#login").removeClass("d-none");
    $("#logout").addClass("d-none");
}else{
    $("#login").addClass("d-none");
    $("#logout").removeClass("d-none");
}

$("#logout").on("click", function(){
    window.localStorage.clear();
    window.location.reload();
});