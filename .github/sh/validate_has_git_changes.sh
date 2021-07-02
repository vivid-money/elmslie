#!/bin/bash
# Condition checks that git status is empty
if [[ -z $(git status -s) ]]; then
  echo "::error ::No git changes"
  exit 1
fi