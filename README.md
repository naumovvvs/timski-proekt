# Timski Proekt
*WebRTC-based educational web platform for video conferencing (Team Project)*
___
## Technologies used
* Java
* Spring Boot
* WebRTC (Agora Web SDK)
* Java 8 Stream API
* REST API
* Bootstrap
* PostgreSQL
* HTML/CSS/JS
* jQuery

## Functionalities
* Sign up/log in (three different types of users: admin, student, professor)
* Admin can create courses and assign professors
* Professor can add students and create rooms in courses
* Professor can identify, mark as suspicious and block (kick) students from the room
* Professor can pin a message, and send HTML edited message with color and other styles
* Professor can toggle microphones of all participants
* Each room has a start time, before which students cannot enter
* Each room has a list of allowed students, only students on the list can enter
* Each room has extensive logging, and after room ends, a detailed report is provided with the student's activity in the room
* Each room has interruption detection which monitors users connection and in a case when a user disconnects, it measures the duration
* Each room has a chatbot that welcomes users when they enter the room, and notify professor only about all other activity
* Student (and Professor) can share camera only, screen only or both at the same time and automatically get two videos contained in one (ManyCam)
* Student can send messages in chat
* Student gets detailed report after room ends about his/her activity in the room
* Custom 403 page

## The team
* Strasho Naumov
    * strase10naumov@outlook.com
    * strasho.naumov@students.finki.ukim.mk
* Jelena Ognjanoska
    * jelena.ognjanoska@students.finki.ukim.mk
* Kristijan Isajloski
    * kristijan.isajloski@students.finki.ukim.mk
