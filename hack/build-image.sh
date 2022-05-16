#!/usr/bin/env bash
set -eux

echo
echo "Start build images, Repo: $1, Tag: $2"
echo

for dir in frontend; do
    if [[ -d $dir ]]; then
        if [[ -n $(ls "$dir" | grep -i Dockerfile) ]]; then
            echo "build ${dir}"
            docker build --build-arg VERSION=$2 -t "$1"/walmart-"${dir}" "$dir" --platform linux/x86_64
            docker tag "$1"/walmart-"${dir}":latest "$1"/walmart-"${dir}":"$2"
        fi
    fi
done

for dir in walmart-*; do
    if [[ -d $dir ]]; then
        if [[ -n $(ls "$dir" | grep -i Dockerfile) ]]; then
            echo "build ${dir}"
            docker build -t "$1"/"${dir}" "$dir"
            docker tag "$1"/"${dir}":latest "$1"/"${dir}":"$2"
        fi
    fi
done
