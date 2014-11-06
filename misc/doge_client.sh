#!/bin/sh
set -e

##
## Simple client that doge-ifies images using the REST service.
##
uri=http://127.0.0.1:8089/doges/$1/photos
resp=`curl -F "file=@$2" $uri`
echo response is $resp
id=`echo $resp| head -n2 | tail -n1 | cut -f2 -d: |cut -f2 -d\" `
echo id is $id
uri=$uri/$id/photo
echo result from $uri 
wget $uri
