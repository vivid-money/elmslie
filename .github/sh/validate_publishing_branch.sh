#!/bin/bash

if [[ ${GITHUB_REF##*/} == main ]]; then
  # Safe to publish from main branch
  exit 0
elif [[ ${GITHUB_REF##*/} == release* ]]; then
  # Safe to publish from branches that have manually enabled publishing
  exit 0
else
  echo "::error ::Can only release from main branch or branches that start with 'release'"
  exit 1
fi
