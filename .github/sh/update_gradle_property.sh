#!/bin/bash
PROPERTY=$1
VALUE=$2
sed -E "s/^[#]*\s*$PROPERTY=.*/$PROPERTY=$VALUE/" gradle.properties >gradle.properties.tmp &&
  mv gradle.properties.tmp gradle.properties
