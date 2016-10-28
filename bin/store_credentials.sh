#!/bin/bash - 
set -o nounset                              # Treat unset variables as an error

BINTRAY_USERNAME=$1
BINTRAY_PASSWORD=$2
CREDENTIALS_FILE="$HOME/.bintray/.credentials"

echo "Creating $CREDENTIALS_FILE"
cat <<EOF >> $CREDENTIALS_FILE
realm = Bintray API Realm
host = api.bintray.com
user = $BINTRAY_USERNAME
password = $BINTRAY_PASSWORD
EOF
