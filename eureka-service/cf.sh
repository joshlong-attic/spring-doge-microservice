#!/bin/bash
set -e

APP_NAME=eureka-service
DOMAIN=${DOMAIN:-run.pivotal.io}
TARGET=api.${DOMAIN}
APPLICATION_DOMAIN=${APPLICATION_DOMAIN:-"$DOMAIN"}

if [ "$DOMAIN" == "run.pivotal.io" ]; then
    APPLICATION_DOMAIN=cfapps.io
fi

cf api | grep ${TARGET} || cf api ${TARGET} --skip-ssl-validation
cf a | grep OK || cf login
cf push $APP_NAME --no-start

cf restart $APP_NAME
APP_URI=`cf apps | grep $APP_NAME | tr -s ' ' | cut -d' ' -f 6`

# find it, update it
P='{"uri":"http://'$APP_NAME.$APPLICATION_DOMAIN'"}'
echo $P
cf s | grep $APP_NAME && cf uups $APP_NAME  -p $P
# find it OR create it
cf s | grep $APP_NAME ||  cf cups $APP_NAME  -p $P


echo "deployed $APP_NAME to $APP_URI. Access as a service using env var: 'vcap.services.$APP_NAME.credentials.uri'";




