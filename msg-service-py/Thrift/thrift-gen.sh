#!/usr/bin/env bash
cd ./msg-service-py/Thrift
thrift --gen py -out ../ message.thrift

# generate java code for thrift api
thrift --gen java -out ../../msg-service-api/src/main/java message.thrift