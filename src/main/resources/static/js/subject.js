if(localStorage.getItem("accessToken") === null){
    $("#login").removeClass("d-none");
    $("#logout").addClass("d-none");
    $("#loggedUserName").addClass("d-none");
    $("#notLogged").removeClass("d-none");
}else{
    $("#login").addClass("d-none");
    $("#logout").removeClass("d-none");
    $("#notLogged").addClass("d-none");
    $("#loggedUserName").removeClass("d-none");
}

let currentSubject = localStorage.getItem("courseTitle");
$(".course-title").text(currentSubject);

$.ajax({
    type: "GET",
    url: "/api/course/name/" + currentSubject,
    async: false,
    headers: {
        "Authorization":
            "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
    },
    success: function (response) {
        $.ajax({
            type: "GET",
            url: "/api/course/all-rooms/" + response.code,
            async: false,
            headers: {
                "Authorization":
                    "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
            },
            success: function (response) {
                let roomContent = $(".room-container");
                response.forEach((element) => {
                    let card = `<div class="d-flex align-items-center my-3"><img src="https://ispiti.finki.ukim.mk/theme/image.php/classic/bigbluebuttonbn/1637703842/icon">
            <p class="text-primary mx-2 my-0"><a href="#" style="text-decoration: none;">${element.name}</a></p></div>`
                    roomContent.append(card);
                });
            }
        });
    }
});

