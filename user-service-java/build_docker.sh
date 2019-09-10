#!/usr/bin/env bash

set -e

cd $(dirname $0)

label=$2
image="user-service-backend"
registry="r5by/user-service-backend"

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

# Usage (after startmysql.sh)
# $ docker run -it user-service-backend:latest --mysql.address=<en0, e.g. 192.168.0.101>
