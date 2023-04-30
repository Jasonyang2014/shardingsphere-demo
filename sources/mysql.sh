#!/bin/bash

# MySQL login credentials
username="root"
password="yang123test"
database="db_user"

# SQL query to execute
sql_file="source /etc/mysql/conf.d/users.sql"

# Log in to MySQL shell and execute query
sql="mysql -u \"$username\" -p\"$password\" \"$database\" -e \"$sql_file\""

docker exec -it mysql-master env LANG=C.UTF_8 /bin/bash -c "$sql"

# Log out of MySQL shell
exit
