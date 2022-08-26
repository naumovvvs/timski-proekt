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
let userIdInDisplayFrame = null;

let expandVideoFrame = (e) => {
  let child = displayFrame.children[0];

  if(child) {
    // remove item from stream box
    document.getElementById('streams__container').appendChild(child);
  }

  displayFrame.style.display = 'block';
  displayFrame.appendChild(e.currentTarget);
  userIdInDisplayFrame = e.currentTarget.id;

  // make other user's streams smaller
  for (let i=0; i<videoFrames.length; i++) {
    if(videoFrames[i].id !== userIdInDisplayFrame) {
      videoFrames[i].style.height = '100px';
      videoFrames[i].style.width = '100px';
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
  document.getElementById('streams__container').appendChild(child);

  // make user's streams bigger after clicking on presenter's screen (un-expand)
  for(let i=0; i<videoFrames.length; i++) {
    videoFrames[i].style.height = "300px";
    videoFrames[i].style.width = "300px";
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
      }
    },
    error: function (rs) {
      console.error(rs.status);
      console.error(rs.responseText);
    }
  });
});