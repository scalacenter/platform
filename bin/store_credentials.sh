#!/bin/bash - 
set -o nounset                              # Treat unset variables as an error

BINTRAY_USERNAME=$1
BINTRAY_PASSWORD=$2
BINTRAY_FOLDER="$HOME/.bintray"
CREDENTIALS_FILE="$BINTRAY_FOLDER/.credentials"

echo "Creating $CREDENTIALS_FILE"
test -d $BINTRAY_FOLDER || mkdir $BINTRAY_FOLDER
cat <<EOF >> $CREDENTIALS_FILE
realm = Bintray API Realm
host = api.bintray.com
user = $BINTRAY_USERNAME
password = $BINTRAY_PASSWORD
EOF
