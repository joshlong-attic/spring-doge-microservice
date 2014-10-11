#!/bin/bash
set -e

app_name=config-service

# cleanup if necessary
cf apps  | grep $app_name && {yes | cf delete $app_name }

# push the app proper to CF
echo pushing $app_name
cf push

# figure out where the service lives and make it a service for other nodes.
uri=`cf apps | grep $app_name | tr -s ' ' | cut -d' ' -f 6`
cf cups $app_name  -p '{"uri":"http://'$uri'"}'
echo deployed user-provided-service at $uri. You can bind to it from other apps and reference the VCAP_SERVICES variable 'vcap.services.config-service.credentials.uri'


