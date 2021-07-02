#!/bin/bash
if [[ ${GITHUB_REF##*/} != "main" ]]; then
  echo "::error ::Can only release from main branch"
  exit 1
fi
