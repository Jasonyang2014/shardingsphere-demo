# MySQL master slave install and configuration

mysql主从复制操作

使用docker安装

```bash
docker run -d \
-p 3306:3306 \
-v /home/ubuntu/mysql/conf:/etc/mysql/conf.d \
-v /home/ubuntu/mysql/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=auyeung \
--name mysql-master \
mysql
```

安装成功后，可以使用如下命令查看

```bash
//查看是否启动成功
docker ps
//查看启动日志
docker logs mysql-master
```

值得注意的是，在Linux中容易因为防火墙导致docker启动不成功。

如果容器启动失败，可以重新创建容器，注意将原来的数据清除。

```bash
//查看所有的容器
docker ps -a
//删除容器
docker rm container-id
//查看所有的image
docker images
//删除image
docker rmi image-id
```

启动成功后，我们可以进入容器内部

```bash
docker exec -it mysql-master env LANG=C.UTF_8 /bin/bash
```

创建用户用来同步数据

```sql
// % 通配符，代表任意
create user 'auyeung_slave'@'%';
// 设置密码
alter user 'auyeung_slave'@'%' identitied with mysql_native_password by 'yang';
// 设置权限
grant replication slave on *.* to 'auyeung_slave'@'%';
// 刷新权限
flush privileges;
```

查看主主从状态

```sql
//查看主服务状态
show master status;
```

配置从服务器

```sql
change master to 
master_host='192.18.134.94',
master_port=3307,
master_user='auyeung',
master_password='yang123test',
master_log_file='binlog.000002',
master_log_pos=1057;
```

查看从服务器的状态

```sql
//启动从服务器
start slave;
// 查看从服务器的状态
show slave status;
//这两个状态在running才算正常
//Slave_IO_Running: Yes
//Slave_SQL_Running: Yes
```

启动mysql脚本

```bash
#!/bin/bash
set -e
echo "Please input your container name."
# Set the name of the container
read CONTAINER_NAME
LEN=${#CONTAINER_NAME}
if [ "$LEN" -lt 1 ]; then
  echo "Input error, exit."
  exit 0
fi
MYSQL_CONF="$HOME/mysql/conf"
MYSQL_DATA="$HOME/mysql/data"
ROOT_PASSWORD=yang123test
# Check container if exists
if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME\$"; then
  echo "The container $CONTAINER_NAME exists."
else
  echo "The container $CONTAINER_NAME does not exist."
  echo "Start install container."
  # Run docker
  docker run -d \
    -p 3306:3306 \
    -v "$MYSQL_CONF":/etc/mysql/conf.d \
    -v "$MYSQL_DATA":/var/lib/mysql \
    -e MYSQL_ROOT_PASSWORD="$ROOT_PASSWORD" \
    --name "$CONTAINER_NAME" \
    mysql
fi

# Get the status of the container
CONTAINER_STATUS=$(docker inspect -f '{{.State.Status}}' "$CONTAINER_NAME")

# Print the container status
echo "Container $CONTAINER_NAME status: $CONTAINER_STATUS"

function enter_mysql {
  docker exec -it "$1" env LANG=C.UTF_8 /bin/bash
}

if [ "$CONTAINER_STATUS" == "running" ]; then
  enter_mysql "$CONTAINER_NAME"
elif [ "$CONTAINER_STATUS" == "exited" ]; then
  echo "Start $CONTAINER_NAME"
  docker start "$CONTAINER_NAME"
  echo "Enter container"
  enter_mysql "$CONTAINER_NAME"
else
  echo "Can't do anything more, please check the container status."
fi
```