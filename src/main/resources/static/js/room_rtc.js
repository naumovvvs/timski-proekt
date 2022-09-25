const APP_ID = "4b77f4fc58994fdd9fe727a7106ad66a";

// user id
let uid = sessionStorage.getItem("uid");
// contains current logged-in user
let currentLoggedInUser= null;
// contains current logged-in user
let currentLoggedInStudent = null;
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
// token used for Screen sharing RTC authorization in Agora
let screenSharingRtcToken = null;
// token used for RTM authorization in Agora
let rtmToken = null;
// RTC core object for functionality
let client;
// RTC screen sharing client
let screenClient;
// rtm client
let rtmClient;
// rtm channel
let channel;
// interruption start time
let interruptionStartTime;
// interruption end time
let interruptionEndTime;
// interruption start time full format
let interruptionStartTimeString;

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


// object used for sending info related to creating the RTC token for screen sharing
const agoraScreenShareDTO = {
    "roomId": roomId,
    "userId": uid*10000
};
// generate the RTC token used for AgoraRTC authentication (Screen sharing feature)
$.ajax({
    type: "POST",
    url: "/api/user/agora/generate-rtc-token",
    async: false,
    data: JSON.stringify(agoraScreenShareDTO),
    contentType: "application/json; charset=utf-8",
    headers: {
        "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
    },
    success: function (response) {
        screenSharingRtcToken = response;
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

// room end url
let roomEndURL = "/api/room/end/" + roomId;

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

    // monitors the connection state
    rtmClient.on('ConnectionStateChanged', async (newState, reason) => {
        console.log('USER STATE CHANGE - ' + uid);
        console.log('newState - ' + newState + " ||| reason - " + reason);

        if (newState === 'RECONNECTING' && reason === 'INTERRUPTED') {
            // record the time when the interruption started
            interruptionStartTime = Date.now();
            let dateObj = new Date();
            interruptionStartTimeString = dateObj.toLocaleString();
        } else if (newState === 'CONNECTED' && interruptionStartTime != null) {
            // check if professor
            if(currentLoggedInStudent==="") {
                interruptionStartTime = null;
                return;
            }

            // record when the interruption ended, calculate duration and save in database
            interruptionEndTime = Date.now();

            // wait 5 seconds before sending message (gives time to the app to connect to Agora RTM server)
            await delay(5);

            // total duration in seconds (1000ms=1s)
            let totalDuration = (interruptionEndTime - interruptionStartTime) / 1000;

            await changeUserStatus("orange__icon", currentLoggedInUser.id);

            // send bot message
            channel.sendMessage({
                text: JSON.stringify({
                    'type': 'bot',
                    'message': currentLoggedInUser.name + " had been disconnected for " + totalDuration + " seconds!"
                })
            });

            // change student status (color)
            await changeStudentStatusAfterInterruption();

            // DTO object for creating interruption
            const interruptionDTO = {
                time: interruptionStartTimeString,
                totalDuration: totalDuration,
                roomId: roomId,
                studentId: uid
            };

            // reset start time
            interruptionStartTime = null;

            // save interruption in database
            $.ajax({
                url: "/api/room/student/connection-interruption",
                type: "POST",
                data: JSON.stringify(interruptionDTO),
                contentType: "application/json",
                headers: {
                    "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
                }
            });
        }
    });

    // event listeners for AgoraRTM
    channel.on('MemberJoined', handleMemberJoined);
    channel.on('MemberLeft', handleMemberLeft);
    channel.on('ChannelMessage', handleChannelMessage);

    // update participant count and list
    await getMembers();
    // add bot message to chat box when a user joins the room
    await addBotMessageToDom(`Welcome to the room ${currentLoggedInUser.name}! 👋`)

    // create the AgoraRTC client
    client = AgoraRTC.createClient({mode: 'rtc', codec: 'vp8'});

    // join a specific room
    await client.join(APP_ID, roomId, rtcToken, uid);

    // create AgoraRTC screen share client
    screenClient = AgoraRTC.createClient({ mode: "rtc", codec: "vp8" });
    await screenClient.join(APP_ID, roomId, screenSharingRtcToken, uid*10000);

    // event listener for AgoraRTC
    client.on('user-published', handleUserPublished);
    client.on('user-unpublished', handleUserUnpublished);
    client.on('user-left', handleUserLeft);

    // event listener for AgoraRTC (screen share client)
    screenClient.on('user-published', handleUserPublishedScreen);
    screenClient.on('user-unpublished', handleUserUnpublishedScreen);

    // check if this is the start of the room
    let members = await channel.getMembers();
    if (members.length === 1) {
        // start this room
        $.ajax({
            type: "GET",
            url: "/api/room/open/" + roomId,
            headers: {
                "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
            },
            success: function (response) {
                console.log(response);
                createStudentInRoomRelationship();
            },
            error: function (rs) {
                console.error(rs.status);
                console.error(rs.responseText);
            }
        });
    } else {
        await createStudentInRoomRelationship();
    }
}

// for this room and the corresponding user link the user with new relationship instance
let createStudentInRoomRelationship = async () => {
    if (currentLoggedInStudent) {
        $.ajax({
            type: "GET",
            url: "/api/room/add-student/" + roomId + "/" + uid,
            headers: {
                "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
            },
            success: function (response) {
                console.log(response);
            },
            error: function (rs) {
                console.error(rs.status);
                console.error(rs.responseText);
            }
        });
    }
}

// handles LOCAL video stream
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

    // default value for player, if there is screen share then modify player
    let player = `<div class="camera_video_container" id="user-container-${uid}">
                        <div class="video-player" id="user-${uid}"></div>
                  </div>`;

    // helper id variable
    let pomId = uid*10000;
    let screenPlayer = document.getElementById(`user-container-${pomId}-screen`);

    if(screenPlayer!=null) {
        // if there is screen share, append the camera to the screen share container
        player = `<div class="camera_video_container" id="user-container-${uid}" style="position: absolute; 
                            height: 90px; width: 170px; top: 65%; left: 65%;">
                    <div class="video-player" id="user-${uid}"></div>
                  </div>`;
        screenPlayer.insertAdjacentHTML('beforeend', player);
    } else {
        // if user hasn't shared screen, then create camera video stream only
        // add video player for current user to the DOM
        document.getElementById('unidentified__container').insertAdjacentHTML('beforeend', player);
        // event listener for when user clicks, expand the video to presenter mode (full screen)
        document.getElementById(`user-container-${uid}`).addEventListener('click', expandVideoFrame);
    }

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

    await client.publish([localTracks[0]]);
}

// Handles REMOTE camera stream (not screen share)
let handleUserPublished = async (user, mediaType) => {
    if(user.uid === currentLoggedInUser.id) {
        return;
    }

    if(user.uid % 10000 === 0) {
        return;
    }

    console.log("Remote user published stream, client: " + uid + ", remote: " + user.uid);

    remoteUsers[user.uid] = user;

    // subscribe to current user's stream
    await client.subscribe(user, mediaType);

    let pomUserId = user.uid * 10000;
    let screenPlayer = document.getElementById(`user-container-${pomUserId}-screen`);

    if(screenPlayer != null) {
        // if the user has screen share already published, then append camera to the screen share container
        // Check if camera container is present, and remove it because we need to create it again
        let cameraPlayer = document.getElementById(`user-container-${user.uid}`);
        if(cameraPlayer!=null) {
            cameraPlayer.remove();
        }

        // default values for camera container
        let ht = "90px";
        let wt = "170px";

        // if the user currently sharing in stream box is the same user with the camera,
        // then make the camera container bigger
        if(userIdInDisplayFrame === screenPlayer.id) {
            ht = "180px";
            wt = "300px";
        }

        let player = `<div class="camera_video_container" id="user-container-${user.uid}" style="position: absolute; 
                            height: ${ht}; width: ${wt}; top: 65%; left: 65%;">
                    <div class="video-player" id="user-${user.uid}"></div>
                  </div>`;

        screenPlayer.insertAdjacentHTML('beforeend', player);
    } else {
        // if user hasn't shared screen, then show camera only with normal dimensions
        // get camera player for user (check if already exists)
        let cameraPlayer = document.getElementById(`user-container-${user.uid}`);

        // if the camera player is not present, then we create it (check for avoiding duplicated)
        if (cameraPlayer == null) {
            let player = `<div class="camera_video_container" id="user-container-${user.uid}">
                    <div class="video-player" id="user-${user.uid}"></div>
                  </div>`;

            // add player to the DOM
            document.getElementById('unidentified__container').insertAdjacentHTML('beforeend', player);
            document.getElementById(`user-container-${user.uid}`).addEventListener('click', expandVideoFrame);
        }
    }

    // when a new user joins, if we are displaying someone set the new user as smaller right away
    if(displayFrame.style.display) {
        // check for screen share container
        let pomId = user.uid*10000;
        let videoFrame = document.getElementById(`user-container-${pomId}-screen`);

        // false means screen share container is not present
        let screenShareFlag = false;

        if(videoFrame!=null){
            // change flag to true because screen share container is present
            screenShareFlag = true;
            videoFrame.style.height = '180px';
            videoFrame.style.width = '300px';
        }

        // check for camera container
        let cameraFrame = document.getElementById(`user-container-${user.uid}`);
        if(cameraFrame!=null) {
            // if screen share is present the make camera small (in the corner), else make it normal size
            if(screenShareFlag){
                cameraFrame.style.height = '90px';
                cameraFrame.style.height = '170px';
            } else {
                cameraFrame.style.height = '180px';
                cameraFrame.style.height = '300px';
            }
        }
    }

    if(mediaType === 'video'){
        user.videoTrack.play(`user-${user.uid}`);
    }

    if(mediaType === 'audio'){
        user.audioTrack.play();
    }
}

let handleUserUnpublished = async (user, mediaType) => {
    if(user.uid === currentLoggedInUser.id) {
        return;
    }

    if(user.uid % 10000 === 0) {
        return;
    }

    console.log("Unsubscribe from user: " + user.uid + ", media type: " + mediaType);

    await client.unsubscribe(user,mediaType);
}

let handleUserLeft = async (user) => {
    // if screen share user, don't run function
    if(user.uid % 10000 === 0) {
        return;
    }

    console.log("User: " + user.uid + ", left the room!");

    // delete user from object of users
    delete remoteUsers[user.uid];
    // remove user video container
    let el = document.getElementById(`user-container-${user.uid}`);
    if(el!==null) {
        el.remove();
    }

    // when user leaves, if he was the presenter, remove him from stream box (presenter)
    // and the make the other participant's screens bigger (because presenter left)
    if(userIdInDisplayFrame === `user-container-${user.uid}`) {
        displayFrame.style.display = null;

        let videoFrames = document.getElementsByClassName('video__container');
        for(let i=0; i<videoFrames.length; i++) {
            videoFrames[i].style.height = "300px";
            videoFrames[i].style.width = "500px";
        }

        let cameraFrames = document.getElementsByClassName('camera_video_container');
        for(let i=0; i<cameraFrames.length; i++) {
            cameraFrames[i].style.height = "180px";
            cameraFrames[i].style.width = "300px";
        }
    }

    // update the "leave-time" in the database
    $.ajax({
        type: "GET",
        url: "/api/room/leave-student/" + roomId + "/" + user.uid,
        headers: {
            "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
        },
        success: function (response) {
            console.log(response);
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
}

let handleUserPublishedScreen = async(user, mediaType) => {
    if(user.uid === currentLoggedInUser.id) {
        return;
    }

    if(user.uid % 10000 !== 0) {
        return;
    }

    console.log("Remote user published screen share stream, client: " + uid + ", remote: " + user.uid);

    remoteUsers[user.uid] = user;

    // subscribe to current user's stream
    await screenClient.subscribe(user, mediaType);

    // get player for user (check if already exists)
    let screenPlayer = document.getElementById(`user-container-${user.uid}-screen`);
    // if the player is not present, then we create it (check for avoiding duplicated)
    if(screenPlayer == null) {
        let player = `<div class="video__container" id="user-container-${user.uid}-screen" style="position: relative">
                        <div class="video-player" id="user-${user.uid}-screen"></div>
                  </div>`;

        // add player to the DOM
        document.getElementById('streams__container').insertAdjacentHTML('beforeend', player);
        document.getElementById(`user-container-${user.uid}-screen`).addEventListener('click', expandVideoFrame);
    }

    let pomId = user.uid/10000;
    let cameraPlayer = document.getElementById(`user-container-${pomId}`);

    // false because camera share is not present else true
    let cameraFlag = false;
    // if camera div already exists then remove it and append it to the screen share container
    if(cameraPlayer!=null) {
        cameraFlag = true;
        // remove existing container
        cameraPlayer.remove();
        // create again
        let cameraDiv = `<div class="camera_video_container" id="user-container-${pomId}" style="position: absolute; 
                            height: 90px!important; width: 170px!important; top: 65%; left: 65%;">
                            <div class="video-player" id="user-${pomId}"></div>
                        </div>`;
        // append to screen share
        let screenPlayer = document.getElementById(`user-container-${user.uid}-screen`);
        screenPlayer.insertAdjacentHTML('beforeend', cameraDiv);
    }

    // when a new user joins, if we are displaying someone set the new user to smaller size right away
    if(displayFrame.style.display) {
        let videoFrame = document.getElementById(`user-container-${user.uid}-screen`);
        videoFrame.style.height = '180px';
        videoFrame.style.width = '300px';
        if(cameraFlag) {
            let cameraFrame = document.getElementById(`user-container-${pomId}`);
            if(cameraFrame!=null) {
                cameraFrame.style.height = '90px';
                cameraFrame.style.width = '170px';
            }
        }
    }

    // get camera user (user's camera video track) from list of remote users
    let cameraUser = remoteUsers[pomId];

    if(mediaType === 'video'){
        user.videoTrack.play(`user-${user.uid}-screen`);
        if(cameraUser != null) {
            cameraUser.videoTrack.play(`user-${pomId}`);
        }
    }

    if(mediaType === 'audio'){
        user.audioTrack.play();
    }
}

let handleUserUnpublishedScreen = async(user, mediaType) => {
    if(user.uid === currentLoggedInUser.id) {
        return;
    }

    if(user.uid % 10000 !== 0) {
        return;
    }

    // before removing screen container, check if camera container is present
    let pomId = user.uid/10000;
    let cameraDiv = document.getElementById(`user-container-${pomId}`);

    // if camera is present (user shared both camera and screen), remove camera container first then screen container
    if(cameraDiv!=null) {
        cameraDiv.remove();
    }

    // remove current video track (camera) from user's video container
    let obj = document.getElementById(`user-container-${user.uid}-screen`);
    if(obj!==null) {
        obj.remove();
    }

    console.log("Unsubscribe from user screen share, user: " + user.uid + ", media type: " + mediaType);
    await screenClient.unsubscribe(user,mediaType);
}

let toggleMic = async (e) => {
    let button;

    if(e===undefined || e===null) {
        button = document.getElementById("mic-btn");
    } else {
        button = e.currentTarget;
    }

    // check if this is the first time turning on the microphone
    // if yes, create audio stream
    if(localTracks[0] === undefined) {
        await joinAudioStream();
        button.classList.add('active');
        return;
    }

    // check if microphone is turned off
    if(!localTracks[0].enabled) {
        // turn on (unmute) microphone
        button.classList.add('active');
        await localTracks[0].setEnabled(true);
    } else {
        // turn off (mute) microphone
        button.classList.remove('active');
        await localTracks[0].setEnabled(false);
    }
}

let toggleCamera = async () => {
    let button = document.getElementById("camera-btn");

    // check if this is the first time turning on the camera
    // if yes, create camera video stream
    if(localTracks[1] === undefined) {
        await joinVideoStream();
        await localTracks[1].setEnabled(false);
    }

    // check if camera is turned off
    if(!localTracks[1].enabled) {
        // turn on camera
        button.classList.add('active');
        await localTracks[1].setEnabled(true);
        // hide user icon and start local playback
        await localTracks[1].play(`user-${uid}`);
    } else {
        // turn off camera
        button.classList.remove('active');
        await localTracks[1].setEnabled(false);
        // stop local playback and show user icon
        await localTracks[1].stop();
    }
}

// Handles LOCAL screen stream
let toggleScreen = async (e) => {
    let screenButton = e.currentTarget;
    let cameraButton = document.getElementById('camera-btn');
    // multiply by 10000, to make local streams consistent with remote streams
    let pomId = uid*10000;

    // turn off camera, turn on screen sharing
    if(!sharingScreen) {
        sharingScreen = true;

        screenButton.classList.add('active');

        // create video track for screen share
        localScreenTracks = await AgoraRTC.createScreenVideoTrack();

        // create a new player for screen share
        let player = `<div class="video__container" id="user-container-${pomId}-screen" style="position: relative">
                        <div class="video-player" id="user-${pomId}-screen"></div>
                  </div>`;
        // add player to HTML (presenter)
        displayFrame.insertAdjacentHTML('beforeend', player);

        localScreenTracks.play(`user-${pomId}-screen`);

        // publish screen share video
        await screenClient.publish([localScreenTracks]);

        let cameraPlayer = document.getElementById(`user-container-${uid}`);

        // if camera div already exists then remove it and append it to the screen share container
        if(cameraPlayer!=null) {
            cameraButton.classList.add('active');
            // stop local playback
            await localTracks[1].stop();
            // un-publish camera track
            await client.unpublish([localTracks[1]]);
            // remove existing container
            cameraPlayer.remove();

            // create again
            let cameraDiv = `<div class="camera_video_container" id="user-container-${uid}" style="position: absolute; 
                            height: 180px!important; width: 300px!important; top: 65%; left: 65%;">
                            <div class="video-player" id="user-${uid}"></div>
                        </div>`;

            // append to screen share
            let screenPlayer = document.getElementById(`user-container-${pomId}-screen`);
            screenPlayer.insertAdjacentHTML('beforeend', cameraDiv);

            // [0]-audio track, [1]-video track
            await localTracks[1].setEnabled(true);
            await localTracks[1].play(`user-${uid}`);
            // publish camera track
            await client.publish([localTracks[1]]);
        }

        // presenter screen
        displayFrame.style.display = 'block';
        // make it clickable (expands on click)
        document.getElementById(`user-container-${pomId}-screen`).addEventListener('click', expandVideoFrame);
        // save presenter id
        userIdInDisplayFrame = `user-container-${pomId}-screen`;

        // set other participants video stream boxes to 100px (because someone is sharing)
        let videoFrames = document.getElementsByClassName('video__container');
        for (let i=0; i<videoFrames.length; i++) {
            if(videoFrames[i].id !== userIdInDisplayFrame) {
                videoFrames[i].style.height = '180px';
                videoFrames[i].style.width = '300px';
            }
        }

        let cameraFrames = document.getElementsByClassName('camera_video_container');
        for(let i=0; i<cameraFrames.length; i++) {
            if(cameraFrames[i].id !== ("user-container-"+uid)) {
                cameraFrames[i].style.height = "90px";
                cameraFrames[i].style.width = "170px";
            }
        }
    } else {
        sharingScreen = false;

        let cameraPlayer = document.getElementById(`user-container-${uid}`);
        if(cameraPlayer!=null) {
            cameraPlayer.remove();
            // active to false, stop playback and enabled->false
            await toggleCamera();
            // un-publish camera track
            await client.unpublish([localTracks[1]]);
            // delete local camera track (to be created again)
            localTracks.splice(1,1);
        }

        screenButton.classList.remove('active');

        // remove current video track (screen share track)
        document.getElementById(`user-container-${pomId}-screen`).remove();
        // un-publish current screen share video track
        await screenClient.unpublish([localScreenTracks]);

        displayFrame.style.display = 'none';

        // set participants video stream boxes to 300px (because someone stopped screen sharing)
        let videoFrames = document.getElementsByClassName('video__container');
        for (let i=0; i<videoFrames.length; i++) {
            if(videoFrames[i].id !== userIdInDisplayFrame) {
                videoFrames[i].style.height = '300px';
                videoFrames[i].style.width = '500px';
            }
        }

        let cameraFrames = document.getElementsByClassName('camera_video_container');
        for(let i=0; i<cameraFrames.length; i++) {
            cameraFrames[i].style.height = "90px";
            cameraFrames[i].style.width = "170px";
        }
    }
}

let endRoom = async (e) => {
    // update the room's "end-time" in the database
    $.ajax({
        type: "GET",
        url: roomEndURL,
        headers: {
            "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
        },
        success: function () {
            sendRoomHasEndedMessage();
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });
}

let changeStudentStatusAfterInterruption = async () => {
    let dtoData = {
        studentId: currentLoggedInUser.id,
        newStudentStatus: "SUSPICIOUS"
    };

    $.ajax({
        url: "/api/room/edit-student-status/" + roomId,
        type: "POST",
        data: JSON.stringify(dtoData),
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

    // send channel message to all users for status change on current user
    channel.sendMessage({text:JSON.stringify({
            'type': 'status_change',
            'new_status': 'orange__icon',
            'user': uid
    })});
}

let changeStudentStatus = async (e) => {
    let newStatus = e.target.id;
    let id = e.target.parentElement.parentElement.parentElement.id;
    let studentId = id.split("__")[1];
    let dtoData = {
        studentId: studentId,
        newStudentStatus: newStatus
    };

    let isBlocked = false;
    let newStatusClass;
    let newBorderClass;
    if (newStatus === "IDENTIFIED") {
        newStatusClass = "green__icon"
        newBorderClass = "#2aca3e"
    } else if (newStatus === "SUSPICIOUS") {
        newStatusClass = "orange__icon";
        newBorderClass = "#FFA500"
    } else {
        newStatusClass = "red__icon";
        newBorderClass = "#ff0000";
        isBlocked = true;
    }

    // console.log(dtoData)
    let url = "/api/room/edit-student-status/" + roomId;

    // call to change student status
    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(dtoData),
        contentType: "application/json",
        headers: {
            "Authorization":
                "Bearer " + JSON.parse(window.localStorage.getItem('accessToken')),
        },
        success: function (data, response) {
            console.log(data);
            let span = e.target.parentElement.parentElement.previousElementSibling.previousElementSibling;
            let userID = e.target.parentElement.parentElement.parentElement.getAttribute('id').match((/(\d+)/))[0];

            let screenContainer = document.getElementById("user-container-" + (userID * 10000) + "-screen");
            if (screenContainer != null) {
                screenContainer.style.borderColor = newBorderClass;
                // change screen-container position based on the new status
                let parentElem = screenContainer.parentElement;
                parentElem.removeChild(screenContainer);
                let newContainerId = newStatus.toLowerCase() + "__container";
                document.getElementById(newContainerId).appendChild(screenContainer);
            }
            let userContainer = document.getElementById("user-container-" + userID);
            if (userContainer != null) {
                userContainer.style.borderColor = newBorderClass;
                if (screenContainer == null) {
                    // change user-container position based on the new status
                    let parentElem = userContainer.parentElement;
                    parentElem.removeChild(userContainer);
                    let newContainerId = newStatus.toLowerCase() + "__container";
                    document.getElementById(newContainerId).appendChild(userContainer);
                }
            }
            span.removeAttribute("class");
            span.classList.add(newStatusClass);
        },
        error: function (rs) {
            console.error(rs.status);
            console.error(rs.responseText);
        }
    });

    if (isBlocked) {
        // send message for blocked user to be kicked out
        channel.sendMessage({
            text: JSON.stringify({
                'type': 'block-user',
                'message': "Blocked user with id: " + studentId,
                'user': studentId
            })
        });
    }
}

document.getElementById('mic-btn').addEventListener('click', toggleMic);
document.getElementById('admin-mic-btn').addEventListener('click', sendToggleAllMicrophonesMessage)
document.getElementById('camera-btn').addEventListener('click', toggleCamera);
document.getElementById('screen-btn').addEventListener('click', toggleScreen);
document.getElementById('end__room__btn').addEventListener('click', endRoom);
$(".student_status").on('click', changeStudentStatus);

$(function () {
    $('[data-toggle="tooltip"]').tooltip();
});

joinRoomInit();