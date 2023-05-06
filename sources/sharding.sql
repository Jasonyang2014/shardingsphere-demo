drop table if exists t_user;
create table t_user(
   id bigint primary key not null comment "id",
   name varchar(20) comment "name"
);

-- 订单表 水平分片
drop table if exists t_order0 ;
create table t_order0(
    order_no bigint primary key not null comment "order id",
    user_id bigint not null comment "user id",
    amount decimal(5,2) default 0.0 comment "order amount"
);


drop table if exists t_order1 ;
create table t_order1(
    order_no bigint primary key not null comment "order id",
    user_id bigint not null comment "user id",
    amount decimal(5,2) default 0.0 comment "order amount"
);

-- 订单详情表 水平分片
drop table if exists t_order_item0;
create table t_order_item0(
    id bigint primary key not null comment "id",
    order_no bigint not null comment "order id",
    description varchar(50) null comment "description"
);


drop table if exists t_order_item1;
create table t_order_item1(
    id bigint primary key not null comment "id",
    order_no bigint not null comment "order id",
    description varchar(50) null comment "description"
);