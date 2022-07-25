const APP_ID = "4b77f4fc58994fdd9fe727a7106ad66a";

// user id
let uid = sessionStorage.getItem("uid");
// contains current logged-in user
let currentLoggedInUser;
// contains current logged-in user
let currentLoggedInStudent;
// audio and video streams (ours/local)
let localTracks = [];
// key-value pairs
let remoteUsers = {}
// screen tracks
let localScreenTracks;
// don't share screen right away
let sharingScreen = false;
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

let checkIfStudent = async () => {
    $.ajax({
        url: "/api/user/student/current",
        type: "GET",
        async: false,
        headers: {
            "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
        },
        success: function (response) {
            currentLoggedInStudent = response;
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
}

// get current logged-in user
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
        checkIfStudent();
    },
    error: function (rs) {
        console.error(rs.status);
        console.error(rs.responseText);
    }
});

sessionStorage.setItem("uid", uid);

// take the query string from the URL and get the current room id
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let roomId = urlParams.get('room');

// if no room is present redirect to home
if(!roomId) {
    window.location.assign("http://localhost:8080/home");
}

// object used for sending info related to creating the RTC token
const agoraDTO = {
    "roomId": roomId,
    "userId": uid
};

// generate the RTC token used for AgoraRTC authentication
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

// generate the RTM token used for AgoraRTM authentication
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

// join room with a specific user
let joinRoomInit = async () => {
    console.log("Joining room, user: " + uid);

    // convert to string, because of API requirements
    let id = uid.toString();

    // create instance for AgoraRTM and login
    rtmClient = await AgoraRTM.createInstance(APP_ID);
    await rtmClient.login({uid: id, token: rtmToken});

    // Save user's name in RTMClient for later access by other members
    await rtmClient.addOrUpdateLocalUserAttributes({'name': currentLoggedInUser.name, 'student': JSON.stringify(currentLoggedInStudent)});

    // create RTM channel
    channel = await rtmClient.createChannel(roomId);
    await channel.join();

    // event listeners for AgoraRTM
    channel.on('MemberJoined', handleMemberJoined);
    channel.on('MemberLeft', handleMemberLeft);
    channel.on('ChannelMessage', handleChannelMessage);

    // update participant count and list
    await getMembers();
    // add bot message to chat box when a user joins the room
    await addBotMessageToDom(`Welcome to the room ${currentLoggedInUser.name}! 👋`)

    // create the AgoraRTC client
    client = AgoraRTC.createClient({
        mode: 'rtc',
        codec: 'vp8'
    });

    // join a specific room
    await client.join(APP_ID, roomId, rtcToken, uid);

    // event listener for AgoraRTC
    client.on('user-published', handleUserPublished);
    client.on('user-left', handleUserLeft);
}

let joinVideoStream = async () => {
    // create local track for video
    localTracks[1] = await AgoraRTC.createCameraVideoTrack({encoderConfig: {
            width: {min: 640, ideal: 1920, max: 1920},
            height: {min:480, ideal: 1080, max:1080}
        }});

    // remove container if already created
    let el = document.getElementById(`user-container-${uid}`);
    if(el!==null) {
        el.remove();
    }

    let player = `<div class="video__container" id="user-container-${uid}">
                        <div class="video-player" id="user-${uid}"></div>
                  </div>`;

    // add video player for current user to the DOM
    document.getElementById('streams__container').insertAdjacentHTML('beforeend', player);
    // event listener for when user clicks, expand the video to presenter mode (full screen)
    document.getElementById(`user-container-${uid}`).addEventListener('click', expandVideoFrame);

    // [0]-audio track, [1]-video track
    await localTracks[1].setEnabled(true);
    await localTracks[1].play(`user-${uid}`);
    await client.publish([localTracks[1]]);
}

let joinAudioStream = async () => {
    // create local track for audio
    localTracks[0] = await AgoraRTC.createMicrophoneAudioTrack({AEC: true, ANS:true, encoderConfig: "high_quality"});

    // [0]-audio track, [1]-video track
    await localTracks[0].setEnabled(true);
    await localTracks[0].play();

    await client.publish([localTracks[0]]);
}

let switchToCamera = async () => {
    let player = `<div class="video__container" id="user-container-${uid}">
                        <div class="video-player" id="user-${uid}"></div>
                  </div>`;

    // add player to the DOM
    document.getElementById('streams__container').insertAdjacentHTML('beforeend', player);
    // event listener for when user clicks, expand the video to presenter mode (full screen)
    document.getElementById(`user-container-${uid}`).addEventListener('click', expandVideoFrame);

    // [0]-audio track, [1]-video track
    if(localTracks[1] !== undefined){
        localTracks[1].play(`user-${uid}`);
    }
}

