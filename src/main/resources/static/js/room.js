let messagesContainer = document.getElementById('messages');
messagesContainer.scrollTop = messagesContainer.scrollHeight;

const memberContainer = document.getElementById('members__container');
const memberButton = document.getElementById('members__button');

const chatContainer = document.getElementById('messages__container');
const chatButton = document.getElementById('chat__button');

let activeMemberContainer = false;

memberButton.addEventListener('click', () => {
  if (activeMemberContainer) {
    memberContainer.style.display = 'none';
  } else {
    memberContainer.style.display = 'block';
  }

  activeMemberContainer = !activeMemberContainer;
});

let activeChatContainer = false;

chatButton.addEventListener('click', () => {
  if (activeChatContainer) {
    chatContainer.style.display = 'none';
  } else {
    chatContainer.style.display = 'block';
  }

  activeChatContainer = !activeChatContainer;
});

let displayFrame = document.getElementById('stream__box');
let videoFrames = document.getElementsByClassName('video__container');
let cameraFrames = document.getElementsByClassName('camera_video_container');
let userIdInDisplayFrame = null;

let expandVideoFrame = (e) => {
  let child = displayFrame.children[0];

  if(child) {
    // remove item from stream box
    document.getElementById('streams__container').appendChild(child);
  }

  displayFrame.style.display = 'block';

  // if camera stream exists, make it bigger (300x180)
  if(e.currentTarget.children[1] != undefined) {
    e.currentTarget.children[1].style.setProperty("width", "300px", "important");
    e.currentTarget.children[1].style.setProperty("height", "180px", "important");
  }

  // append the presenter to stream box (display frame)
  displayFrame.appendChild(e.currentTarget);
  // id of the user (ex. user-container-10000-screen)
  userIdInDisplayFrame = e.currentTarget.id;
  // add class for correct expansion
  e.currentTarget.classList.add("cameraExpand");

  // make other user's streams smaller (skip current user)
  for (let i=0; i<videoFrames.length; i++) {
    if(videoFrames[i].id !== userIdInDisplayFrame) {
      videoFrames[i].style.height = '180px';
      videoFrames[i].style.width = '300px';
    }
  }

  // make other users cameras smaller (skip current user)
  for (let i=0; i<cameraFrames.length; i++) {
    let arr = cameraFrames[i].id.split("-");
    let userId = arr[2]*10000;
    let pom = (cameraFrames[i].id.substring(0,15))+userId+"-screen";

    if(cameraFrames[i].id === userIdInDisplayFrame) {
      continue;
    }

    if(pom !== userIdInDisplayFrame) {
      cameraFrames[i].style.height = '90px';
      cameraFrames[i].style.width = '170px';
    }
  }
}

// add event listeners to all video frames for expanding video
for (let i=0; i<videoFrames.length; i++) {
  videoFrames[i].addEventListener('click', expandVideoFrame);
}

let hideDisplayFrame = () => {
  userIdInDisplayFrame = null;
  displayFrame.style.display = null;

  // remove user presenting (unpin user)
  let child = displayFrame.children[0];
  // remove class to avoid wrong width and height
  child.classList.remove("cameraExpand");
  // remove from stream box and append to container below
  document.getElementById('streams__container').appendChild(child);

  // make user's streams bigger after clicking on presenter's screen (un-expand)
  for(let i=0; i<videoFrames.length; i++) {
    videoFrames[i].style.height = "300px";
    videoFrames[i].style.width = "500px";
  }

  for(let i=0; i<cameraFrames.length; i++) {
    let arr = cameraFrames[i].id.split("-");
    let userId = arr[2]*10000;
    let pom = (cameraFrames[i].id.substring(0,15))+userId+"-screen";
    let screenDiv = document.getElementById(pom);

    // if user has shared screen, make cammera smaller when un-expanding
    if(screenDiv!=null){
      cameraFrames[i].style.height = "90px";
      cameraFrames[i].style.width = "170px";
    }
  }
}

displayFrame.addEventListener('click', hideDisplayFrame);

// hide end-session button for users with non-moderator role
$(document).ready(function() {
  $.ajax({
    url: "/api/user/current",
    type: "GET",
    async: false,
    headers: {
      "Authorization": "Bearer " + JSON.parse(window.localStorage.getItem("accessToken"))
    },
    success: function (response) {
      if(!(response.roles[0].name === "ROLE_PROFESSOR")){
        $("#end__room__btn").css("display", "none");
        $("#admin-mic-btn").css("display", "none");
        $(".html__editor__show").css("display", "none");
        $(".html__editor__hide").css("display", "none");
      }
    },
    error: function (rs) {
      console.error(rs.status);
      console.error(rs.responseText);
    }
  });
});