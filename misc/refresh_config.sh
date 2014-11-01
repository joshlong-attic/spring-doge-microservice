#!/bin/sh

set -e
#todo: this should use cf tool to get uri of doge-service
#todo make that an option

curl -d {} http://127.0.0.1:8089/refresh
