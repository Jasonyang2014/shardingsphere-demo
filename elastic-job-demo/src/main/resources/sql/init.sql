drop table if exists t_user;

create table t_user(
id bigint not null auto_increment primary key,
name varchar(20) not null,
gender tinyint(1) not null
)engine=Innodb;