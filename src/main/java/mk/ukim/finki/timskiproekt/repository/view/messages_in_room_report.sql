select r.name, r.status, st.index, m.content, m.sent_at
from room r, chat c, message m, student st
where r.id=c.room_id and c.id=message.chat_id and st.id=m.sender_id


/*
 vo room so 'name' i 'status',
 student so 'index' ispratil poraka so 'content' vo vreme 'sent_at'
 */