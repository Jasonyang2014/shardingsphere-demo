-- % 通配符，代表任意
create user 'auyeung_slave'@'%';
-- 设置密码
alter user 'auyeung_slave'@'%' identitied with mysql_native_password by 'yang';
-- 设置权限
grant replication slave on *.* to 'auyeung_slave'@'%';
-- 刷新权限
flush privileges;

show master status;