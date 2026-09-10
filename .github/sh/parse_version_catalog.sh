#!/bin/bash
PROPERTY="$1"
FILE="gradle/libs.versions.toml"
VERSION=$(sed -En "s/^$PROPERTY = \"([^\"]+)\"$/\1/p" "$FILE")

if [[ -z $VERSION ]]; then
  echo "::error ::Version '$PROPERTY' not found in $FILE"
  exit 1
fi

echo "$VERSION"
