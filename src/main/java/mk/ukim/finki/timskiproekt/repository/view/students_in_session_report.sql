select s.name, s.code, s.status, s.start_time, s.end_time, st.index, ss.enter_time, ss.leave_time, ss.status
from session s, student_in_session ss, student st
where s.id=ss.session_id and ss.student_id=st.id


/*
 vo sesija so 'name', 'code' i 'status', sto zapocnala vo 'start_time' i zavrsila vo 'end_time',
 student so 'index' vlegol vo 'enter_time', izlegol vo 'leave_time' i imal 'status'
 */