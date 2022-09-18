// function for waiting x seconds
const delay = (n) => new Promise( r => setTimeout(r, n*1000));

let handleMemberJoined = async (MemberId) => {
    // add user to participants list
    await addMemberToDom(MemberId);

    // get all members in the channel
    let members = await channel.getMembers();

    // update the participants list
    await updateMemberTotal(members);

    // add welcome message to DOM only if user is of type 'professor'
    if(currentLoggedInStudent==="") {
        // get user's name by key MemberId from storage
        let {name} = await rtmClient.getUserAttributesByKeys(MemberId, ['name']);
        // add bot message for welcoming user
        await addBotMessageToDom(`Welcome to the room ${name}! 👋`);
    }
}

let addMemberToDom = async (MemberId) => {
    // get the name value from this specific user
    // {name} destructure the response
    let {name} = await rtmClient.getUserAttributesByKeys(MemberId, ['name']);

    // temporary variable
    let pom;
    // get student object and check if present
    let {student} = await rtmClient.getUserAttributesByKeys(MemberId, ['student']);
    // parse the student string
    let studentObj = JSON.parse(student);

    // get list of participants
    let membersWrapper = document.getElementById('member__list');
    let memberItem;

    // check if object parsed successfully, if not it means that the user is not a student, but a professor
    if(studentObj === "" || studentObj === null) {
        pom = "(MOD)";
        memberItem = `<div class="member__wrapper" id="member__${MemberId}__wrapper">
                        <span class="mod__icon"></span>
                        <p class="member_name">${name} ${pom}</p>
                      </div>`;
    } else {
        pom = "(" + studentObj.index + ")";
        memberItem = `<div class="member__wrapper" id="member__${MemberId}__wrapper">
                        <span class="gray__icon"></span>
                        <p class="member_name">${name} ${pom}</p>
                        <ul class="dropdown-menu">
                            <li onclick="changeStudentStatus(event)" class="student_status"><button id="IDENTIFIED" style="background-color: #2aca3e">Identify</button></li>
                            <li onclick="changeStudentStatus(event)" class="student_status"><button id="SUSPICIOUS" style="background-color: #FFA500">Watch</button></li>
                            <li onclick="changeStudentStatus(event)" class="student_status"><button id="BLOCKED" style="background-color: #ff0000">Block</button></li>
                        </ul>
                      </div>`;
    }

    // add new participant to participants list
    membersWrapper.insertAdjacentHTML('beforeend', memberItem);
}

let updateMemberTotal = async (members) => {
    // updates the participant count above pariticpant's list
    let total = document.getElementById('members__count');
    total.innerText = members.length;
}

let handleMemberLeft = async (MemberId) => {
    console.log("Member with id: " + MemberId + ", has left the room!");

    // remove participant from participants list
    await removeMemberFromDom(MemberId);

    // get all members in the channel
    let members = await channel.getMembers();
    // update the participant total
    await updateMemberTotal(members);
};

let removeMemberFromDom = async (MemberId) => {
    let memberWrapper = document.getElementById(`member__${MemberId}__wrapper`);
    let name = memberWrapper.getElementsByClassName('member_name')[0].textContent;

    // remove the participant from participants list
    memberWrapper.remove();

    // add bot message if the user is professor
    if(currentLoggedInStudent==="") {
        await addBotMessageToDom(`${name} has left the room.`);
    }
}

let getMembers = async () => {
    let members = await channel.getMembers();
    // update participants total
    await updateMemberTotal(members);

    // add participants to participants list
    for(let i=0; i<members.length; i++) {
        await addMemberToDom(members[i]);
    }
}

let handleChannelMessage = async (messData, memberId) => {
    console.log('A new message was received from: ' + memberId);

    // the value was stringified before, so we need to parse it
    let data = JSON.parse(messData.text);

    // if the message is of type 'chat', add it to the DOM
    if(data.type === 'chat') {
        await addMessageToDom(data.displayName, data.message, memberId);
    } else if (data.type === 'session') {
        await leaveChannel();
        await getRoomInfo(currentLoggedInUser.id, roomId);
        await delay(10);
        window.location.href = "/subject";
    } else if (data.type === 'bot' && currentLoggedInStudent==="") {
        await addBotMessageToDom(data.message);
    } else if (data.type === 'microphoneToggle') {
        await toggleMic();
    } else if (data.type === 'status_change') {
        await changeUserStatus(data.new_status, data.user);
    } else if (data.type === 'session_end') {
        await addBotMessageToDom(data.message);
    }
}

