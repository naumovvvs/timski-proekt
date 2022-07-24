const APP_ID = "4b77f4fc58994fdd9fe727a7106ad66a";

// user id
let uid = sessionStorage.getItem("uid");
// contains current logged in user
let currentLoggedInUser;

$.ajax({
    url: "/api/user/current",
    type: "GET",
    async: false,
    headers: {
      "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
    },
    success: function (response) {
        uid = response.id;
        currentLoggedInUser = response;
    },
    error: function (rs) {
        console.error(rs.status);
        console.error(rs.responseText);
    }
});

sessionStorage.setItem("uid", uid);


// test za uid
console.log("User id: " + parseInt(sessionStorage.getItem("uid")));

// token used for RTC authorization in Agora
let rtcToken = null;
// token used for RTM authorization in Agora
let rtmToken = null;
// RTC core object for functionality
let client;
// rtm client
let rtmClient;
// rtm channel
let channel;

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let roomId = urlParams.get('room');

console.log("Room id: " + roomId);
console.log(roomId);

if(!roomId) {
    roomId = 'main';
}

const agoraDTO = {
    "roomId": roomId,
    "userId": uid
};

$.ajax({
    type: "POST",
    url: "/api/user/agora/generate-rtc-token",
    async: false,
    data: JSON.stringify(agoraDTO),
    contentType: "application/json; charset=utf-8",
    headers: {
        "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
    },
    success: function (response) {
        rtcToken = response;
    },
    error: function (rs) {
        console.error(rs.status);
        console.error(rs.responseText);
    }
});

$.ajax({
    type: "POST",
    url: "/api/user/agora/generate-rtm-token",
    async: false,
    data: JSON.stringify(agoraDTO),
    contentType: "application/json; charset=utf-8",
    headers: {
        "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
    },
    success: function (response) {
        rtmToken = response;
    },
    error: function (rs) {
        console.error(rs.status);
        console.error(rs.responseText);
    }
});

// audio and video streams (ours)
let localTracks = [];
// key-value pairs
let remoteUsers = {}
// screen tracks
let localScreenTracks;
// don't share screen right away
let sharingScreen = false;

// join room with a specific user
let joinRoomInit = async () => {
    console.log("Joining room ");
    console.log(uid);
    let id = uid.toString();

    rtmClient = await AgoraRTM.createInstance(APP_ID);
    await rtmClient.login({uid: id, token: rtmToken});

    console.log("SUCCESSFUL LOGIN"  );
    console.log("CURRENT USER: ");
    console.log(currentLoggedInUser.name);

    // Save user's name for access by others later
    await rtmClient.addOrUpdateLocalUserAttributes({'name': currentLoggedInUser.name});

    channel = await rtmClient.createChannel(roomId);
    await channel.join();

    console.log("Joined channel");

    channel.on('MemberJoined', handleMemberJoined);
    channel.on('MemberLeft', handleMemberLeft);
    channel.on('ChannelMessage', handleChannelMessage);

    console.log("Listeners on for member joined and left");

    getMembers();
    addBotMessageToDom(`Welcome to the room ${currentLoggedInUser.name}! 👋`)

    client = AgoraRTC.createClient({
        mode: 'rtc',
        codec: 'vp8'
    });

    await client.join(APP_ID, roomId, rtcToken, uid);

    client.on('user-published', handleUserPublished);
    client.on('user-left', handleUserLeft);

    await joinStream();
}

let joinStream = async () => {
    localTracks = await AgoraRTC.createMicrophoneAndCameraTracks({}, {encoderConfig: {
            width: {min: 640, ideal: 1920, max: 1920},
            height: {min:480, ideal: 1080, max:1080}
        }
    });

    console.log("JOIN STREAM");

    let player = `<div class="video__container" id="user-container-${uid}">
                        <div class="video-player" id="user-${uid}"></div>
                  </div>`;

    // add player to the DOM
    document.getElementById('streams__container').insertAdjacentHTML('beforeend', player);
    document.getElementById(`user-container-${uid}`).addEventListener('click', expandVideoFrame);

    // let cameraId = "ce95d4ad940944fc5ff09c032e8dff5f61ff6322f4ea6d48dcd8b261726e721b";
    // const videoTrack = await AgoraRTC.createCameraVideoTrack({ cameraId });
    // localTracks[1] = videoTrack;

    // [0]-audio track, [1]-video track
    localTracks[1].play(`user-${uid}`);
    await client.publish([localTracks[0], localTracks[1]]);
}

