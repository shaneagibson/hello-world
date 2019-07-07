#!/bin/bash

region=eu-west-2

image=$(./gradlew dockerImage -q)

function login_to_ecr {
  login_cmd=$(aws ecr get-login --no-include-email --region $region | sed 's|https://||g')
  eval $login_cmd > /dev/null
  ecr_hostname=$(echo $login_cmd | awk '{print $NF}')
  echo $ecr_hostname
}

function create_repository {
  repository=$1
  aws ecr create-repository --repository-name $repository > /dev/null
}

function release_image {
  image=$1
  tag=$2
  docker tag $image $tag
  docker push $tag
}

ecr_hostname=$(login_to_ecr)

repository=$(echo ${image} | awk -F: '{print $1}')
tag=$(echo ${image} | awk -F: '{print $2}')

create_repository $repository

release_image $image $ecr_hostname/$repository:$tag
release_image $image $ecr_hostname/$repository:latest