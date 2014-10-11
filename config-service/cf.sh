#!/bin/bash
set -e

app_name=config-service

# cleanup if necessary
cf apps  | grep $app_name && {yes | cf delete $app_name }

# push the app proper to CF
echo pushing $app_name
cf push
yes | cf delete-orphaned-routes

# register as a service (deleting existing one if it exists)
uri=`cf apps | grep $app_name | tr -s ' ' | cut -d' ' -f 6`
cf s | grep $app_name && {yes | cf ds $app_name}
cf cups $app_name  -p '{"uri":"http://'$uri'"}'


echo "deployed config-service ($uri). Access with env var: 'vcap.services.config-service.credentials.uri'";




