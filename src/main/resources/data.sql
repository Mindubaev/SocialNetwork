insert into profile(name,lastname,birth_date,login,password) values 
('Ivan','Ivanov','2015-12-12','admin','admin');
insert into profile(name,lastname,birth_date,login,password) values 
('Artur','Mindubaev','1999-02-01','user','user');
insert into profile(name,lastname,birth_date,login,password) values 
('John','Smith','1987-02-01','test1','test1');
insert into chat (title) values 
('Conversation');
insert into chat_profile_relation (profile_id,chat_id) values
(1,1);
insert into chat_profile_relation (profile_id,chat_id) values
(2,1);
insert into message (sender_id,chat_id,text,date) values
(1,1,'Hellow!','2015-12-12');
insert into message (sender_id,chat_id,text,date) values
(2,1,'Hi!','2019-02-21');
insert into topic (profile_id,text) values
(1,'Java is a general-purpose programming language that is class-based, object-oriented, and designed to have as few implementation dependencies as possible. It is intended to let application developers write once, run anywhere (WORA),[15] meaning that compiled Java code can run on all platforms that support Java without the need for recompilation.[16] Java applications are typically compiled to bytecode that can run on any Java virtual machine (JVM) regardless of the underlying computer architecture. The syntax of Java is similar to C and C++, but it has fewer low-level facilities than either of them. As of 2019, Java was one of the most popular programming languages in use according to GitHub, particularly for client-server web applications, with a reported 9 million developers.');
insert into topic (profile_id,text) values
(2,'JavaScript® (often shortened to JS) is a lightweight, interpreted, object-oriented language with first-class functions, most known as the scripting language for Web pages, but used in many non-browser environments as well such as node.js or Apache CouchDB. It is a prototype-based, multi-paradigm scripting language that is dynamic, and supports object-oriented, imperative, and functional programming styles.');
insert into comment (profile_id,topic_id,text,date) values
(2,1,'Good article!','2019-10-1');
insert into comment (profile_id,topic_id,text,date) values
(3,1,'Great...!','2019-10-1');
insert into comment (profile_id,topic_id,text,date) values
(3,2,'Here we go again!','2019-10-1');
insert into comment (profile_id,topic_id,text,date) values
(1,2,'Never like id...','2019-10-1');