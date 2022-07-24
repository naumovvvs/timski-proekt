let handleMemberJoined = async (MemberId) => {
    console.log("A new member has joined the room: " + MemberId);
    await addMemberToDom(MemberId);

    let members = await channel.getMembers();

    await updateMemberTotal(members);
    let {name} = await rtmClient.getUserAttributesByKeys(MemberId, ['name']);
    await addBotMessageToDom(`Welcome to the room ${name}! 👋`);
}

let addMemberToDom = async (MemberId) => {
    // get the name value from this specific user
    // {name} destructure the response
    let {name} = await rtmClient.getUserAttributesByKeys(MemberId, ['name']);

    let membersWrapper = document.getElementById('member__list');
    let memberItem = `<div class="member__wrapper" id="member__${MemberId}__wrapper">
                        <span class="green__icon"></span>
                        <p class="member_name">${name}</p>
                      </div>`;

    membersWrapper.insertAdjacentHTML('beforeend', memberItem);
}

let updateMemberTotal = async (members) => {
    let total = document.getElementById('members__count');
    total.innerText = members.length;
}

let handleMemberLeft = async (MemberId) => {
    console.log("Member with id: " + MemberId + ", has left the room!");
    await removeMemberFromDom(MemberId);

    let members = await channel.getMembers();
    await updateMemberTotal(members);
};

let removeMemberFromDom = async (MemberId) => {
    let memberWrapper = document.getElementById(`member__${MemberId}__wrapper`);
    let name = memberWrapper.getElementsByClassName('member_name')[0].textContent;

    memberWrapper.remove();
    await addBotMessageToDom(`${name} has left the room.`);
}

let getMembers = async () => {
    let members = await channel.getMembers();
    await updateMemberTotal(members);

    for(let i=0; i<members.length; i++) {
        await addMemberToDom(members[i]);
    }
}

let handleChannelMessage = async (messData, memberId) => {
    console.log('A new message was received from: ' + memberId);

    // the value was stringified, so we need to parse it
    let data = JSON.parse(messData.text);

    if(data.type === 'chat') {
        await addMessageToDom(data.displayName, data.message);
    }
}

let addMessageToDom = async (name, message) => {
    let messagesWrapper = document.getElementById('messages');

    let newMessage = `<div class="message__wrapper">
                        <div class="message__body">
                            <strong class="message__author">${name}</strong>
                            <p class="message__text">${message}</p>
                        </div>
                    </div>`;

    messagesWrapper.insertAdjacentHTML('beforeend', newMessage);

    let lastMessage= document.querySelector('#messages .message__wrapper:last-child');
    if(lastMessage) {
        lastMessage.scrollIntoView();
    }
}

let addBotMessageToDom = async (botMessage) => {
    let messagesWrapper = document.getElementById('messages');

    let newMessage = `<div class="message__wrapper">
                        <div class="message__body__bot">
                            <strong class="message__author__bot">🤖 Timski Bot</strong>
                            <p class="message__text__bot">${botMessage}</p>
                        </div>
                    </div>`;

    messagesWrapper.insertAdjacentHTML('beforeend', newMessage);

    let lastMessage= document.querySelector('#messages .message__wrapper:last-child');
    if(lastMessage) {
        lastMessage.scrollIntoView();
    }
}

let sendMessage = async (e) => {
    e.preventDefault();

    let message = e.target.message.value;
    // this function sends message to channel (all users), can also send p2p message (direct) with different function
    channel.sendMessage({text:JSON.stringify({
            'type': 'chat',
            'message': message,
            'displayName': currentLoggedInUser.name}
        )});

    await addMessageToDom(currentLoggedInUser.name, message);

    // reset the form
    e.target.reset();
}

let leaveChannel = async () => {
    await channel.leave();
    await rtmClient.logout();
}

window.addEventListener('beforeunload', leaveChannel);
let messageForm = document.getElementById('message__form');
messageForm.addEventListener('submit', sendMessage)