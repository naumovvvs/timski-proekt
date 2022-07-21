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

if(localStorage.getItem("accessToken") === null) {
    $.ajax({
        type: "GET",
        url: "/api/course/all-courses",
        async: false,
        success: function (response) {
            console.log(response);
            response.forEach((element) => {
                let subjectContent = $("#subject-content");
                let card = `<div class="col-md-3 py-3">
                <div class="card" style="width: 18rem;">
                    <img src="${element.imageUrl}" class="card-img-top border-bottom" style="height:150px; width:100%; object-fit: cover;">
                    <div class="card-body" style="height: 100px;">
                        <h5 class="text-primary"><a href="#">${element.name}</a></h5>
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
            console.log(response)
            $.ajax({
                type: "GET",
                url: "/api/student/all-courses/" + response.id,
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
                        <h5 class="text-primary"><a href="#">${element.name}</a></h5>
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