let getRoomInfo = async (userId, roomId) => {
    let url = "/api/room/end/"+roomId+"/room-summary/"+userId;
    let room = null;

    $.ajax({
        type: "GET",
        url: url,
        async: false,
        headers: {
            "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
        },
        success: function (response) {
            room = response;
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });

    if(room!=null) {
        alert("Room has ended! Press ok to redirect...\n\n"
            + "Room summary:\n"
            + "Name: " + room.name + "\n"
            + "Duration: " + room.roomDuration + " minutes\n"
            + "\n"
            + "Student summary:\n"
            + "Student: " + room.studentFullName + "\n"
            + "Total interruptions: " + room.totalInterruptions + "\n"
            + "Interruptions duration: " + room.interruptionsDuration + " seconds\n"
            + "Student status: " + room.studentStatus);

    }
}

let changeUserStatus = async (newStatus, userId) => {
    console.log("CHANGE USER STATUS (COLOR)")

    // change status in participants
    let obj = $("#member__"+userId+"__wrapper");

    let previousStatus = obj[0].firstElementChild.className;
    // remove previous status
    obj[0].firstElementChild.classList.remove(previousStatus);
    // add new status
    obj[0].firstElementChild.classList.add(newStatus);

    // change status in video container
    let videoContainer = document.getElementById("user-container-" + userId);
    if(videoContainer!==null) {
        videoContainer.style.borderColor = "orange";
    }
}

let addMessageToDom = async (name, message, sender) => {
    let messagesWrapper = document.getElementById('messages');

    // create new message element
    let newMessage = `<div class="message__wrapper">
                        <div class="message__body">
                            <strong class="message__author">${name}</strong>
                            <p class="message__text">${message}</p>
                        </div>
                        <div class="pin-message">
                        <i class="fa-solid fa-thumbtack thumbtack-messages"></i>
                        </div>
                    </div>`;
    // add new message to the HTML DOM
    messagesWrapper.insertAdjacentHTML('beforeend', newMessage);

    // get the last message sent, and scroll to it so the newest message is always into view
    let lastMessage= document.querySelector('#messages .message__wrapper:last-child');
    if(lastMessage) {
        lastMessage.scrollIntoView();
    }

    // send message to backend
    let messageDto = {
        content: message,
        senderId: sender
    }

    console.log(messageDto)
    $.ajax({
        url: "api/chat/save-msg/" + roomId,
        type: "POST",
        data: JSON.stringify(messageDto),
        contentType: "application/json",
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            console.log(response);
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
    $(".pin-message").on("click", function(){
        $(".thumbtack-messages").removeClass("thumbtack-messages-pinned");
        $("#pinned-message").css("display", "flex");
        let pinMessage = $(this).siblings().children()[1].innerHTML;
        $(".pin-message-paragraph").text(pinMessage);
        var clientHeight = document.getElementById('pinned-message').clientHeight;
        $("#messages").css("margin-top", clientHeight);
        $(this).children().addClass("thumbtack-messages-pinned");
    });
}

let addBotMessageToDom = async (botMessage) => {
    let messagesWrapper = document.getElementById('messages');

    let newMessage = `<div class="message__wrapper">
                        <div class="message__body__bot">
                            <strong class="message__author__bot">🤖 Timski Bot</strong>
                            <p class="message__text__bot">${botMessage}</p>
                        </div>
                    </div>`;

    // add the bot message to DOM
    messagesWrapper.insertAdjacentHTML('beforeend', newMessage);

    // scroll to the newest message
    let lastMessage= document.querySelector('#messages .message__wrapper:last-child');
    if(lastMessage) {
        lastMessage.scrollIntoView();
    }
}

// send channel message to end session
let sendRoomHasEndedMessage = async () => {
    // add bot message to professor's chat
    await addBotMessageToDom("Session has ended! Redirecting...");

    channel.sendMessage({text:JSON.stringify({
            'type': 'session_end',
            'message': "Session has ended! Redirecting..."
    })});

    // this function sends message to channel (all users) to end session
    channel.sendMessage({text:JSON.stringify({
            'type': 'session',
            'message': "End session"
    })});

    // the professor logs out after 10 seconds
    await delay(10);
    // redirect to previous page
    window.location.href="/subject";
}

let sendToggleAllMicrophonesMessage = async (e) => {
    channel.sendMessage({text:JSON.stringify({
            'type': 'microphoneToggle',
            'message': "Toggle all microphones..."
        })});
}

let sendMessage = async (e) => {
    e.preventDefault();

    // get message content that was written in the form
    let message = e.target.message.value;
    // this function sends message to channel (all users), can also send p2p message (direct) with different function
    channel.sendMessage({text:JSON.stringify({
            'type': 'chat',
            'message': message,
            'displayName': currentLoggedInUser.name}
        )});

    // add message element to the DOM
    await addMessageToDom(currentLoggedInUser.name, message, uid);

    // send message to backend
    let messageDto = {
        content: message,
        senderId: uid
    }

    $.ajax({
        url: "api/chat/save-msg/" + roomId,
        type: "POST",
        data: JSON.stringify(messageDto),
        contentType: "application/json",
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            console.log(response);
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });

    // reset the form
    e.target.reset();
}

let leaveChannel = async () => {
    await channel.leave();
    await rtmClient.logout();
}

window.addEventListener('beforeunload', leaveChannel);
document.getElementById('message__form').addEventListener('submit', sendMessage);
    $("#pinMessage").on("click", function () {
        let formattedText = tinymce.get("summernote").getContent()
    });