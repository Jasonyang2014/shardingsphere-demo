#!/bin/bash
set -e

echo "Please input your container name."
read container_name
echo "Please input your db password."
read -s db_password

if [ ${#container_name} -lt 5 ]; then
  echo "Container name [$container_name] is too short."
  exit 0
fi
if [ ${#db_password} -lt 6 ]; then
  echo "Password is too short."
  exit 0
fi
echo "Container name [$container_name] password => [$db_password]"
# check container if exists
if docker ps -a --format '{{.Names}}' | grep -q "^$container_name\$"; then
  echo "$container_name exists"
  c_status=$(docker inspect -f '{{.State.Status}}' "$container_name")
  if [ "$c_status" == "running" ]; then
    echo "Start stop $container_name"
    sleep 1s
    docker stop "$container_name"
    echo "Start remove $container_name"
    docker rm "$container_name"
  else
    echo "Remove container $container_name"
    docker rm "$container_name"
  fi
fi

mysql_conf="$HOME/$container_name/mysql/conf"
mysql_data="$HOME/$container_name/mysql/data"

if [ -d "$HOME/$container_name" ]; then
  echo "$container_name is already exists. remove the old dir and create it again"
  sudo rm -rf "$HOME/$container_name"
  mkdir -p "$HOME/$container_name"
fi

echo "Install mysql docker container"
docker run -d \
  -p 3307:3306 \
  -v "$mysql_conf":/etc/mysql/conf.d \
  -v "$mysql_data":/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD="$db_password" \
  --name "$container_name" mysql

if docker ps -a --format '{{.Names}}' | grep -q "^$container_name\$"; then
  echo "Install $container_name success."
else
  echo "Install $container_name failure."
  exit 0
fi
container_status=$(docker inspect -f '{{.State.Status}}' "$container_name")

echo "$container_name is $container_status."
if [ "$container_status" == "running" ]; then
  docker exec -it "$container_name" env LANG=C.UTF_8 /bin/bash
else
  echo "Exit."
  exit 0
fi
