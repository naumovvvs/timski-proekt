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

$("#logout").on("click", function(){
    window.localStorage.clear();
    window.location.reload();
});

let loggedInUserId;
let courseId;
let isModerator = false;
// get current logged-in user
$.ajax({
    url: "/api/user/current",
    type: "GET",
    async: false,
    headers: {
        "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
    },
    success: function (response) {
        loggedInUserId = response.id;
        if(response.roles[0].name == "ROLE_PROFESSOR"){
            isModerator = true;
        }
        $("#loggedUserName").append(response.username);
    },
    error: function (rs) {
        console.error(rs.status);
        console.error(rs.responseText);
    }
});

if(!isModerator) {
    $("#createRoom").css("display", "none");
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
        courseId = response.id;

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
                    <p class="text-primary mx-2 my-0"><a href="http://localhost:8080/room?room=${element.id}&student=${loggedInUserId}&isProfessor=${isModerator}" style="text-decoration: none;">${element.name}</a></p></div>`
                    roomContent.append(card);
                });
            }
        });
    }
});


$("#saveRoom").on("click", function(){
    let roomName = $("#roomName").val();
    let dateStart = $("#dateStart").val();
    let dateEnd = $("#dateEnd").val();
    let roomObject = {
        name: roomName,
        openFrom: dateStart,
        openTo: dateEnd,
        courseId: courseId,
        moderatorId: loggedInUserId
    }
    $.ajax({
        url: "api/room/add",
        type: "POST",
        data: JSON.stringify(roomObject),
        contentType: "application/json",
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            console.log(response);
            $("#roomName").val("");
            $("#dateStart").val("");
            $("#dateEnd").val("");
            $('#addRoomModal').modal('hide');
            location.reload();
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
});