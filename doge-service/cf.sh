#!/bin/bash
set -e

app_name=doge-service

 cf push $app_name

# register as a service (deleting existing one if it exists)
uri=`cf apps | grep $app_name | tr -s ' ' | cut -d' ' -f 6`

echo "deployed $app_name to $uri.";


function deploy() {
    app=$1
    $app_home=$2

    cf push $app

}

#doge-mongo