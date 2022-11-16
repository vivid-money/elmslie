#!/bin/bash
VERSION=$1
EXPECTED='^([0-9]+){1,3}\.([0-9]+){1,9}\.([0-9]+){1,9}(-(alpha|beta|rc)[0-9]{2})?$'
if [[ ! $VERSION =~ $EXPECTED ]]; then
  echo "::error ::Invalid version format: $VERSION"
  exit 1
fi