let switchToCamera = async () => {
    let player = `<div class="video__container" id="user-container-${uid}">
                        <div class="video-player" id="user-${uid}"></div>
                  </div>`;
    // add player to the DOM
    displayFrame.insertAdjacentHTML('beforeend', player);

    await localTracks[0].setMuted(true);
    await localTracks[1].setMuted(true);

    document.getElementById('mic-btn').classList.remove('active');
    document.getElementById('screen-btn').classList.remove('active');

    // [0]-audio track, [1]-video track
    localTracks[1].play(`user-${uid}`);
    await client.publish([localTracks[1]]);
}

let handleUserPublished = async (user, mediaType) => {

    console.log("USER PUBLISHED, client: " + uid);

    remoteUsers[user.uid] = user;

    await client.subscribe(user, mediaType);

    let player = document.getElementById(`user-container-${user.uid}`);

    if(player == null) {
        player = `<div class="video__container" id="user-container-${user.uid}">
                        <div class="video-player" id="user-${user.uid}"></div>
                  </div>`;

        // add player to the DOM
        document.getElementById('streams__container').insertAdjacentHTML('beforeend', player);
        document.getElementById(`user-container-${user.uid}`).addEventListener('click', expandVideoFrame);
    }

    // when a new user joins, if we are displaying someone set them to 100px right away
    if(displayFrame.style.display) {
        let videoFrame = document.getElementById(`user-container-${user.uid}`);
        videoFrame.style.height = '100px';
        videoFrame.style.width = '100px';
    }

    if(mediaType === 'video'){
        user.videoTrack.play(`user-${user.uid}`);
    }

    if(mediaType === 'audio'){
        user.audioTrack.play();
    }
}

let handleUserLeft = async (user) => {
    console.log("USER LEFT");

    // delete user from object of users
    delete remoteUsers[user.uid];
    // remove user video container
    document.getElementById(`user-container-${user.uid}`).remove();

    // when user leaves, remove him from stream box (presenter)
    if(userIdInDisplayFrame === `user-container-${user.uid}`) {
        displayFrame.style.display = null;

        let videoFrames = document.getElementsByClassName('video__container');
        for(let i=0; i<videoFrames.length; i++) {
            videoFrames[i].style.height = "300px";
            videoFrames[i].style.width = "300px";
        }
    }
}

let toggleMic = async (e) => {
    let button = e.currentTarget;

    // check if microphone is turned off
    if(localTracks[0].muted) {
        await localTracks[1].setMuted(false);
        button.classList.add('active');
    } else {
        await localTracks[1].setMuted(true);
        button.classList.remove('active');
    }
}

let toggleCamera = async (e) => {
    let button = e.currentTarget;

    // check if camera is turned off
    if(localTracks[1].muted) {
        await localTracks[1].setMuted(false);
        button.classList.add('active');
    } else {
        await localTracks[1].setMuted(true);
        button.classList.remove('active');
    }
}

let toggleScreen = async (e) => {
    let screenButton = e.currentTarget;
    let cameraButton = document.getElementById('camera-btn');

    // turn off camera, turn on screen sharing
    if(!sharingScreen) {
        sharingScreen = true;

        screenButton.classList.add('active');
        cameraButton.classList.remove('active');
        cameraButton.style.display = 'none';

        localScreenTracks = await AgoraRTC.createScreenVideoTrack();
        // remove current video track
        document.getElementById(`user-container-${uid}`).remove();
        // share screen
        displayFrame.style.display = 'block';

        let player = `<div class="video__container" id="user-container-${uid}">
                        <div class="video-player" id="user-${uid}"></div>
                  </div>`;
        // add player to HTML
        displayFrame.insertAdjacentHTML('beforeend', player);
        // make it clickable
        document.getElementById(`user-container-${uid}`).addEventListener('click', expandVideoFrame);

        userIdInDisplayFrame = `user-container-${uid}`;
        localScreenTracks.play(`user-${uid}`);

        // un-publish current video track
        await client.unpublish(localTracks[1]);
        // publish screen share video
        await client.publish([localScreenTracks]);

        let videoFrames = document.getElementsByClassName('video__container');
        for (let i=0; i<videoFrames.length; i++) {
            if(videoFrames[i].id !== userIdInDisplayFrame) {
                videoFrames[i].style.height = '100px';
                videoFrames[i].style.width = '100px';
            }
        }
    } else {
        sharingScreen = false;

        cameraButton.style.display = 'block';
        screenButton.classList.remove('active');

        // remove current video track
        document.getElementById(`user-container-${uid}`).remove();
        // un-publish current screen share video track
        await client.unpublish([localScreenTracks]);

        await switchToCamera();
    }
}

document.getElementById('mic-btn').addEventListener('click', toggleMic);
document.getElementById('camera-btn').addEventListener('click', toggleCamera);
document.getElementById('screen-btn').addEventListener('click', toggleScreen);

joinRoomInit();





