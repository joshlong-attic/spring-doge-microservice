#!/bin/bash
set -e

app_name=client

# cleanup if necessary
cf apps  | grep $app_name && cf delete $app_name

# push the app proper to CF
echo pushing $app_name
cf push
cf delete-orphaned-routes

# register as a service (deleting existing one if it exists)
uri=`cf apps | grep $app_name | tr -s ' ' | cut -d' ' -f 6`


echo "deployed $app_name to $uri.";




