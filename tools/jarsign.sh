#!/bin/sh

if [ $# -lt 2 ]; then

   echo "Usage: $0 <Keystore_password>"
   exit
fi

jarsigner -keystore /usr/java/JKS/KS1 -storepass $1  ../swip/Swip.jar swipkey
