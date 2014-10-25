#!/bin/bash
set -e

#
# the big CloudFoundry installer
#


CLOUD_DOMAIN=${DOMAIN:-run.pivotal.io}
CLOUD_TARGET=api.${DOMAIN}

function login(){
    cf api | grep ${CLOUD_TARGET} || cf api ${CLOUD_TARGET} --skip-ssl-validation
    cf a | grep OK || cf login
}

function app_domain(){
    APP_NAME=$1
    APPLICATION_DOMAIN=`cf apps | grep $APP_NAME | tr -s ' ' | cut -d' ' -f 6 | cut -d, -f1`
    echo $APPLICATION_DOMAIN
}

function deploy_app(){
    APP_NAME=$1
    cd $APP_NAME
    mvn clean install
    cf push $APP_NAME  --no-start
    APPLICATION_DOMAIN=`app_domain $APP_NAME`
    echo determined that application_domain for $APP_NAME is $APPLICATION_DOMAIN.
    cf env $APP_NAME | grep APPLICATION_DOMAIN || cf set-env $APP_NAME APPLICATION_DOMAIN $APPLICATION_DOMAIN
    cf restart $APP_NAME
    cd ..
}

function deploy_service(){
    SVC_NAME=$1
    APPLICATION_DOMAIN=`app_domain $SVC_NAME`
    JSON='{"uri":"http://'$APPLICATION_DOMAIN'"}'
    echo creating service $JSON
    cf s | grep $SVC_NAME ||  cf cups $SVC_NAME  -p $JSON
}

function deploy_eureka() {
    EUREKA=eureka-service
    deploy_app $EUREKA
    deploy_service $EUREKA
}

function deploy_config(){
    CONFIG=config-service
    deploy_app $CONFIG
    deploy_service $CONFIG
}

#echo `app_domain eureka-service`
function reset(){
    cf d config-service
    cf d eureka-service
    cf ds config-service
    cf ds eureka-service
}

reset
deploy_eureka
deploy_config


#cf delete-orphaned-routes

#deploy_eureka


#
#if [ "$DOMAIN" == "run.pivotal.io" ]; then
#    APPLICATION_DOMAIN=cfapps.io
#fi
#
#cf api | grep ${TARGET} || cf api ${TARGET} --skip-ssl-validation
#cf a | grep OK || cf login
#
#cf d $APP_NAME
#
#
#cf push $APP_NAME --no-start
#
#
#cf restart $APP_NAME
#APP_URI=`cf apps | grep $APP_NAME | tr -s ' ' | cut -d' ' -f 6 | cut -d, -f1`
#
## find it, update it
#P='{"uri":"http://'$APP_URI'"}'
#echo $P
#cf s | grep $APP_NAME && cf uups $APP_NAME  -p $P
## find it OR create it
#cf s | grep $APP_NAME ||  cf cups $APP_NAME  -p $P
#
#
#echo "deployed $APP_NAME to $APP_URI. Access as a service using env var: 'vcap.services.$APP_NAME.credentials.uri'";
#
#
#
#

#
#function deploy_app() {
#
#    APP=$PREFIX$1
#    NAME=$1
#    [ "$1" == "stores" ] && NAME=store
#    JARPATH=$DEMO_HOME/rest-microservices-$NAME/target/*.jar
#    [ "$1" == "customersui" ] && JARPATH=$DEMO_HOME/customers-stores-ui/app.jar
#    [ "$1" == "hystrix-dashboard" -o "$1" == "turbine" ] && JARPATH=$PLATFORM_HOME/$NAME/target/*.jar
#
#    if [ ! -f $JARPATH ]; then
#        echo "No jar for deployment of $1 at: $JARPATH"
#        exit 0
#    fi
#
#    #TODO: using java8 because of temp requirement for spring-platform-bus
#    cf push $APP -m 1028m -b https://github.com/spring-io/java-buildpack -p $JARPATH --no-start
#    cf env $APP | grep SPRING_PROFILES_ACTIVE || cf set-env $APP SPRING_PROFILES_ACTIVE cloud
#    cf env $APP | grep ENCRYPT_KEY || cf set-env $APP ENCRYPT_KEY deadbeef
#    if [ "$PREFIX" != "" ]; then
#        cf env $APP | grep PREFIX || cf set-env $APP PREFIX $PREFIX
#    fi
#    if [ "$APPLICATION_DOMAIN" != "cfapps.io" ]; then
#        cf set-env $APP APPLICATION_DOMAIN $APPLICATION_DOMAIN
#    else
#        cf set-env $APP DOMAIN $APPLICATION_DOMAIN
#    fi
#
#    cf bind-service $APP ${PREFIX}configserver
#    cf bind-service $APP ${PREFIX}eureka
#    cf bind-service $APP ${PREFIX}rabbitmq
#    [ "$1" == "stores" ] &&  cf bind-service $APP ${PREFIX}mongodb
#
#    cf restart $APP
#
#}
