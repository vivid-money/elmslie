#!/bin/bash
TOKEN=$1
EXPECTED='^[0-9_a-zA-Z]{10,}$'

if [[ -z $TOKEN ]]; then
  echo "::error ::Github api token is not specified"
  exit 1
fi

if [[ ! $TOKEN =~ $EXPECTED ]]; then
  echo "::error ::Invalid github api token format: $TOKEN"
  exit 1
fi
