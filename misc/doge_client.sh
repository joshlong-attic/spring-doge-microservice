#!/bin/sh
set -e

curl -F "file=@$2" http://127.0.0.1:8089/doges/$1/photos
