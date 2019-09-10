#!/usr/bin/env bash
cd ./user-service-api/Thrift
thrift --gen java -out ../src/main/java user_service.thrift