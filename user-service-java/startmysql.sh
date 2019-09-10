#!/usr/bin/env bash

cd $(dirname $0)
cur_dir=`pwd`
docker stop mysql-local
docker rm mysql-local
# Use docker to easily start mysql locally for dev purpose
# $docker ps //check if mysql image is runing
# $netstat -na|grep 3306 //check if port is listening
# Use DBeaver to connect to local mysql db, set allowPublicKeyRetrival=true and useSSL=false to avoid connection failure
docker run --name mysql-local -v ${cur_dir}/conf:/etc/mysql/conf.d -v ${cur_dir}/data:/var/lib/mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=aB123456 -d mysql:latest