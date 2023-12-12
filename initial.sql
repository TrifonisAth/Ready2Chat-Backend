create table users (
	user_id int auto_increment primary key,
	email varchar(80) not null unique,
    name varchar(80) not null unique,
    password varchar(80) not null,
    is_online boolean not null default false,
    created_at timestamp default now(),
    updated_at timestamp default now() on update now()
    );
     

create table friendships (
	id int auto_increment primary key,
    user_id1 int not null,
    user_id2 int not null,
    foreign key (user_id1) references users(user_id)
    on delete cascade on update no action,
    foreign key (user_id2) references users(user_id)
    on delete cascade on update no action
);

create table friend_requests (
	id int auto_increment primary key,
    sender_user_id int not null,
    receiver_user_id int not null,
    unique key (sender_user_id, receiver_user_id),
    foreign key (sender_user_id) references users(user_id)
    on delete cascade on update no action,
    foreign key (receiver_user_id) references users(user_id)
    on delete cascade on update no action
);

create table verification_tokens(
	user_id int primary key,
	token varchar(6) not null,
    expiration_time timestamp not null,
    key FK_USER_ID_idx (user_id),
    constraint FK_USER
    foreign key (user_id) references users(user_id)
    on delete cascade on update no action
);

create table roles (
    id int auto_increment primary key,
	user_id int,
    role enum('ROLE_TEMP', 'ROLE_VERIFIED', 'ROLE_ADMIN') default 'ROLE_TEMP',
    unique key authorities_idx_1 (user_id, role),
	foreign key (user_id) REFERENCES users (user_id)
     on delete cascade on update no action
);

create table refresh_tokens(
	token_id int auto_increment primary key,
    token varchar(256),
    user_id int,
    creation_date timestamp default now(),
    expiration_date timestamp,
    foreign key (user_id) references users(user_id)
    on delete cascade on update no action
);

create table messages (
	id int auto_increment primary key,
    sender_id int not null,
    recipient_id int not null,
    message varchar(200),
    created_at timestamp default current_timestamp,
    conversation_id int,
    foreign key (conversation_id) references friendships(id)
    on delete cascade on update no action,
    foreign key (sender_id) references users(user_id)
    on delete cascade on update no action,
    foreign key (recipient_id) references users(user_id)
    on delete cascade on update no action
);