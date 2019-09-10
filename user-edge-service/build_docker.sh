#!/usr/bin/env bash

set -e

cd $(dirname $0)

label=$2
image="user-service"
# change to your registered docker hub username
registry="r5by/user-service"

if [[ -z "$label" ]]; then
  label=latest
fi

mvn package

docker build -t ${image}:${label} .

if [[ "push" == $1 ]]; then
  tag=$(docker images -q $image:$label)
  docker tag ${tag} ${registry}:${label}
  docker push ${registry}:${label}
fi

# Usage (after redis start)
# $ docker run -it user-service:latest --redis.address=<redis ip, e.g. 127.0.0.1>
