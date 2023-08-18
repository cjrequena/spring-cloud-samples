#!/bin/sh

NONE='\033[00m'
RED='\033[01;31m'
GREEN='\033[01;32m'
YELLOW='\033[01;33m'
PURPLE='\033[01;35m'
CYAN='\033[01;36m'
WHITE='\033[01;37m'
BOLD='\033[1m'
UNDERLINE='\033[4m'

echo  "${GREEN}-------------------------- ${NONE}"
echo  "${GREEN}Plan ${NONE}"
echo  "${GREEN}-------------------------- ${NONE}"

java -jar schema-registry-gitops.jar -v -r=http://localhost:8081 dump base-dump.yml