let handleUserPublished = async (user, mediaType) => {

    console.log("USER PUBLISHED stream, client: " + uid);

    remoteUsers[user.uid] = user;

    // subscribe to current user's stream
    await client.subscribe(user, mediaType);

    // get player for user (check if already exists)
    let player = document.getElementById(`user-container-${user.uid}`);

    // if the player is not present, then we create it (check for avoiding duplicated)
    if(player == null) {
        player = `<div class="video__container" id="user-container-${user.uid}">
                        <div class="video-player" id="user-${user.uid}"></div>
                  </div>`;

        // add player to the DOM
        document.getElementById('streams__container').insertAdjacentHTML('beforeend', player);
        document.getElementById(`user-container-${user.uid}`).addEventListener('click', expandVideoFrame);
    }

    // when a new user joins, if we are displaying someone set the new user to 100px right away
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

    // when user leaves, if he was the presenter, remove him from stream box (presenter)
    // and the make the other participant's screens bigger (because presenter left)
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

    // check if this is the first time turning on the microphone
    // if yes, create audio stream
    if(localTracks[0] === undefined) {
        console.log("Track 0 undefineeeeddd");
        await joinAudioStream();
        button.classList.add('active');
        return;
    }

    // check if microphone is turned off
    if(!localTracks[0].enabled) {
        // turn on (umnute) microphone
        button.classList.add('active');
        await localTracks[0].setEnabled(true);
    } else {
        // turn off (mute) microphone
        button.classList.remove('active');
        await localTracks[0].setEnabled(false);
    }
}

let toggleCamera = async (e) => {
    let button = e.currentTarget;

    // check if this is the first time turning on the camera
    // if yes, create camera video stream
    if(localTracks[1] === undefined) {
        await joinVideoStream();
        button.classList.add('active');
        return;
    }

    // check if camera is turned off
    if(!localTracks[1].enabled) {
        // turn on camera
        button.classList.add('active');
        await localTracks[1].setEnabled(true);
    } else {
        // turn off camera
        button.classList.remove('active');
        await localTracks[1].setEnabled(false);
        // change black screen to user icon
        document.getElementById(`user-container-${uid}`).children[0].style.backgroundColor = "none";
    }
}

let toggleScreen = async (e) => {
    let screenButton = e.currentTarget;
    let cameraButton = document.getElementById('camera-btn');

    // turn off camera, turn on screen sharing
    if(!sharingScreen) {
        sharingScreen = true;

        screenButton.classList.add('active');

        // turn off camera video and hide camera button
        cameraButton.classList.remove('active');
        cameraButton.style.display = 'none';
        // check if camera stream already exists
        if(localTracks[1] !== undefined) {
            // disable camera
            await localTracks[1].setEnabled(false);
            // un-publish current video track
            await client.unpublish(localTracks[1]);
            // remove current video track (camera) from user's video container
            document.getElementById(`user-container-${uid}`).remove();
        }

        // create video track for screen share
        localScreenTracks = await AgoraRTC.createScreenVideoTrack();

        // share screen
        displayFrame.style.display = 'block';

        // create a new player for screen share
        let player = `<div class="video__container" id="user-container-${uid}">
                        <div class="video-player" id="user-${uid}"></div>
                  </div>`;
        // add player to HTML
        displayFrame.insertAdjacentHTML('beforeend', player);
        // make it clickable (expands on click)
        document.getElementById(`user-container-${uid}`).addEventListener('click', expandVideoFrame);

        userIdInDisplayFrame = `user-container-${uid}`;
        localScreenTracks.play(`user-${uid}`);

        // publish screen share video
        await client.publish([localScreenTracks]);

        // set other participants video stream boxes to 100px (because someone is sharing)
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

        // remove current video track (screen share track)
        document.getElementById(`user-container-${uid}`).remove();
        // un-publish current screen share video track
        await client.unpublish([localScreenTracks]);
        displayFrame.style.display = 'none';

        await switchToCamera();

        // set participants video stream boxes to 300px (because someone stopped screen sharing)
        let videoFrames = document.getElementsByClassName('video__container');
        for (let i=0; i<videoFrames.length; i++) {
            if(videoFrames[i].id !== userIdInDisplayFrame) {
                videoFrames[i].style.height = '300px';
                videoFrames[i].style.width = '300px';
            }
        }
    }
}

document.getElementById('mic-btn').addEventListener('click', toggleMic);
document.getElementById('camera-btn').addEventListener('click', toggleCamera);
document.getElementById('screen-btn').addEventListener('click', toggleScreen);

joinRoomInit();





