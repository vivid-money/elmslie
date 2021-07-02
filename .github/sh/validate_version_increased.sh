#!/bin/bash
OLD_VERSION=$1
NEW_VERSION=$2

MIN_VERSION=$(printf "%s\n%s" "$OLD_VERSION" "$NEW_VERSION" | sort -V | head -n 1)

if [[ "$OLD_VERSION" == "$NEW_VERSION" ]]; then
  echo "::error ::Can't update to the same version"
  exit 1
fi

if [[ "$OLD_VERSION" != "$MIN_VERSION" ]]; then
  echo "::error ::Can't update to an older version. Old: $OLD_VERSION. New: $NEW_VERSION"
  exit 1
fi
