#!/bin/bash
set -e

APP_NAME=config-service
DOMAIN=${DOMAIN:-run.pivotal.io}
TARGET=api.${DOMAIN}
APPLICATION_DOMAIN=${APPLICATION_DOMAIN:-"$DOMAIN"}

echo $APPLICATION_DOMAIN $APP_NAME $DOMAIN $TARGET

# cleanup if necessary and/or login
#cf a  | grep $APP_NAME && cf d $APP_NAME

if [ "$DOMAIN" == "run.pivotal.io" ]; then
    APPLICATION_DOMAIN=cfapps.io
fi

cf api | grep ${TARGET} || cf api ${TARGET} --skip-ssl-validation
cf a | grep OK || cf login



# push the app proper to CF
cf push $APP_NAME --no-start
#cf delete-orphaned-routes

cf env $APP_NAME | grep APPLICATION_DOMAIN || cf set-env $APP_NAME APPLICATION_DOMAIN $APPLICATION_DOMAIN

cf restart $APP_NAME

# register as a service (deleting existing one if it exists)
uri=`cf apps | grep $APP_NAME | tr -s ' ' | cut -d' ' -f 6`
echo registering $uri as a service.

cf s | grep $APP_NAME &&  cf ds $APP_NAME

cf cups $APP_NAME  -p '{"uri":"http://'$uri'"}'

echo "deployed $APP_NAME to $uri. Access as a service using env var: 'vcap.services.$APP_NAME.credentials.uri'";




