#!/usr/bin/env bash

set -e

cd $(dirname $0)

label=$2
image="api-gateway"
# change to your registered docker hub username
registry="r5by/api-gateway"

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
