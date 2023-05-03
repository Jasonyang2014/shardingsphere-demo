drop table if exists t_user;
create table t_user(
   id bigint primary key not null comment "id",
   name varchar(20) comment "name"
);

drop table if exists t_order ;
create table t_order0(
    order_no bigint primary key not null comment "order id",
    user_id bigint not null comment "user id",
    amount decimal(5,2) default 0.0 comment "order amount"
);

drop table if exists t_order_item;
create table t_order_item(
    id bigint primary key not null comment "id",
    order_no bigint not null comment "order id",
    description varchar(50) null comment "description"
);