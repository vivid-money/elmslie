#!/bin/bash
PROPERTY="$1"
FILE="gradle.properties"
sed -En "s/^$PROPERTY=([^\n]+)$/\1/p" "$FILE"
