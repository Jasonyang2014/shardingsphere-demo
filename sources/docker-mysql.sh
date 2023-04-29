#!/bin/bash
set -e
ARG_NUMBER=$#

if [ $ARG_NUMBER -lt 1 ]
then
        echo "Please input docker container name."
        exit 0
fi

# Set the name of the container
CONTAINER_NAME=$1

# Get the status of the container
CONTAINER_STATUS=$(docker inspect -f '{{.State.Status}}' "$CONTAINER_NAME")

# Print the container status
echo "Container $CONTAINER_NAME status: $CONTAINER_STATUS"

function enter_mysql {
        docker exec -it "$1" env LANG=C.UTF_8 /bin/bash
}

if [ "$CONTAINER_STATUS" == "running" ]
then
        enter_mysql "$CONTAINER_NAME"
elif [ "$CONTAINER_STATUS" == "exited" ]
then
        echo "start $CONTAINER_NAME"
        docker start "$CONTAINER_NAME"
        echo "enter container"
        enter_mysql "$CONTAINER_NAME"
else
        echo "Can't do anything more, please check the container status."
fi