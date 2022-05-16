#!/usr/bin/env bash
set -eux

echo
echo "Start build images, Repo: $1, Tag: $2"
echo

# frontend
echo "build frontend"
docker build -t "$1"/walmart-frontend:"$2"  frontend

# eureka-server
echo "build eureka-server"
docker build -t "$1"/walmart-eureka-server:"$2" -f eureka-server/Dockerfile.arm64 eureka-server

for dir in walmart-*; do
    if [[ -d $dir ]]; then
        if [[ -n $(ls "$dir" | grep -i Dockerfile) ]]; then
            echo "build ${dir}"
            docker build -t "$1"/"${dir}":"$2" -f "$dir"/Dockerfile.arm64 "$dir"
        fi
    fi
done
