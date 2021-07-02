#!/bin/bash
VERSION=$1
sed -E "s/^[#]*\s*libraryVersion=.*/libraryVersion=$VERSION/" gradle.properties >gradle.properties.tmp &&
  mv gradle.properties.tmp gradle.properties
