#!/bin/bash
set -e

APP_NAME=config-service
DOMAIN=${DOMAIN:-run.pivotal.io}
TARGET=api.${DOMAIN}

echo $APPLICATION_DOMAIN $APP_NAME $DOMAIN $TARGET

# cleanup if necessary and/or login
#cf a  | grep $APP_NAME && cf d $APP_NAME


cf api | grep ${TARGET} || cf api ${TARGET} --skip-ssl-validation
cf a | grep OK || cf login

cf d $APP_NAME
cf ds $APP_NAME

# push the app proper to CF
cf push $APP_NAME --no-start
#cf delete-orphaned-routes

APP_URI=`cf apps | grep $APP_NAME | tr -s ' ' | cut -d' ' -f 6 | cut -d, -f1`
APPLICATION_DOMAIN=$APP_URI
cf env $APP_NAME | grep APPLICATION_DOMAIN || cf set-env $APP_NAME APPLICATION_DOMAIN $APP_URI

cf restart $APP_NAME

# register as a service (deleting existing one if it exists)
uri=`cf apps | grep $APP_NAME | tr -s ' ' | cut -d' ' -f 6`
echo registering $uri as a service.

#cf s | grep $APP_NAME &&  cf ds $APP_NAME


P='{"uri":"http://'$APP_URI'"}'
echo $P
#cf s | grep $APP_NAME && cf uups $APP_NAME  -p $P
# find it OR create it
cf s | grep $APP_NAME ||  cf cups $APP_NAME  -p $P

echo "deployed $APP_NAME to $APP_URI. Access as a service using env var: 'vcap.services.$APP_NAME.credentials.uri'";



