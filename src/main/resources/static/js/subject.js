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
let courseCode;
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
        if(response.roles[0].name === "ROLE_PROFESSOR"){
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
    $("#addStudents").css("display", "none");
}

let currentSubject = localStorage.getItem("courseTitle");
$(".course-title").text(currentSubject);

// Display rooms for @{currentSubject}
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
        courseCode = response.code;
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

// Add new room with allowed students (as checked previously)
$("#saveRoom").on("click", function(){
    let roomName = $("#roomName").val();
    let dateStart = $("#dateStart").val();
    let dateEnd = $("#dateEnd").val();
    let allowedStudents = [];
    $(".allowed__students input:checkbox[name=studentIndex]:checked").each(function(){
        allowedStudents.push($(this).attr("id"));
    });
    //console.log(allowedStudents)

    let roomObject = {
        name: roomName,
        openFrom: dateStart,
        openTo: dateEnd,
        courseId: courseId,
        moderatorId: loggedInUserId,
        allowedStudents: allowedStudents
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

// Fetch students enrolled in course, in order to choose the allowed ones while creating a room
$("#createRoom").on("click", function () {
    $(".allowed__students").html("");

    $.ajax({
        type: "GET",
        url: "/api/course/" + courseCode + "/students",
        async: false,
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            console.log(data);
            console.log(response);

            // add the student to the checkbox-list
            for (let i = 0; i < data.length; i++) {
                let checkboxStudent = `<li class="list-group-item">
                                            <input type="checkbox" name="studentIndex" class="custom-control-input" id="${data[i].index}">
                                            <label class="custom-control-label" for="${data[i].index}">
                                                ${data[i].name} <b>${data[i].index}</b>
                                            </label>
                                       </li>`;
                $(".allowed__students").append(checkboxStudent);
            }
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
})

// Get all students, not enrolled in the course with {@courseCode} (in order to show them as a checkbox-list)
$("#addStudents").on("click", function () {
    $(".all__students").html("");

    $.ajax({
        type: "GET",
        url: "/api/course/" + courseCode + "/students/not-in",
        async: false,
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            console.log(data);
            console.log(response);

            // add the student to the checkbox-list
            for (let i = 0; i < data.length; i++) {
                let checkboxStudent = `<li class="list-group-item">
                                            <input type="checkbox" name="studentIndex" class="custom-control-input" id="${data[i].index}">
                                            <label class="custom-control-label" for="${data[i].index}">
                                                ${data[i].name} <b>${data[i].index}</b>
                                            </label>
                                       </li>`;
                $(".all__students").append(checkboxStudent);
            }
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
});

// Enroll the checked students to course with code @{courseCode}
$("#addStudentsToCourse").on("click", function () {
    // get checked students
    let checkedStudents = [];
    $(".all__students input:checkbox[name=studentIndex]:checked").each(function(){
        checkedStudents.push($(this).attr("id"));
    });
    let dtoData = {
        courseCode: courseCode,
        studentIndexes: checkedStudents
    };
    // console.log(dtoData)

    // add multiple students to course
    $.ajax({
        url: "/api/course/add-students",
        type: "POST",
        data: JSON.stringify(dtoData),
        contentType: "application/json",
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            console.log(data);
            console.log(response);
            $('#addStudentsModal').modal('hide');
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
});