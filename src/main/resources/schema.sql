create table if not exists profile(id identity, 
name varchar(20) not null,
lastname varchar(20) not null, 
photo blob,
birth_date date,
login varchar(50) not null, 
password varchar(200) not null,
primary key (id)
);
create table if not exists chat(id identity, 
title varchar(50),
primary key (id),
);
create table if not exists chat_profile_relation(id identity, 
profile_id integer, 
chat_id integer,
primary key(id),
foreign key (profile_id) references profile(id),
foreign key (chat_id) references chat(id)
);
create table if not exists message(id identity, 
sender_id integer,
chat_id integer,
text varchar(1000) not null, 
date date not null,
primary key (id),
foreign key (sender_id) references profile(id),
foreign key (chat_id) references chat(id)
);
create table if not exists topic(id identity, 
profile_id integer not null, 
text varchar (3000) not null,
primary key (id),
foreign key (profile_id) references profile(id)
);
create table if not exists comment(id identity, 
profile_id  integer, 
topic_id integer, 
text varchar(500) not null, 
date date not null,
primary key (id),
foreign key (profile_id) references profile(id),
foreign key (topic_id) references topic(id)
);


