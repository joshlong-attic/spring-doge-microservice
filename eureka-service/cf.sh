#!/bin/bash
set -e

app_name=eureka-service

# cleanup if necessary
cf apps  | grep $app_name && cf delete $app_name

# push the app proper to CF
echo pushing $app_name
cf push
cf delete-orphaned-routes

# register as a service (deleting existing one if it exists)
uri=`cf apps | grep $app_name | tr -s ' ' | cut -d' ' -f 6`
cf s | grep $app_name && cf ds $app_name
cf cups $app_name  -p '{"uri":"http://'$uri'"}'

echo "deployed $app_name to $uri. Access as a service using env var: 'vcap.services.$app_name.credentials.uri'";




