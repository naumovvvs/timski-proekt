select r.name, r.status, r.start_time, r.end_time, st.index, sr.enter_time, sr.leave_time, sr.status
from room r, student_in_room sr, student st
where s.id=sr.room_id and sr.student_id=st.id


/*
 vo room so 'name' i 'status', sto zapocnala vo 'start_time' i zavrsila vo 'end_time',
 student so 'index' vlegol vo 'enter_time', izlegol vo 'leave_time' i imal 'status'
 */