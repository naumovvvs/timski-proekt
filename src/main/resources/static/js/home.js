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

let professorId = null;
let isAdmin = false;

if(localStorage.getItem("accessToken") === null) {
    $.ajax({
        type: "GET",
        url: "/api/course/all-courses",
        async: false,
        success: function (response) {
            response.forEach((element) => {
                let subjectContent = $("#subject-content");
                let card = `<div class="col-md-3 py-3">
                <div class="card" style="width: 18rem;">
                    <img src="${element.imageUrl}" class="card-img-top border-bottom" style="height:150px; width:100%; object-fit: cover;">
                    <div class="card-body" style="height: 100px;">
                        <h5 class="text-primary course-title"><a href="http://localhost:8080/subject">${element.name}</a></h5>
                    </div>
                </div>
            </div>`
                subjectContent.append(card);
            });
        }
    });
}else {
    $.ajax({
        type: "GET",
        url: "/api/user/current",
        async: false,
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (response) {
            let url;
            let userRole = response.roles[0].name;
            if (userRole === "ROLE_ADMIN") {
                $("#addCourse").removeClass("d-none");
                url = "/api/course/all-courses/";
                isAdmin = true;
            }
            else if(userRole === "ROLE_PROFESSOR") {
                $("#addCourse").removeClass("d-none");
                url = "/api/professor/all-courses/";
                professorId = response.id;
            }
            else {
                $("#addCourse").addClass("d-none");
                url = "/api/student/all-courses/";
            }
            $.ajax({
                type: "GET",
                url: isAdmin ? url : url + response.id,
                async: false,
                headers: {
                    "Authorization":
                        "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
                },
                success: function (response) {
                    response.forEach((element) => {
                        let subjectContent = $("#subject-content");
                        let card = `<div class="col-md-3 py-3">
                        <div class="card" style="width: 18rem;">
                            <img src="${element.imageUrl}" class="card-img-top border-bottom" style="height:150px; width:100%; object-fit: cover;">
                            <div class="card-body" style="height: 100px;">
                                <h5 class="text-primary course-title"><a href="http://localhost:8080/subject">${element.name}</a></h5>
                            </div>
                        </div>
                        </div>`
                        subjectContent.append(card);
                    });
                }
            });
            $("#loggedUserName").append(response.username);
        }
    });
}

$("#logout").on("click", function(){
    window.localStorage.clear();
    window.location.reload();
});

$(".course-title").on("click", function(){
    window.localStorage.setItem('courseTitle', $(this).text());
});

$("#saveCourse").on("click", function(){
    let name = $("#courseName").val();
    let code = $("#courseCode").val();
    let imgURL = $("#courseImg").val();
    let semester = $('#semester').find(":selected").val();
    let courseObject = {
        name: name,
        code: code,
        imageUrl: imgURL,
        semester: semester
    }
    $.ajax({
        url: "/api/course/create",
        type: "POST",
        data: JSON.stringify(courseObject),
        contentType: "application/json",
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data) {
            if (professorId !== null) {
                addCourseToProfessor(data, professorId);
            } else {
                if (isAdmin) {
                    // assign the course to the checked professors
                    $(".professors input:checkbox[name=profId]:checked").each(function () {
                        addCourseToProfessor(data, $(this).attr("id"));
                    });
                }
            }
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
});

let addCourseToProfessor = (course, moderator) => {
    let courseToProfessor = {
        courseName: course.name,
        userId: moderator
    }

    $.ajax({
        url: "/api/professor/add-course",
        type: "POST",
        data: JSON.stringify(courseToProfessor),
        contentType: "application/json",
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function () {
            $("#courseName").val("");
            $("#courseCode").val("");
            $("#courseImg").val("");
            $('#semester').selectedIndex = -1;
            $('#addCourseModal').modal('hide');
            location.reload();
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
}

// Get all professors (in order to show them as a checkbox-list)
$("#addCourse").on("click", function () {
    if (isAdmin) {
        $(".professors").html("").parent().removeClass("d-none");

        $.ajax({
            type: "GET",
            url: "/api/professor",
            async: false,
            headers: {
                "Authorization":
                    "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
            },
            success: function (data, response) {
                console.log(data);
                console.log(response);

                // add the professors to the checkbox-list
                for (let i = 0; i < data.length; i++) {
                    let checkboxProfessor = `<li class="list-group-item">
                                            <input type="checkbox" name="profId" class="custom-control-input" id="${data[i].id}">
                                            <label class="custom-control-label" for="${data[i].id}">
                                                ${data[i].name}
                                            </label>
                                       </li>`;
                    $(".professors").append(checkboxProfessor);
                }
            },
            error: function (rs) {
                console.error(rs.status);
                console.error(rs.responseText);
            }
        });
    }
});

// Initially hide the professors checkbox-list
$(".professors").parent().addClass("d-none");