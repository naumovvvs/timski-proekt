if (localStorage.getItem("accessToken") === null) {
    $("#login").removeClass("d-none");
    $("#logout").addClass("d-none");
    $("#loggedUserName").addClass("d-none");
    $("#notLogged").removeClass("d-none");
} else {
    $("#login").addClass("d-none");
    $("#logout").removeClass("d-none");
    $("#notLogged").addClass("d-none");
    $("#loggedUserName").removeClass("d-none");
}

let isModerator = false;
let currentLoggedInUser;
let currentLoggedInUserRole;

// get current logged-in user
$.ajax({
    url: "/api/user/current",
    type: "GET",
    async: false,
    headers: {
        "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
    },
    success: function (response) {
        currentLoggedInUser = response;
        currentLoggedInUserRole = response.roles[0].name;
        if (currentLoggedInUserRole === "ROLE_PROFESSOR" || currentLoggedInUserRole === "ROLE_ADMIN") {
            isModerator = true;
        }
    },
    error: function (rs) {
        console.error(rs.status);
        console.error(rs.responseText);
    }
});

function formatDateAndTime(dateTime) {
    if (dateTime == null) return "/";
    let parts = dateTime.split("T");
    return parts[0] + " at " + parts[1];
}

function populateStudentsInRoomTable(data) {
    let tbody = $("#student-table tbody");

    for (let i = 0; i < data.length; i++) {
        let customHTML = $(`<tr></tr>`);

        $(customHTML).append(`<th scope="row">${i + 1}</th>`);
        $(customHTML).append(`<td>${data[i].name}</td>`);
        $(customHTML).append(`<td>${formatDateAndTime(data[i].startTime)}</td>`);
        $(customHTML).append(`<td>${formatDateAndTime(data[i].endTime)}</td>`);
        $(customHTML).append(`<td>${data[i].index}</td>`);
        $(customHTML).append(`<td>${formatDateAndTime(data[i].enterTime)}</td>`);
        $(customHTML).append(`<td>${formatDateAndTime(data[i].leaveTime)}</td>`);
        $(customHTML).append(`<td>${data[i].status}</td>`);

        console.log(customHTML)
        $(tbody).append(customHTML);
    }
    $("#student-table tbody").replaceWith(tbody);
}

function populateMessagesInRoomTable(data) {
    let tbody = $("#msg-table tbody");

    for (let i = 0; i < data.length; i++) {
        let customHTML = $(`<tr></tr>`);

        $(customHTML).append(`<th scope="row">${i + 1}</th>`);
        $(customHTML).append(`<td>${data[i].name}</td>`);
        $(customHTML).append(`<td>${data[i].content}</td>`);
        $(customHTML).append(`<td>${data[i].index}</td>`);
        $(customHTML).append(`<td>${formatDateAndTime(data[i].sentAt)}</td>`);

        console.log(customHTML)
        $(tbody).append(customHTML);
    }
    $("#msg-table tbody").replaceWith(tbody);
}

function populateInterruptionsInRoomTable(data) {
    let tbody = $("#interruption-table tbody");

    for (let i = 0; i < data.length; i++) {
        let customHTML = $(`<tr></tr>`);

        $(customHTML).append(`<th scope="row">${i + 1}</th>`);
        $(customHTML).append(`<td>${data[i].name}</td>`);
        $(customHTML).append(`<td>${data[i].index}</td>`);
        $(customHTML).append(`<td>${formatDateAndTime(data[i].interruptionTime)}</td>`);
        $(customHTML).append(`<td>${data[i].totalDurationSeconds}</td>`);
        $(customHTML).append(`<td>${data[i].status}</td>`);

        console.log(customHTML)
        $(tbody).append(customHTML);
    }
    $("#interruption-table tbody").replaceWith(tbody);
}

if (isModerator) {
    let url = window.location.pathname
    let id = url.split("/")[2]
    // console.log(id)

    $.ajax({
        type: "GET",
        url: "/api/room/report/" + id,
        async: false,
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            populateStudentsInRoomTable(data);
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });

    $.ajax({
        type: "GET",
        url: "/api/room/msg-report/" + id,
        async: false,
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            populateMessagesInRoomTable(data);
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });

    $.ajax({
        type: "GET",
        url: "/api/room/interruption-report/" + id,
        async: false,
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            populateInterruptionsInRoomTable(data);
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
}