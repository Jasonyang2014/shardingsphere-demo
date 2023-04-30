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
#check container if exists
if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME\$"; then
  echo "The container $CONTAINER_NAME exists."
else
  echo "The container $CONTAINER_NAME does not exist."
  echo "start install container."
  #   run docker
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
  echo "start $CONTAINER_NAME"
  docker start "$CONTAINER_NAME"
  echo "enter container"
  enter_mysql "$CONTAINER_NAME"
else
  echo "Can't do anything more, please check the container status."
fi
