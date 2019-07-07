#!/bin/bash -e

git_username=$1
git_password=$2

image=$(./gradlew dockerImage -q)

service="hello-world"
version=${image#*:}
environment="ci"

github_base_url="https://${git_username}:${git_password}@api.github.com"

service_version_url="${github_base_url}/repos/shaneagibson/environment-manager/contents/services/${service}.version"

content=$(echo ${version} | base64)
sha=$(curl -X GET "${service_version_url}?ref=${environment}" | jq -r ".sha")
commit_message="CI pipeline commit for ${service}:${version}"
payload="{ \"message\": \"${commit_message}\", \"content\": \"${content}\", \"sha\": \"${sha}\", \"branch\": \"${environment}\" }"

curl -X PUT "${service_version_url}" -H "Content-Type: application/json" --data "${payload}"