#!/bin/bash
set -e

app_name=doge-service

# cleanup if necessary
cf apps  | grep $app_name && cf delete $app_name

# push the app proper to CF
echo pushing $app_name
cf push
cf delete-orphaned-routes




