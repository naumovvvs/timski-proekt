select s.name, s.code, s.status, st.index, m.content, m.sent_at
from session s, chat c, message m, student st
where s.id=c.session_id and c.id=message.chat_id and st.id=m.sender_id


/*
 vo sesija so 'name', 'code' i 'status',
 student so 'index' ispratil poraka so 'content' vo vreme 'sent_at'
 */