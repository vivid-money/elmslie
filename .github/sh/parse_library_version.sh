#!/bin/bash
parseProperty() {
  PROPERTY="$1"
  FILE="$2"
  sed -En "s/^$PROPERTY=([^\n]+)$/\1/p" "$FILE"
}
parseProperty "libraryVersion" "gradle.properties"
