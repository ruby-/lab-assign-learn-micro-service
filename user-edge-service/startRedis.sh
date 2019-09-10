#!/usr/bin/env bash
cd $(dirname $0)
docker stop redis
docker rm redis
# -v persistent data storage (docker restart with this data)
docker run -idt -p 6379:6379 -v `pwd`/data:/data --name redis -v `pwd`/conf/redis.conf:/etc/redis/redis_default.conf -d redis:latest
# verify
# $telnet localhost 6379
# $set a 1
# $get a