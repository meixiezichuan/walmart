drop table if exists user;
create table user(
    id varchar(36) not null,
    name varchar(256) not null unique ,
    psw varchar(256) not null,
    role int not null,
    email varchar(256),
    phone_num varchar(256),
    address varchar(256),
    primary key(id)
);